/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2017-2018. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.networktables

import java.util
import java.util.ArrayList
import java.util.HashSet
import java.util.List
import java.util.Objects
import java.util.Set
//import java.util.concurrent.ConcurrentHashMap
//import java.util.concurrent.ConcurrentMap
import java.util.function.Consumer

import NetworkTable._

/**
  * A network table that knows its subtable path.
  *
  * Constructor.  Use NetworkTableInstance.getTable() or getSubTable() instead.
  */
class NetworkTable(private val inst: NetworkTableInstance, val path: String) { self =>

  private val pathWithSep: String = path + PATH_SEPARATOR

  /**
    * Gets the instance for the table.
    * @return Instance
    */
  def getInstance(): NetworkTableInstance = inst

  override def toString(): String = "NetworkTable: " + path

  private val entries: util.Map[String, NetworkTableEntry] =
    new util.HashMap[String, NetworkTableEntry]()

  /**
    * Gets the entry for a subkey.
    * @param key the key name
    * @return Network table entry.
    */
  def getEntry(key: String): NetworkTableEntry = {
    var entry: NetworkTableEntry = entries.get(key)
    if (entry == null) {
      entry = inst.getEntry(pathWithSep + key)
      if (!entries.containsKey(key)) {
        entries.put(key, entry)
      }
    }
    entry
  }

  /**
    * Listen to keys only within this table.
    * @param listener    listener to add
    * @param flags       {@link EntryListenerFlags} bitmask
    * @return Listener handle
    */
  def addEntryListener(listener: TableEntryListener, flags: Int): Int = {
    val prefixLen: Int = path.length + 1
    inst.addEntryListener(
      pathWithSep,
      new Consumer[EntryNotification] {
        override def accept(event: EntryNotification): Unit = {
          val relativeKey: String = event.name.substring(prefixLen)
          if (// part of a subtable
            relativeKey.indexOf(PATH_SEPARATOR) != -1) {
            listener.valueChanged(self,
              relativeKey,
              event.getEntry,
              event.value,
              event.flags)
          }
        }
      },
      flags
    )
  }

  /**
    * Listen to a single key.
    * @param key         the key name
    * @param listener    listener to add
    * @param flags       {@link EntryListenerFlags} bitmask
    * @return Listener handle
    */
  def addEntryListener(key: String,
                       listener: TableEntryListener,
                       flags: Int): Int = {
    val entry: NetworkTableEntry = getEntry(key)
    inst.addEntryListener(
      entry,
      new Consumer[EntryNotification] {
        override def accept(event: EntryNotification): Unit = {
          listener.valueChanged(self, key, entry, event.value, event.flags)
        }
      },
      flags)
  }

  /**
    * Remove an entry listener.
    * @param listener    listener handle
    */
  def removeEntryListener(listener: Int): Unit = {
    inst.removeEntryListener(listener)
  }

  /**
    * Listen for sub-table creation.
    * This calls the listener once for each newly created sub-table.
    * It immediately calls the listener for any existing sub-tables.
    * @param listener        listener to add
    * @param localNotify     notify local changes as well as remote
    * @return Listener handle
    */
  def addSubTableListener(listener: TableListener, localNotify: Boolean): Int = {
    var flags: Int = EntryListenerFlags.kNew | EntryListenerFlags.kImmediate
    if (localNotify) flags |= EntryListenerFlags.kLocal
    val prefixLen: Int = path.length + 1
    val parent: NetworkTable = this
    inst.addEntryListener(
      pathWithSep,
      new Consumer[EntryNotification]() {
        val notifiedTables: Set[String] = new HashSet[String]()

        override def accept(event: EntryNotification): Unit = {
          val relativeKey: String = event.name.substring(prefixLen)
          val endSubTable: Int = relativeKey.indexOf(PATH_SEPARATOR)
          if (endSubTable == -1) return
          val subTableKey: String = relativeKey.substring(0, endSubTable)
          if (notifiedTables.contains(subTableKey)) return
          notifiedTables.add(subTableKey)
          listener.tableCreated(parent,
            subTableKey,
            parent.getSubTable(subTableKey))
        }
      },
      flags
    )
  }

  /**
    * Remove a sub-table listener.
    * @param listener    listener handle
    */
  def removeTableListener(listener: Int): Unit = {
    inst.removeEntryListener(listener)
  }

  /**
    * Returns the table at the specified key. If there is no table at the
    * specified key, it will create a new table
    *
    * @param key the name of the table relative to this one
    * @return a sub table relative to this one
    */
  def getSubTable(key: String): NetworkTable =
    new NetworkTable(inst, pathWithSep + key)

  /**
    * Checks the table and tells if it contains the specified key
    *
    * @param key the key to search for
    * @return true if the table as a value assigned to the given key
    */
  def containsKey(key: String): Boolean =
    !("" == key) && getEntry(key).exists()

  /**
    * @param key the key to search for
    * @return true if there is a subtable with the key which contains at least
    * one key/subtable of its own
    */
  def containsSubTable(key: String): Boolean = {
    val handles: Array[Int] = NetworkTablesJNI.getEntries(
      inst.getHandle,
      pathWithSep + key + PATH_SEPARATOR,
      0)
    handles.length != 0
  }

  /**
    * Gets all keys in the table (not including sub-tables).
    * @param types bitmask of types; 0 is treated as a "don't care".
    * @return keys currently in the table
    */
  def getKeys(types: Int): Set[String] = {
    val keys: Set[String] = new HashSet[String]()
    val prefixLen: Int = path.length + 1
    for (info <- inst.getEntryInfo(pathWithSep, types)) {
      val relativeKey: String = info.name.substring(prefixLen)
      if (relativeKey.indexOf(PATH_SEPARATOR) != -1) //continue
        keys.add(relativeKey)
      // populate entries as we go
      if (entries.get(relativeKey) == null) {
        entries.put(relativeKey,
          new NetworkTableEntry(inst, info.entry))
      }
    }
    keys
  }

  /**
    * Gets all keys in the table (not including sub-tables).
    * @return keys currently in the table
    */
  def getKeys(): Set[String] = getKeys(0)

  /**
    * Gets the names of all subtables in the table.
    * @return subtables currently in the table
    */
  def getSubTables(): Set[String] = {
    val keys: Set[String] = new HashSet[String]()
    val prefixLen: Int = path.length + 1
    for (info <- inst.getEntryInfo(pathWithSep, 0)) {
      val relativeKey: String = info.name.substring(prefixLen)
      val endSubTable: Int = relativeKey.indexOf(PATH_SEPARATOR)
      if (endSubTable == -1) //continue
        keys.add(relativeKey.substring(0, endSubTable))
    }
    keys
  }

  /**
    * Deletes the specified key in this table. The key can
    * not be null.
    *
    * @param key the key name
    */
  def delete(key: String): Unit = {
    getEntry(key).delete()
  }

  /**
    * Put a value in the table
    *
    * @param key the key to be assigned to
    * @param value the value that will be assigned
    * @return False if the table key already exists with a different type
    */
  def putValue(key: String, value: NetworkTableValue): Boolean =
    getEntry(key).setValue(value)

  /**
    * Gets the current value in the table, setting it if it does not exist.
    * @param key the key
    * @param defaultValue the default value to set if key doesn't exist.
    * @returns False if the table key exists with a different type
    */
  def setDefaultValue(key: String, defaultValue: NetworkTableValue): Boolean =
    getEntry(key).setDefaultValue(defaultValue)

  /**
    * Gets the value associated with a key as an object
    *
    * @param key the key of the value to look up
    * @return the value associated with the given key, or nullptr if the key
    * does not exist
    */
  def getValue(key: String): NetworkTableValue = getEntry(key).getValue

  /**
    * Save table values to a file.  The file format used is identical to
    * that used for SavePersistent.
    * @param filename  filename
    * @throws PersistentException if error saving file
    */
  def saveEntries(filename: String): Unit = {
    inst.saveEntries(filename, pathWithSep)
  }

  /**
    * Load table values from a file.  The file format used is identical to
    * that used for SavePersistent / LoadPersistent.
    * @param filename  filename
    * @return List of warnings (errors result in an exception instead)
    * @throws PersistentException if error saving file
    */
  def loadEntries(filename: String): Array[String] =
    inst.loadEntries(filename, pathWithSep)

  override def equals(o: Any): Boolean = {
    if (o == this) {
      true
    }
    if (!(o.isInstanceOf[NetworkTable])) {
      false
    }
    val other: NetworkTable = o.asInstanceOf[NetworkTable]
    inst == other.inst && path == other.path
  }

  override def hashCode(): Int = Objects.hash(inst, path)

}

object NetworkTable {

  /**
    * The path separator for sub-tables and keys
    *
    */ /**
    * The path separator for sub-tables and keys
    *
    */
  val PATH_SEPARATOR: Char = '/'

  /**
    * Gets the "base name" of a key. For example, "/foo/bar" becomes "bar".
    * If the key has a trailing slash, returns an empty string.
    * @param key key
    * @return base name
    */
  def basenameKey(key: String): String = {
    val slash: Int = key.lastIndexOf(PATH_SEPARATOR)
    if (slash == -1) {
      key
    }
    key.substring(slash + 1)
  }

  /**
    * Normalizes an network table key to contain no consecutive slashes and
    * optionally start with a leading slash. For example:
    *
    * <pre><code>
    * normalizeKey("/foo/bar", true)  == "/foo/bar"
    * normalizeKey("foo/bar", true)   == "/foo/bar"
    * normalizeKey("/foo/bar", false) == "foo/bar"
    * normalizeKey("foo//bar", false) == "foo/bar"
    * </code></pre>
    *
    * @param key              the key to normalize
    * @param withLeadingSlash whether or not the normalized key should begin
    *                         with a leading slash
    * @return normalized key
    */
  def normalizeKey(key: String, withLeadingSlash: Boolean): String = {
    var normalized: String = null
    normalized = if (withLeadingSlash) PATH_SEPARATOR + key else key
    normalized = normalized.replaceAll(PATH_SEPARATOR + "{2,}",
      String.valueOf(PATH_SEPARATOR))
    if (!withLeadingSlash && normalized.charAt(0) == PATH_SEPARATOR) {
      // remove leading slash, if present
      normalized = normalized.substring(1)
    }
    normalized
  }

  /**
    * Normalizes a network table key to start with exactly one leading slash
    * ("/") and contain no consecutive slashes. For example,
    * {@code "//foo/bar/"} becomes {@code "/foo/bar/"} and
    * {@code "///a/b/c"} becomes {@code "/a/b/c"}.
    *
    * <p>This is equivalent to {@code normalizeKey(key, true)}
    *
    * @param key the key to normalize
    * @return normalized key
    */
  def normalizeKey(key: String): String = normalizeKey(key, true)

  /**
    * Gets a list of the names of all the super tables of a given key. For
    * example, the key "/foo/bar/baz" has a hierarchy of "/", "/foo",
    * "/foo/bar", and "/foo/bar/baz".
    * @param key the key
    * @return List of super tables
    */
  def getHierarchy(key: String): List[String] = {
    val normal: String = normalizeKey(key, true)
    val hierarchy: List[String] = new ArrayList[String]()
    if (normal.length == 1) {
      hierarchy.add(normal)
      hierarchy
    }
    var i: Int = 1
    while (true) {
      if (i == -1) {
        // add the full key
        hierarchy.add(normal)
        //break
      } else {
        hierarchy.add(normal.substring(0, i))
      }
      i = normal.indexOf(PATH_SEPARATOR, i + 1)
    }
    hierarchy
  }

}
