/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2017-2018. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.networktables

import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
//import edu.wpi.first.wpiutil.RuntimeDetector

import com.lynbrookrobotics.scalanativejni._
import scala.scalanative.native._

@jnilib("ntcore")
object NetworkTablesJNI {
  private[networktables] var libraryLoaded = false
  private[networktables] var jniLibrary = null

  def getDefaultInstance: Int = jni

  def createInstance: Int = jni

  def destroyInstance(inst: Int): Unit = jni

  def getInstanceFromHandle(handle: Int): Int = jni

  def getEntry(inst: Int, key: String): Int = jni

  def getEntries(inst: Int, prefix: String, types: Int): Array[Int] = jni

  def getEntryName(entry: Int): String = jni

  def getEntryLastChange(entry: Int): Long = jni

  def getType(entry: Int): Int = jni

  def setBoolean(entry: Int, time: Long, value: Boolean, force: Boolean): Boolean = jni

  def setDouble(entry: Int, time: Long, value: Double, force: Boolean): Boolean = jni

  def setString(entry: Int, time: Long, value: String, force: Boolean): Boolean = jni

  def setRaw(entry: Int, time: Long, value: Array[Byte], force: Boolean): Boolean = jni

  def setRaw(entry: Int, time: Long, value: ByteBuffer, len: Int, force: Boolean): Boolean = jni

  def setBooleanArray(entry: Int, time: Long, value: Array[Boolean], force: Boolean): Boolean = jni

  def setDoubleArray(entry: Int, time: Long, value: Array[Double], force: Boolean): Boolean = jni

  def setStringArray(entry: Int, time: Long, value: Array[String], force: Boolean): Boolean = jni

  def getValue(entry: Int): NetworkTableValue = jni

  def getBoolean(entry: Int, defaultValue: Boolean): Boolean = jni

  def getDouble(entry: Int, defaultValue: Double): Double = jni

  def getString(entry: Int, defaultValue: String): String = jni

  def getRaw(entry: Int, defaultValue: Array[Byte]): Array[Byte] = jni

  def getBooleanArray(entry: Int, defaultValue: Array[Boolean]): Array[Boolean] = jni

  def getDoubleArray(entry: Int, defaultValue: Array[Double]): Array[Double] = jni

  def getStringArray(entry: Int, defaultValue: Array[String]): Array[String] = jni

  def setDefaultBoolean(entry: Int, time: Long, defaultValue: Boolean): Boolean = jni

  def setDefaultDouble(entry: Int, time: Long, defaultValue: Double): Boolean = jni

  def setDefaultString(entry: Int, time: Long, defaultValue: String): Boolean = jni

  def setDefaultRaw(entry: Int, time: Long, defaultValue: Array[Byte]): Boolean = jni

  def setDefaultBooleanArray(entry: Int, time: Long, defaultValue: Array[Boolean]): Boolean = jni

  def setDefaultDoubleArray(entry: Int, time: Long, defaultValue: Array[Double]): Boolean = jni

  def setDefaultStringArray(entry: Int, time: Long, defaultValue: Array[String]): Boolean = jni

  def setEntryFlags(entry: Int, flags: Int): Unit = jni

  def getEntryFlags(entry: Int): Int = jni

  def deleteEntry(entry: Int): Unit = jni

  def deleteAllEntries(inst: Int): Unit = jni

  def getEntryInfoHandle(inst: NetworkTableInstance, entry: Int): EntryInfo = jni

  def getEntryInfo(instObject: NetworkTableInstance, inst: Int, prefix: String, types: Int): Array[EntryInfo] = jni

  def createEntryListenerPoller(inst: Int): Int = jni

  def destroyEntryListenerPoller(poller: Int): Unit = jni

  def addPolledEntryListener(poller: Int, prefix: String, flags: Int): Int = jni

  def addPolledEntryListener(poller: Int, entry: Int, flags: Int): Int = jni

  @throws[InterruptedException]
  def pollEntryListener(inst: NetworkTableInstance, poller: Int): Array[EntryNotification] = jni

  @throws[InterruptedException]
  def pollEntryListenerTimeout(inst: NetworkTableInstance, poller: Int, timeout: Double): Array[EntryNotification] = jni

  def cancelPollEntryListener(poller: Int): Unit = jni

  def removeEntryListener(entryListener: Int): Unit = jni

  def waitForEntryListenerQueue(inst: Int, timeout: Double): Boolean = jni

  def createConnectionListenerPoller(inst: Int): Int = jni

  def destroyConnectionListenerPoller(poller: Int): Unit = jni

  def addPolledConnectionListener(poller: Int, immediateNotify: Boolean): Int = jni

  @throws[InterruptedException]
  def pollConnectionListener(inst: NetworkTableInstance, poller: Int): Array[ConnectionNotification] = jni

  @throws[InterruptedException]
  def pollConnectionListenerTimeout(inst: NetworkTableInstance, poller: Int, timeout: Double): Array[ConnectionNotification] = jni

  def cancelPollConnectionListener(poller: Int): Unit = jni

  def removeConnectionListener(connListener: Int): Unit = jni

  def waitForConnectionListenerQueue(inst: Int, timeout: Double): Boolean = jni

  def createRpcCallPoller(inst: Int): Int = jni

  def destroyRpcCallPoller(poller: Int): Unit = jni

  def createPolledRpc(entry: Int, `def`: Array[Byte], poller: Int): Unit = jni

  @throws[InterruptedException]
  def pollRpc(inst: NetworkTableInstance, poller: Int): Array[RpcAnswer] = jni

  @throws[InterruptedException]
  def pollRpcTimeout(inst: NetworkTableInstance, poller: Int, timeout: Double): Array[RpcAnswer] = jni

  def cancelPollRpc(poller: Int): Unit = jni

  def waitForRpcCallQueue(inst: Int, timeout: Double): Boolean = jni

  def postRpcResponse(entry: Int, call: Int, result: Array[Byte]): Unit = jni

  def callRpc(entry: Int, params: Array[Byte]): Int = jni

  def getRpcResult(entry: Int, call: Int): Array[Byte] = jni

  def getRpcResult(entry: Int, call: Int, timeout: Double): Array[Byte] = jni

  def cancelRpcResult(entry: Int, call: Int): Unit = jni

  def getRpc(entry: Int, defaultValue: Array[Byte]): Array[Byte] = jni

  def setNetworkIdentity(inst: Int, name: String): Unit = jni

  def getNetworkMode(inst: Int): Int = jni

  def startServer(inst: Int, persistFilename: String, listenAddress: String, port: Int): Unit = jni

  def stopServer(inst: Int): Unit = jni

  def startClient(inst: Int): Unit = jni

  def startClient(inst: Int, serverName: String, port: Int): Unit = jni

  def startClient(inst: Int, serverNames: Array[String], ports: Array[Int]): Unit = jni

  def startClientTeam(inst: Int, team: Int, port: Int): Unit = jni

  def stopClient(inst: Int): Unit = jni

  def setServer(inst: Int, serverName: String, port: Int): Unit = jni

  def setServer(inst: Int, serverNames: Array[String], ports: Array[Int]): Unit = jni

  def setServerTeam(inst: Int, team: Int, port: Int): Unit = jni

  def startDSClient(inst: Int, port: Int): Unit = jni

  def stopDSClient(inst: Int): Unit = jni

  def setUpdateRate(inst: Int, interval: Double): Unit = jni

  def flush(inst: Int): Unit = jni

  def getConnections(inst: Int): Array[ConnectionInfo] = jni

  def isConnected(inst: Int): Boolean = jni

//  @throws[PersistentException]
  def savePersistent(inst: Int, filename: String): Unit = jni

//  @throws[PersistentException]
  def loadPersistent(inst: Int, filename: String): Array[String] = jni // returns warnings

//  @throws[PersistentException]
  def saveEntries(inst: Int, filename: String, prefix: String): Unit = jni

//  @throws[PersistentException]
  def loadEntries(inst: Int, filename: String, prefix: String): Array[String] = jni

  def now: Long = jni

  def createLoggerPoller(inst: Int): Int = jni

  def destroyLoggerPoller(poller: Int): Unit = jni

  def addPolledLogger(poller: Int, minLevel: Int, maxLevel: Int): Int = jni

  @throws[InterruptedException]
  def pollLogger(inst: NetworkTableInstance, poller: Int): Array[LogMessage] = jni

  @throws[InterruptedException]
  def pollLoggerTimeout(inst: NetworkTableInstance, poller: Int, timeout: Double): Array[LogMessage] = jni

  def cancelPollLogger(poller: Int): Unit = jni

  def removeLogger(logger: Int): Unit = jni

  def waitForLoggerQueue(inst: Int, timeout: Double): Boolean = jni

  if (!libraryLoaded) {
    DL.dlopen(c"libntcore.so", 0x002 /* RTLD_NOW */)
    JNILoad.JNI_OnLoad(vm, null)
    libraryLoaded = true
  }
}
