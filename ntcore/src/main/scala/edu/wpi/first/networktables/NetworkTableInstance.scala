/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2017-2018. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.networktables

import java.util
//import java.util.concurrent.ConcurrentHashMap
//import java.util.concurrent.ConcurrentMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock
import java.util.function.Consumer


/**
  * NetworkTables Instance.
  *
  * Instances are completely independent from each other.  Table operations on
  * one instance will not be visible to other instances unless the instances are
  * connected via the network.  The main limitation on instances is that you
  * cannot have two servers on the same network port.  The main utility of
  * instances is for unit testing, but they can also enable one program to
  * connect to two different NetworkTables networks.
  *
  * The global "default" instance (as returned by {@link #getDefault()}) is
  * always available, and is intended for the common case when there is only
  * a single NetworkTables instance being used in the program.
  *
  * Additional instances can be created with the {@link #create()} function.
  * A reference must be kept to the NetworkTableInstance returned by this
  * function to keep it from being garbage collected.
  */
object NetworkTableInstance {
  /**
    * Client/server mode flag values (as returned by {@link #getNetworkMode()}).
    * This is a bitmask.
    */
  val kNetModeNone = 0x00
  val kNetModeServer = 0x01
  val kNetModeClient = 0x02
  val kNetModeStarting = 0x04
  val kNetModeFailure = 0x08
  /**
    * The default port that network tables operates on.
    */
  val kDefaultPort = 1735
  /* The default instance. */ private var s_defaultInstance: NetworkTableInstance = null

  /**
    * Get global default instance.
    *
    * @return Global default instance
    */
  def getDefault(): NetworkTableInstance = {
    if (s_defaultInstance == null) s_defaultInstance = new NetworkTableInstance(NetworkTablesJNI.getDefaultInstance)
    s_defaultInstance
  }

  /**
    * Create an instance.
    * Note: A reference to the returned instance must be retained to ensure the
    * instance is not garbage collected.
    *
    * @return Newly created instance
    */
  def create(): NetworkTableInstance = {
    val inst = new NetworkTableInstance(NetworkTablesJNI.createInstance)
    inst.m_owned = true
    inst
  }

  private class EntryConsumer[T] private[networktables](val entry: NetworkTableEntry, val consumer: Consumer[T]) {
  }

  private val rev0def = Array[Byte](0)
}

/**
  * Construct from native handle.
  *
  * @param handle Native handle
  */
final class NetworkTableInstance private(var m_handle: Int) { self =>
  /**
    * Destroys the instance (if created by {@link #create()}).
    */
  def free(): Unit = {
    if (m_owned && m_handle != 0) NetworkTablesJNI.destroyInstance(m_handle)
  }

  /**
    * Determines if the native handle is valid.
    *
    * @return True if the native handle is valid, false otherwise.
    */
  def isValid: Boolean = m_handle != 0

  /**
    * Gets the native handle for the entry.
    *
    * @return Native handle
    */
  def getHandle: Int = m_handle

  /**
    * Gets the entry for a key.
    *
    * @param name Key
    * @return Network table entry.
    */
  def getEntry(name: String) = new NetworkTableEntry(this, NetworkTablesJNI.getEntry(m_handle, name))

  /**
    * Get entries starting with the given prefix.
    * The results are optionally filtered by string prefix and entry type to
    * only return a subset of all entries.
    *
    * @param prefix entry name required prefix; only entries whose name
    *               starts with this string are returned
    * @param types  bitmask of types; 0 is treated as a "don't care"
    * @return Array of entries.
    */
  def getEntries(prefix: String, types: Int): Array[NetworkTableEntry] = {
    val handles = NetworkTablesJNI.getEntries(m_handle, prefix, types)
    val entries = new Array[NetworkTableEntry](handles.length)
    var i = 0
    while ( {
      i < handles.length
    }) {
      entries(i) = new NetworkTableEntry(this, handles(i))

      {
        i += 1; i - 1
      }
    }
    entries
  }

  /**
    * Get information about entries starting with the given prefix.
    * The results are optionally filtered by string prefix and entry type to
    * only return a subset of all entries.
    *
    * @param prefix entry name required prefix; only entries whose name
    *               starts with this string are returned
    * @param types  bitmask of types; 0 is treated as a "don't care"
    * @return Array of entry information.
    */
  def getEntryInfo(prefix: String, types: Int): Array[EntryInfo] = NetworkTablesJNI.getEntryInfo(this, m_handle, prefix, types)

  /* Cache of created tables. */ final private val m_tables = new util.HashMap[String, NetworkTable]

  /**
    * Gets the table with the specified key.
    *
    * @param key the key name
    * @return The network table
    */
  def getTable(key: String): NetworkTable = { // prepend leading / if not present
    var theKey: String = null
    if (key.isEmpty || key == "/") theKey = ""
    else if (key.charAt(0) == NetworkTable.PATH_SEPARATOR) theKey = key
    else theKey = NetworkTable.PATH_SEPARATOR + key
    // cache created tables
    var table = m_tables.get(theKey)
    if (table == null) {
      table = new NetworkTable(this, theKey)
      val oldTable = if (!m_tables.containsKey(theKey)) {
        m_tables.put(theKey, table)
        table
      } else m_tables.get(theKey)
      if (oldTable != null) table = oldTable
    }
    table
  }

  /**
    * Deletes ALL keys in ALL subtables (except persistent values).
    * Use with caution!
    */
  def deleteAllEntries(): Unit = {
    NetworkTablesJNI.deleteAllEntries(m_handle)
  }

  final private val m_entryListenerLock = new ReentrantLock
  final private val m_entryListeners = new util.HashMap[Integer, NetworkTableInstance.EntryConsumer[EntryNotification]]
  private var m_entryListenerThread: Thread = null
  private var m_entryListenerPoller = 0
  private var m_entryListenerWaitQueue = false
  final private val m_entryListenerWaitQueueCond = m_entryListenerLock.newCondition

  private def startEntryListenerThread(): Unit = {
    m_entryListenerThread = new Thread(new Runnable {
      def run() = {
        var wasInterrupted = false
        while ( {
          !Thread.interrupted
        }) {
          var events: Array[EntryNotification] = null
          try
            events = NetworkTablesJNI.pollEntryListener(self, m_entryListenerPoller)
          catch {
            case ex: InterruptedException =>
              m_entryListenerLock.lock()
              try {
                if (m_entryListenerWaitQueue) {
                  m_entryListenerWaitQueue = false
                  m_entryListenerWaitQueueCond.signalAll()
                }
              } finally m_entryListenerLock.unlock()
              Thread.currentThread.interrupt()
              wasInterrupted = true // don't try to destroy poller, as its handle is likely no longer valid
          }
          for (event <- events) {
            var listener: NetworkTableInstance.EntryConsumer[EntryNotification] = null
            m_entryListenerLock.lock()
            try
              listener = m_entryListeners.get(event.listener)
            finally m_entryListenerLock.unlock()
            if (listener != null) {
              event.entryObject = listener.entry
              try
                listener.consumer.accept(event)
              catch {
                case throwable: Throwable =>
                  System.err.println("Unhandled exception during entry listener callback: " + throwable.toString)
                  throwable.printStackTrace()
              }
            }
          }
        }
        m_entryListenerLock.lock()
        try {
          if (!wasInterrupted) NetworkTablesJNI.destroyEntryListenerPoller(m_entryListenerPoller)
          m_entryListenerPoller = 0
        } finally m_entryListenerLock.unlock()
      }
    }, "NTEntryListener")
    m_entryListenerThread.setDaemon(true)
    m_entryListenerThread.start()
  }

  /**
    * Add a listener for all entries starting with a certain prefix.
    *
    * @param prefix   UTF-8 string prefix
    * @param listener listener to add
    * @param flags    { @link EntryListenerFlags} bitmask
    * @return Listener handle
    */
  def addEntryListener(prefix: String, listener: Consumer[EntryNotification], flags: Int): Int = {
    m_entryListenerLock.lock()
    try {
      if (m_entryListenerPoller == 0) {
        m_entryListenerPoller = NetworkTablesJNI.createEntryListenerPoller(m_handle)
        startEntryListenerThread()
      }
      val handle = NetworkTablesJNI.addPolledEntryListener(m_entryListenerPoller, prefix, flags)
      m_entryListeners.put(handle, new NetworkTableInstance.EntryConsumer[EntryNotification](null, listener))
      handle
    } finally m_entryListenerLock.unlock()
  }

  /**
    * Add a listener for a particular entry.
    *
    * @param entry    the entry
    * @param listener listener to add
    * @param flags    { @link EntryListenerFlags} bitmask
    * @return Listener handle
    */
  def addEntryListener(entry: NetworkTableEntry, listener: Consumer[EntryNotification], flags: Int): Int = {
    if (!(this == entry.getInstance)) throw new IllegalArgumentException("entry does not belong to this instance")
    m_entryListenerLock.lock()
    try {
      if (m_entryListenerPoller == 0) {
        m_entryListenerPoller = NetworkTablesJNI.createEntryListenerPoller(m_handle)
        startEntryListenerThread()
      }
      val handle = NetworkTablesJNI.addPolledEntryListener(m_entryListenerPoller, entry.getHandle, flags)
      m_entryListeners.put(handle, new NetworkTableInstance.EntryConsumer[EntryNotification](entry, listener))
      handle
    } finally m_entryListenerLock.unlock()
  }

  /**
    * Remove an entry listener.
    *
    * @param listener Listener handle to remove
    */
  def removeEntryListener(listener: Int): Unit = {
    NetworkTablesJNI.removeEntryListener(listener)
  }

  /**
    * Wait for the entry listener queue to be empty.  This is primarily useful
    * for deterministic testing.  This blocks until either the entry listener
    * queue is empty (e.g. there are no more events that need to be passed along
    * to callbacks or poll queues) or the timeout expires.
    *
    * @param timeout timeout, in seconds.  Set to 0 for non-blocking behavior,
    *                or a negative value to block indefinitely
    * @return False if timed out, otherwise true.
    */
  def waitForEntryListenerQueue(timeout: Double): Boolean = {
    if (!NetworkTablesJNI.waitForEntryListenerQueue(m_handle, timeout)) return false
    m_entryListenerLock.lock()
    try
        if (m_entryListenerPoller != 0) {
          m_entryListenerWaitQueue = true
          NetworkTablesJNI.cancelPollEntryListener(m_entryListenerPoller)
          while ( {
            m_entryListenerWaitQueue
          }) try
              if (timeout < 0) m_entryListenerWaitQueueCond.await()
              else return m_entryListenerWaitQueueCond.await((timeout * 1e9).toLong, TimeUnit.NANOSECONDS)
          catch {
            case ex: InterruptedException =>
              Thread.currentThread.interrupt()
              return true
          }
        }
    finally m_entryListenerLock.unlock()
    true
  }

  final private val m_connectionListenerLock = new ReentrantLock
  final private val m_connectionListeners = new util.HashMap[Integer, Consumer[ConnectionNotification]]
  private var m_connectionListenerThread: Thread = null
  private var m_connectionListenerPoller = 0
  private var m_connectionListenerWaitQueue = false
  final private val m_connectionListenerWaitQueueCond = m_connectionListenerLock.newCondition

  private def startConnectionListenerThread(): Unit = {
    m_connectionListenerThread = new Thread(new Runnable {
      def run() = {
        var wasInterrupted = false
        while ( {
          !Thread.interrupted
        }) {
          var events: Array[ConnectionNotification] = null
          try
            events = NetworkTablesJNI.pollConnectionListener(self, m_connectionListenerPoller)
          catch {
            case ex: InterruptedException =>
              m_connectionListenerLock.lock()
              try {
                if (m_connectionListenerWaitQueue) {
                  m_connectionListenerWaitQueue = false
                  m_connectionListenerWaitQueueCond.signalAll()
                }
              } finally m_connectionListenerLock.unlock()
              Thread.currentThread.interrupt()
              wasInterrupted = true
          }

          for (event <- events) {
            var listener: Consumer[ConnectionNotification] = null
            m_connectionListenerLock.lock()
            try
              listener = m_connectionListeners.get(event.listener)
            finally m_connectionListenerLock.unlock()
            if (listener != null) try
              listener.accept(event)
            catch {
              case throwable: Throwable =>
                System.err.println("Unhandled exception during connection listener callback: " + throwable.toString)
                throwable.printStackTrace()
            }
          }
        }
        m_connectionListenerLock.lock()
        try {
          if (!wasInterrupted) NetworkTablesJNI.destroyConnectionListenerPoller(m_connectionListenerPoller)
          m_connectionListenerPoller = 0
        } finally m_connectionListenerLock.unlock()
      }
    }, "NTConnectionListener")
    m_connectionListenerThread.setDaemon(true)
    m_connectionListenerThread.start()
  }

  /**
    * Add a connection listener.
    *
    * @param listener        Listener to add
    * @param immediateNotify Notify listener of all existing connections
    * @return Listener handle
    */
  def addConnectionListener(listener: Consumer[ConnectionNotification], immediateNotify: Boolean): Int = {
    m_connectionListenerLock.lock()
    try {
      if (m_connectionListenerPoller == 0) {
        m_connectionListenerPoller = NetworkTablesJNI.createConnectionListenerPoller(m_handle)
        startConnectionListenerThread()
      }
      val handle = NetworkTablesJNI.addPolledConnectionListener(m_connectionListenerPoller, immediateNotify)
      m_connectionListeners.put(handle, listener)
      handle
    } finally m_connectionListenerLock.unlock()
  }

  /**
    * Remove a connection listener.
    *
    * @param listener Listener handle to remove
    */
  def removeConnectionListener(listener: Int): Unit = {
    m_connectionListenerLock.lock()
    try
      m_connectionListeners.remove(listener)
    finally m_connectionListenerLock.unlock()
    NetworkTablesJNI.removeConnectionListener(listener)
  }

  /**
    * Wait for the connection listener queue to be empty.  This is primarily useful
    * for deterministic testing.  This blocks until either the connection listener
    * queue is empty (e.g. there are no more events that need to be passed along
    * to callbacks or poll queues) or the timeout expires.
    *
    * @param timeout timeout, in seconds.  Set to 0 for non-blocking behavior,
    *                or a negative value to block indefinitely
    * @return False if timed out, otherwise true.
    */
  def waitForConnectionListenerQueue(timeout: Double): Boolean = {
    if (!NetworkTablesJNI.waitForConnectionListenerQueue(m_handle, timeout)) return false
    m_connectionListenerLock.lock()
    try
        if (m_connectionListenerPoller != 0) {
          m_connectionListenerWaitQueue = true
          NetworkTablesJNI.cancelPollConnectionListener(m_connectionListenerPoller)
          while ( {
            m_connectionListenerWaitQueue
          }) try
              if (timeout < 0) m_connectionListenerWaitQueueCond.await()
              else return m_connectionListenerWaitQueueCond.await((timeout * 1e9).toLong, TimeUnit.NANOSECONDS)
          catch {
            case ex: InterruptedException =>
              Thread.currentThread.interrupt()
              return true
          }
        }
    finally m_connectionListenerLock.unlock()
    true
  }

  final private val m_rpcCallLock = new ReentrantLock
  final private val m_rpcCalls = new util.HashMap[Integer, NetworkTableInstance.EntryConsumer[RpcAnswer]]
  private var m_rpcCallThread: Thread = null
  private var m_rpcCallPoller = 0
  private var m_rpcCallWaitQueue = false
  final private val m_rpcCallWaitQueueCond = m_rpcCallLock.newCondition

  private def startRpcCallThread(): Unit = {
    m_rpcCallThread = new Thread(new Runnable {
      def run() = {
        var wasInterrupted = false
        while ( {
          !Thread.interrupted
        }) {
          var events: Array[RpcAnswer] = null
          try
            events = NetworkTablesJNI.pollRpc(self, m_rpcCallPoller)
          catch {
            case ex: InterruptedException =>
              m_rpcCallLock.lock()
              try {
                if (m_rpcCallWaitQueue) {
                  m_rpcCallWaitQueue = false
                  m_rpcCallWaitQueueCond.signalAll()
                }
              } finally m_rpcCallLock.unlock()
              Thread.currentThread.interrupt()
              wasInterrupted = true
          }
          for (event <- events) {
            var listener: NetworkTableInstance.EntryConsumer[RpcAnswer] = null
            m_rpcCallLock.lock()
            try
              listener = m_rpcCalls.get(event.entry)
            finally m_rpcCallLock.unlock()
            if (listener != null) {
              event.entryObject = listener.entry
              try
                listener.consumer.accept(event)
              catch {
                case throwable: Throwable =>
                  System.err.println("Unhandled exception during RPC callback: " + throwable.toString)
                  throwable.printStackTrace()
              }
            }
          }
        }
        m_rpcCallLock.lock()
        try {
          if (!wasInterrupted) NetworkTablesJNI.destroyRpcCallPoller(m_rpcCallPoller)
          m_rpcCallPoller = 0
        } finally m_rpcCallLock.unlock()
      }
    }, "NTRpcCall")
    m_rpcCallThread.setDaemon(true)
    m_rpcCallThread.start()
  }

  /**
    * Create a callback-based RPC entry point.  Only valid to use on the server.
    * The callback function will be called when the RPC is called.
    * This function creates RPC version 0 definitions (raw data in and out).
    *
    * @param entry    the entry
    * @param callback callback function
    */
  def createRpc(entry: NetworkTableEntry, callback: Consumer[RpcAnswer]): Unit = {
    m_rpcCallLock.lock()
    try {
      if (m_rpcCallPoller == 0) {
        m_rpcCallPoller = NetworkTablesJNI.createRpcCallPoller(m_handle)
        startRpcCallThread()
      }
      NetworkTablesJNI.createPolledRpc(entry.getHandle, NetworkTableInstance.rev0def, m_rpcCallPoller)
      m_rpcCalls.put(entry.getHandle, new NetworkTableInstance.EntryConsumer[RpcAnswer](entry, callback))
    } finally m_rpcCallLock.unlock()
  }

  /**
    * Wait for the incoming RPC call queue to be empty.  This is primarily useful
    * for deterministic testing.  This blocks until either the RPC call
    * queue is empty (e.g. there are no more events that need to be passed along
    * to callbacks or poll queues) or the timeout expires.
    *
    * @param timeout timeout, in seconds.  Set to 0 for non-blocking behavior,
    *                or a negative value to block indefinitely
    * @return False if timed out, otherwise true.
    */
  def waitForRpcCallQueue(timeout: Double): Boolean = {
    if (!NetworkTablesJNI.waitForRpcCallQueue(m_handle, timeout)) return false
    m_rpcCallLock.lock()
    try
        if (m_rpcCallPoller != 0) {
          m_rpcCallWaitQueue = true
          NetworkTablesJNI.cancelPollRpc(m_rpcCallPoller)
          while ( {
            m_rpcCallWaitQueue
          }) try
              if (timeout < 0) m_rpcCallWaitQueueCond.await()
              else return m_rpcCallWaitQueueCond.await((timeout * 1e9).toLong, TimeUnit.NANOSECONDS)
          catch {
            case ex: InterruptedException =>
              Thread.currentThread.interrupt()
              return true
          }
        }
    finally m_rpcCallLock.unlock()
    true
  }

  /**
    * Set the network identity of this node.
    * This is the name used during the initial connection handshake, and is
    * visible through ConnectionInfo on the remote node.
    *
    * @param name identity to advertise
    */
  def setNetworkIdentity(name: String): Unit = {
    NetworkTablesJNI.setNetworkIdentity(m_handle, name)
  }

  /**
    * Get the current network mode.
    *
    * @return Bitmask of NetworkMode.
    */
  def getNetworkMode: Int = NetworkTablesJNI.getNetworkMode(m_handle)

  /**
    * Starts a server using the networktables.ini as the persistent file,
    * using the default listening address and port.
    */
  def startServer(): Unit = {
    startServer("networktables.ini")
  }

  /**
    * Starts a server using the specified persistent filename, using the default
    * listening address and port.
    *
    * @param persistFilename the name of the persist file to use
    */
  def startServer(persistFilename: String): Unit = {
    startServer(persistFilename, "")
  }

  /**
    * Starts a server using the specified filename and listening address,
    * using the default port.
    *
    * @param persistFilename the name of the persist file to use
    * @param listenAddress   the address to listen on, or empty to listen on any
    *                        address
    */
  def startServer(persistFilename: String, listenAddress: String): Unit = {
    startServer(persistFilename, listenAddress, NetworkTableInstance.kDefaultPort)
  }

  /**
    * Starts a server using the specified filename, listening address, and port.
    *
    * @param persistFilename the name of the persist file to use
    * @param listenAddress   the address to listen on, or empty to listen on any
    *                        address
    * @param port            port to communicate over
    */
  def startServer(persistFilename: String, listenAddress: String, port: Int): Unit = {
    NetworkTablesJNI.startServer(m_handle, persistFilename, listenAddress, port)
  }

  /**
    * Stops the server if it is running.
    */
  def stopServer(): Unit = {
    NetworkTablesJNI.stopServer(m_handle)
  }

  /**
    * Starts a client.  Use SetServer to set the server name and port.
    */
  def startClient(): Unit = {
    NetworkTablesJNI.startClient(m_handle)
  }

  /**
    * Starts a client using the specified server and the default port
    *
    * @param serverName server name
    */
  def startClient(serverName: String): Unit = {
    startClient(serverName, NetworkTableInstance.kDefaultPort)
  }

  /**
    * Starts a client using the specified server and port
    *
    * @param serverName server name
    * @param port       port to communicate over
    */
  def startClient(serverName: String, port: Int): Unit = {
    NetworkTablesJNI.startClient(m_handle, serverName, port)
  }

  /**
    * Starts a client using the specified servers and default port.  The
    * client will attempt to connect to each server in round robin fashion.
    *
    * @param serverNames array of server names
    */
  def startClient(serverNames: Array[String]): Unit = {
    startClient(serverNames, NetworkTableInstance.kDefaultPort)
  }

  /**
    * Starts a client using the specified servers and port number.  The
    * client will attempt to connect to each server in round robin fashion.
    *
    * @param serverNames array of server names
    * @param port        port to communicate over
    */
  def startClient(serverNames: Array[String], port: Int): Unit = {
    val ports = new Array[Int](serverNames.length)
    var i = 0
    while ( {
      i < serverNames.length
    }) {
      ports(i) = port

      {
        i += 1; i - 1
      }
    }
    startClient(serverNames, ports)
  }

  /**
    * Starts a client using the specified (server, port) combinations.  The
    * client will attempt to connect to each server in round robin fashion.
    *
    * @param serverNames array of server names
    * @param ports       array of port numbers
    */
  def startClient(serverNames: Array[String], ports: Array[Int]): Unit = {
    NetworkTablesJNI.startClient(m_handle, serverNames, ports)
  }

  /**
    * Starts a client using commonly known robot addresses for the specified
    * team using the default port number.
    *
    * @param team team number
    */
  def startClientTeam(team: Int): Unit = {
    startClientTeam(team, NetworkTableInstance.kDefaultPort)
  }

  /**
    * Starts a client using commonly known robot addresses for the specified
    * team.
    *
    * @param team team number
    * @param port port to communicate over
    */
  def startClientTeam(team: Int, port: Int): Unit = {
    NetworkTablesJNI.startClientTeam(m_handle, team, port)
  }

  /**
    * Stops the client if it is running.
    */
  def stopClient(): Unit = {
    NetworkTablesJNI.stopClient(m_handle)
  }

  /**
    * Sets server address and port for client (without restarting client).
    * Changes the port to the default port.
    *
    * @param serverName server name
    */
  def setServer(serverName: String): Unit = {
    setServer(serverName, NetworkTableInstance.kDefaultPort)
  }

  /**
    * Sets server address and port for client (without restarting client).
    *
    * @param serverName server name
    * @param port       port to communicate over
    */
  def setServer(serverName: String, port: Int): Unit = {
    NetworkTablesJNI.setServer(m_handle, serverName, port)
  }

  /**
    * Sets server addresses and port for client (without restarting client).
    * Changes the port to the default port.  The client will attempt to connect
    * to each server in round robin fashion.
    *
    * @param serverNames array of server names
    */
  def setServer(serverNames: Array[String]): Unit = {
    setServer(serverNames, NetworkTableInstance.kDefaultPort)
  }

  /**
    * Sets server addresses and port for client (without restarting client).
    * The client will attempt to connect to each server in round robin fashion.
    *
    * @param serverNames array of server names
    * @param port        port to communicate over
    */
  def setServer(serverNames: Array[String], port: Int): Unit = {
    val ports = new Array[Int](serverNames.length)
    var i = 0
    while ( {
      i < serverNames.length
    }) {
      ports(i) = port

      {
        i += 1; i - 1
      }
    }
    setServer(serverNames, ports)
  }

  /**
    * Sets server addresses and ports for client (without restarting client).
    * The client will attempt to connect to each server in round robin fashion.
    *
    * @param serverNames array of server names
    * @param ports       array of port numbers
    */
  def setServer(serverNames: Array[String], ports: Array[Int]): Unit = {
    NetworkTablesJNI.setServer(m_handle, serverNames, ports)
  }

  /**
    * Sets server addresses and port for client (without restarting client).
    * Changes the port to the default port.  The client will attempt to connect
    * to each server in round robin fashion.
    *
    * @param team team number
    */
  def setServerTeam(team: Int): Unit = {
    setServerTeam(team, NetworkTableInstance.kDefaultPort)
  }

  /**
    * Sets server addresses and port for client (without restarting client).
    * Connects using commonly known robot addresses for the specified team.
    *
    * @param team team number
    * @param port port to communicate over
    */
  def setServerTeam(team: Int, port: Int): Unit = {
    NetworkTablesJNI.setServerTeam(m_handle, team, port)
  }

  /**
    * Starts requesting server address from Driver Station.
    * This connects to the Driver Station running on localhost to obtain the
    * server IP address, and connects with the default port.
    */
  def startDSClient(): Unit = {
    startDSClient(NetworkTableInstance.kDefaultPort)
  }

  /**
    * Starts requesting server address from Driver Station.
    * This connects to the Driver Station running on localhost to obtain the
    * server IP address.
    *
    * @param port server port to use in combination with IP from DS
    */
  def startDSClient(port: Int): Unit = {
    NetworkTablesJNI.startDSClient(m_handle, port)
  }

  /**
    * Stops requesting server address from Driver Station.
    */
  def stopDSClient(): Unit = {
    NetworkTablesJNI.stopDSClient(m_handle)
  }

  /**
    * Set the periodic update rate.
    * Sets how frequently updates are sent to other nodes over the network.
    *
    * @param interval update interval in seconds (range 0.01 to 1.0)
    */
  def setUpdateRate(interval: Double): Unit = {
    NetworkTablesJNI.setUpdateRate(m_handle, interval)
  }

  /**
    * Flushes all updated values immediately to the network.
    * Note: This is rate-limited to protect the network from flooding.
    * This is primarily useful for synchronizing network updates with
    * user code.
    */
  def flush(): Unit = {
    NetworkTablesJNI.flush(m_handle)
  }

  /**
    * Gets information on the currently established network connections.
    * If operating as a client, this will return either zero or one values.
    *
    * @return array of connection information
    */
  def getConnections: Array[ConnectionInfo] = NetworkTablesJNI.getConnections(m_handle)

  /**
    * Return whether or not the instance is connected to another node.
    *
    * @return True if connected.
    */
  def isConnected: Boolean = NetworkTablesJNI.isConnected(m_handle)

  /**
    * Saves persistent keys to a file.  The server does this automatically.
    *
    * @param filename file name
    * @throws PersistentException if error saving file
    */
  @throws[PersistentException]
  def savePersistent(filename: String): Unit = {
    NetworkTablesJNI.savePersistent(m_handle, filename)
  }

  /**
    * Loads persistent keys from a file.  The server does this automatically.
    *
    * @param filename file name
    * @return List of warnings (errors result in an exception instead)
    * @throws PersistentException if error reading file
    */
  @throws[PersistentException]
  def loadPersistent(filename: String): Array[String] = NetworkTablesJNI.loadPersistent(m_handle, filename)

  /**
    * Save table values to a file.  The file format used is identical to
    * that used for SavePersistent.
    *
    * @param filename filename
    * @param prefix   save only keys starting with this prefix
    * @throws PersistentException if error saving file
    */
  @throws[PersistentException]
  def saveEntries(filename: String, prefix: String): Unit = {
    NetworkTablesJNI.saveEntries(m_handle, filename, prefix)
  }

  /**
    * Load table values from a file.  The file format used is identical to
    * that used for SavePersistent / LoadPersistent.
    *
    * @param filename filename
    * @param prefix   load only keys starting with this prefix
    * @return List of warnings (errors result in an exception instead)
    * @throws PersistentException if error saving file
    */
  @throws[PersistentException]
  def loadEntries(filename: String, prefix: String): Array[String] = NetworkTablesJNI.loadEntries(m_handle, filename, prefix)

  final private val m_loggerLock = new ReentrantLock
  final private val m_loggers = new util.HashMap[Integer, Consumer[LogMessage]]
  private var m_loggerThread: Thread = null
  private var m_loggerPoller = 0
  private var m_loggerWaitQueue = false
  final private val m_loggerWaitQueueCond = m_loggerLock.newCondition

  private def startLogThread(): Unit = {
    m_loggerThread = new Thread(new Runnable {
      def run() = {
        var wasInterrupted = false
        while ( {
          !Thread.interrupted
        }) {
          var events: Array[LogMessage] = null
          try
            events = NetworkTablesJNI.pollLogger(self, m_loggerPoller)
          catch {
            case ex: InterruptedException =>
              Thread.currentThread.interrupt()
              wasInterrupted = true
          }
          for (event <- events) {
            var logger: Consumer[LogMessage] = null
            m_loggerLock.lock()
            try
              logger = m_loggers.get(event.logger)
            finally m_loggerLock.unlock()
            if (logger != null) try
              logger.accept(event)
            catch {
              case throwable: Throwable =>
                System.err.println("Unhandled exception during logger callback: " + throwable.toString)
                throwable.printStackTrace()
            }
          }
        }
        m_loggerLock.lock()
        try {
          if (!wasInterrupted) NetworkTablesJNI.destroyLoggerPoller(m_loggerPoller)
          m_rpcCallPoller = 0
        } finally m_loggerLock.unlock()
      }
    }, "NTLogger")
    m_loggerThread.setDaemon(true)
    m_loggerThread.start()
  }

  /**
    * Add logger callback function.  By default, log messages are sent to stderr;
    * this function sends log messages with the specified levels to the provided
    * callback function instead.  The callback function will only be called for
    * log messages with level greater than or equal to minLevel and less than or
    * equal to maxLevel; messages outside this range will be silently ignored.
    *
    * @param func     log callback function
    * @param minLevel minimum log level
    * @param maxLevel maximum log level
    * @return Logger handle
    */
  def addLogger(func: Consumer[LogMessage], minLevel: Int, maxLevel: Int): Int = {
    m_loggerLock.lock()
    try {
      if (m_loggerPoller == 0) {
        m_loggerPoller = NetworkTablesJNI.createLoggerPoller(m_handle)
        startLogThread()
      }
      val handle = NetworkTablesJNI.addPolledLogger(m_loggerPoller, minLevel, maxLevel)
      m_loggers.put(handle, func)
      handle
    } finally m_loggerLock.unlock()
  }

  /**
    * Remove a logger.
    *
    * @param logger Logger handle to remove
    */
  def removeLogger(logger: Int): Unit = {
    m_loggerLock.lock()
    try
      m_loggers.remove(logger)
    finally m_loggerLock.unlock()
    NetworkTablesJNI.removeLogger(logger)
  }

  /**
    * Wait for the incoming log event queue to be empty.  This is primarily useful
    * for deterministic testing.  This blocks until either the log event
    * queue is empty (e.g. there are no more events that need to be passed along
    * to callbacks or poll queues) or the timeout expires.
    *
    * @param timeout timeout, in seconds.  Set to 0 for non-blocking behavior,
    *                or a negative value to block indefinitely
    * @return False if timed out, otherwise true.
    */
  def waitForLoggerQueue(timeout: Double): Boolean = {
    if (!NetworkTablesJNI.waitForLoggerQueue(m_handle, timeout)) return false
    m_loggerLock.lock()
    try {
      if (m_loggerPoller != 0) {
        m_loggerWaitQueue = true
        NetworkTablesJNI.cancelPollLogger(m_loggerPoller)
        while ( {
          m_loggerWaitQueue
        }) try
            if (timeout < 0) m_loggerWaitQueueCond.await()
            else return m_loggerWaitQueueCond.await((timeout * 1e9).toLong, TimeUnit.NANOSECONDS)
        catch {
          case ex: InterruptedException =>
            Thread.currentThread.interrupt()
            return true
        }
      }
    } finally m_loggerLock.unlock()
    true
  }

  override def equals(o: Any): Boolean = {
    if (o.asInstanceOf[AnyRef] eq this) return true
    if (!o.isInstanceOf[NetworkTableInstance]) return false
    val other = o.asInstanceOf[NetworkTableInstance]
    m_handle == other.m_handle
  }

  override def hashCode: Int = m_handle

  private var m_owned = false
}
