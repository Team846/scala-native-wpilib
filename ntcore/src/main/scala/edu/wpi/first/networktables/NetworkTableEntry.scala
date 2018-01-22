/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2017-2018. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.networktables

import java.nio.ByteBuffer
import java.util.function.Consumer


/**
  * Construct from native handle.
  *
  * @param inst   Instance
  * @param handle Native handle
  */
final class NetworkTableEntry(var m_inst: NetworkTableInstance, var m_handle: Int) {
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
    * Gets the instance for the entry.
    *
    * @return Instance
    */
  def getInstance: NetworkTableInstance = m_inst

  /**
    * Determines if the entry currently exists.
    *
    * @return True if the entry exists, false otherwise.
    */
  def exists(): Boolean = NetworkTablesJNI.getType(m_handle) != 0

  /**
    * Gets the name of the entry (the key).
    *
    * @return the entry's name
    */
  def getName: String = NetworkTablesJNI.getEntryName(m_handle)

  /**
    * Gets the type of the entry.
    *
    * @return the entry's type
    */
  def getType: NetworkTableType = NetworkTableType.getFromInt(NetworkTablesJNI.getType(m_handle))

  /**
    * Returns the flags.
    *
    * @return the flags (bitmask)
    */
  def getFlags: Int = NetworkTablesJNI.getEntryFlags(m_handle)

  /**
    * Gets the last time the entry's value was changed.
    *
    * @return Entry last change time
    */
  def getLastChange: Long = NetworkTablesJNI.getEntryLastChange(m_handle)

  /**
    * Gets combined information about the entry.
    *
    * @return Entry information
    */
  def getInfo: EntryInfo = NetworkTablesJNI.getEntryInfoHandle(m_inst, m_handle)

  /**
    * Gets the entry's value.
    * Returns a value with type NetworkTableType.kUnassigned if the value
    * does not exist.
    *
    * @return the entry's value
    */
  def getValue: NetworkTableValue = NetworkTablesJNI.getValue(m_handle)

  /**
    * Gets the entry's value as a boolean. If the entry does not exist or is of
    * different type, it will return the default value.
    *
    * @param defaultValue the value to be returned if no value is found
    * @return the entry's value or the given default value
    */
  def getBoolean(defaultValue: Boolean): Boolean = NetworkTablesJNI.getBoolean(m_handle, defaultValue)

  /**
    * Gets the entry's value as a double. If the entry does not exist or is of
    * different type, it will return the default value.
    *
    * @param defaultValue the value to be returned if no value is found
    * @return the entry's value or the given default value
    */
  def getDouble(defaultValue: Double): Double = NetworkTablesJNI.getDouble(m_handle, defaultValue)

  def getNumber(defaultValue: Number): Number = NetworkTablesJNI.getDouble(m_handle, defaultValue.doubleValue)

  /**
    * Gets the entry's value as a string. If the entry does not exist or is of
    * different type, it will return the default value.
    *
    * @param defaultValue the value to be returned if no value is found
    * @return the entry's value or the given default value
    */
  def getString(defaultValue: String): String = NetworkTablesJNI.getString(m_handle, defaultValue)

  /**
    * Gets the entry's value as a raw value (byte array). If the entry does not
    * exist or is of different type, it will return the default value.
    *
    * @param defaultValue the value to be returned if no value is found
    * @return the entry's value or the given default value
    */
  def getRaw(defaultValue: Array[Byte]): Array[Byte] = NetworkTablesJNI.getRaw(m_handle, defaultValue)

  /**
    * Gets the entry's value as a boolean array. If the entry does not exist
    * or is of different type, it will return the default value.
    *
    * @param defaultValue the value to be returned if no value is found
    * @return the entry's value or the given default value
    */
  def getBooleanArray(defaultValue: Array[Boolean]): Array[Boolean] = NetworkTablesJNI.getBooleanArray(m_handle, defaultValue)

  /**
    * Gets the entry's value as a double array. If the entry does not exist
    * or is of different type, it will return the default value.
    *
    * @param defaultValue the value to be returned if no value is found
    * @return the entry's value or the given default value
    */
  def getDoubleArray(defaultValue: Array[Double]): Array[Double] = NetworkTablesJNI.getDoubleArray(m_handle, defaultValue)

  def getNumberArray(defaultValue: Array[Number]): Array[Number] = NetworkTableValue.fromNative(NetworkTablesJNI.getDoubleArray(m_handle, NetworkTableValue.toNative(defaultValue))).map(v => v: java.lang.Double)

  /**
    * Gets the entry's value as a string array. If the entry does not exist
    * or is of different type, it will return the default value.
    *
    * @param defaultValue the value to be returned if no value is found
    * @return the entry's value or the given default value
    */
  def getStringArray(defaultValue: Array[String]): Array[String] = NetworkTablesJNI.getStringArray(m_handle, defaultValue)

  /**
    * Sets the entry's value if it does not exist.
    *
    * @param defaultValue the default value to set
    * @return False if the entry exists with a different type
    * @throws IllegalArgumentException if the value is not a known type
    */
  def setDefaultValue(defaultValue: Any): Boolean = if (defaultValue.isInstanceOf[NetworkTableValue]) {
    val time = defaultValue.asInstanceOf[NetworkTableValue].getTime
    val o = defaultValue.asInstanceOf[NetworkTableValue].getValue
    import NetworkTableType._
    defaultValue.asInstanceOf[NetworkTableValue].getType match {
      case v if v == kBoolean =>
        NetworkTablesJNI.setDefaultBoolean(m_handle, time, o.asInstanceOf[Boolean].booleanValue)
      case v if v == kDouble =>
        NetworkTablesJNI.setDefaultDouble(m_handle, time, o.asInstanceOf[Number].doubleValue)
      case v if v == kString =>
        NetworkTablesJNI.setDefaultString(m_handle, time, o.asInstanceOf[String])
      case v if v == kRaw =>
        NetworkTablesJNI.setDefaultRaw(m_handle, time, o.asInstanceOf[Array[Byte]])
      case v if v == kBooleanArray =>
        NetworkTablesJNI.setDefaultBooleanArray(m_handle, time, o.asInstanceOf[Array[Boolean]])
      case v if v == kDoubleArray =>
        NetworkTablesJNI.setDefaultDoubleArray(m_handle, time, o.asInstanceOf[Array[Double]])
      case v if v == kStringArray =>
        NetworkTablesJNI.setDefaultStringArray(m_handle, time, o.asInstanceOf[Array[String]])
      case v if v == kRpc =>
        true
      case _ =>
        true
    }
  }
  else if (defaultValue.isInstanceOf[Boolean]) setDefaultBoolean(defaultValue.asInstanceOf[Boolean])
  else if (defaultValue.isInstanceOf[Number]) setDefaultNumber(defaultValue.asInstanceOf[Number])
  else if (defaultValue.isInstanceOf[String]) setDefaultString(defaultValue.asInstanceOf[String])
  else if (defaultValue.isInstanceOf[Array[Byte]]) setDefaultRaw(defaultValue.asInstanceOf[Array[Byte]])
  else if (defaultValue.isInstanceOf[Array[Boolean]]) setDefaultBooleanArray(defaultValue.asInstanceOf[Array[Boolean]])
  else if (defaultValue.isInstanceOf[Array[Double]]) setDefaultDoubleArray(defaultValue.asInstanceOf[Array[Double]])
  else if (defaultValue.isInstanceOf[Array[Number]]) setDefaultNumberArray(defaultValue.asInstanceOf[Array[Number]])
  else if (defaultValue.isInstanceOf[Array[String]]) setDefaultStringArray(defaultValue.asInstanceOf[Array[String]])
  else throw new IllegalArgumentException("Value of type " + defaultValue.getClass.getName + " cannot be put into a table")

  /**
    * Sets the entry's value if it does not exist.
    *
    * @param defaultValue the default value to set
    * @return False if the entry exists with a different type
    */
  def setDefaultBoolean(defaultValue: Boolean): Boolean = NetworkTablesJNI.setDefaultBoolean(m_handle, 0, defaultValue)

  def setDefaultDouble(defaultValue: Double): Boolean = NetworkTablesJNI.setDefaultDouble(m_handle, 0, defaultValue)

  def setDefaultNumber(defaultValue: Number): Boolean = NetworkTablesJNI.setDefaultDouble(m_handle, 0, defaultValue.doubleValue)

  def setDefaultString(defaultValue: String): Boolean = NetworkTablesJNI.setDefaultString(m_handle, 0, defaultValue)

  def setDefaultRaw(defaultValue: Array[Byte]): Boolean = NetworkTablesJNI.setDefaultRaw(m_handle, 0, defaultValue)

  def setDefaultBooleanArray(defaultValue: Array[Boolean]): Boolean = NetworkTablesJNI.setDefaultBooleanArray(m_handle, 0, defaultValue)

  def setDefaultDoubleArray(defaultValue: Array[Double]): Boolean = NetworkTablesJNI.setDefaultDoubleArray(m_handle, 0, defaultValue)

  def setDefaultNumberArray(defaultValue: Array[Number]): Boolean = NetworkTablesJNI.setDefaultDoubleArray(m_handle, 0, NetworkTableValue.toNative(defaultValue))

  def setDefaultStringArray(defaultValue: Array[String]): Boolean = NetworkTablesJNI.setDefaultStringArray(m_handle, 0, defaultValue)

  /**
    * Sets the entry's value
    *
    * @param value the value that will be assigned
    * @return False if the table key already exists with a different type
    * @throws IllegalArgumentException if the value is not a known type
    */
  def setValue(value: Any): Boolean = if (value.isInstanceOf[NetworkTableValue]) {
    val time = value.asInstanceOf[NetworkTableValue].getTime
    val o = value.asInstanceOf[NetworkTableValue].getValue
    import NetworkTableType._
    value.asInstanceOf[NetworkTableValue].getType match {
      case v if v == kBoolean =>
        NetworkTablesJNI.setBoolean(m_handle, time, o.asInstanceOf[Boolean].booleanValue, false)
      case v if v == kDouble =>
        NetworkTablesJNI.setDouble(m_handle, time, o.asInstanceOf[Number].doubleValue, false)
      case v if v == kString =>
        NetworkTablesJNI.setString(m_handle, time, o.asInstanceOf[String], false)
      case v if v == kRaw =>
        NetworkTablesJNI.setRaw(m_handle, time, o.asInstanceOf[Array[Byte]], false)
      case v if v == kBooleanArray =>
        NetworkTablesJNI.setBooleanArray(m_handle, time, o.asInstanceOf[Array[Boolean]], false)
      case v if v == kDoubleArray =>
        NetworkTablesJNI.setDoubleArray(m_handle, time, o.asInstanceOf[Array[Double]], false)
      case v if v == kStringArray =>
        NetworkTablesJNI.setStringArray(m_handle, time, o.asInstanceOf[Array[String]], false)
      case v if v == kRpc => true
      case _ =>
        true
    }
  }
  else if (value.isInstanceOf[Boolean]) setBoolean(value.asInstanceOf[Boolean])
  else if (value.isInstanceOf[Number]) setNumber(value.asInstanceOf[Number])
  else if (value.isInstanceOf[String]) setString(value.asInstanceOf[String])
  else if (value.isInstanceOf[Array[Byte]]) setRaw(value.asInstanceOf[Array[Byte]])
  else if (value.isInstanceOf[Array[Boolean]]) setBooleanArray(value.asInstanceOf[Array[Boolean]])
  else if (value.isInstanceOf[Array[Double]]) setDoubleArray(value.asInstanceOf[Array[Double]])
  else if (value.isInstanceOf[Array[Boolean]]) setBooleanArray(value.asInstanceOf[Array[Boolean]])
  else if (value.isInstanceOf[Array[Number]]) setNumberArray(value.asInstanceOf[Array[Number]])
  else if (value.isInstanceOf[Array[String]]) setStringArray(value.asInstanceOf[Array[String]])
  else throw new IllegalArgumentException("Value of type " + value.getClass.getName + " cannot be put into a table")

  /**
    * Sets the entry's value.
    *
    * @param value the value to set
    * @return False if the entry exists with a different type
    */
  def setBoolean(value: Boolean): Boolean = NetworkTablesJNI.setBoolean(m_handle, 0, value, false)

  def setDouble(value: Double): Boolean = NetworkTablesJNI.setDouble(m_handle, 0, value, false)

  def setNumber(value: Number): Boolean = NetworkTablesJNI.setDouble(m_handle, 0, value.doubleValue, false)

  def setString(value: String): Boolean = NetworkTablesJNI.setString(m_handle, 0, value, false)

  def setRaw(value: Array[Byte]): Boolean = NetworkTablesJNI.setRaw(m_handle, 0, value, false)

  /**
    * Sets the entry's value.
    *
    * @param value the value to set
    * @param len   the length of the value
    * @return False if the entry exists with a different type
    */
  def setRaw(value: ByteBuffer, len: Int): Boolean = {
    if (!value.isDirect) throw new IllegalArgumentException("must be a direct buffer")
    if (value.capacity < len) throw new IllegalArgumentException("buffer is too small, must be at least " + len)
    NetworkTablesJNI.setRaw(m_handle, 0, value, len, false)
  }

  def setBooleanArray(value: Array[Boolean]): Boolean = NetworkTablesJNI.setBooleanArray(m_handle, 0, value, false)

  def setDoubleArray(value: Array[Double]): Boolean = NetworkTablesJNI.setDoubleArray(m_handle, 0, value, false)

  def setNumberArray(value: Array[Number]): Boolean = NetworkTablesJNI.setDoubleArray(m_handle, 0, NetworkTableValue.toNative(value), false)

  def setStringArray(value: Array[String]): Boolean = NetworkTablesJNI.setStringArray(m_handle, 0, value, false)

  /**
    * Sets the entry's value.  If the value is of different type, the type is
    * changed to match the new value.
    *
    * @param value the value to set
    * @throws IllegalArgumentException if the value is not a known type
    */
  def forceSetValue(value: Any): Unit = {
    if (value.isInstanceOf[NetworkTableValue]) {
      val time = value.asInstanceOf[NetworkTableValue].getTime
      val o = value.asInstanceOf[NetworkTableValue].getValue
      import NetworkTableType._
      value.asInstanceOf[NetworkTableValue].getType match {
        case v if v == kBoolean =>
          NetworkTablesJNI.setBoolean(m_handle, time, o.asInstanceOf[Boolean].booleanValue, true)
        case v if v == kDouble =>
          NetworkTablesJNI.setDouble(m_handle, time, o.asInstanceOf[Number].doubleValue, true)
        case v if v == kString =>
          NetworkTablesJNI.setString(m_handle, time, o.asInstanceOf[String], true)
        case v if v == kRaw =>
          NetworkTablesJNI.setRaw(m_handle, time, o.asInstanceOf[Array[Byte]], true)
        case v if v == kBooleanArray =>
          NetworkTablesJNI.setBooleanArray(m_handle, time, o.asInstanceOf[Array[Boolean]], true)
        case v if v == kDoubleArray =>
          NetworkTablesJNI.setDoubleArray(m_handle, time, o.asInstanceOf[Array[Double]], true)
        case v if v == kStringArray =>
          NetworkTablesJNI.setStringArray(m_handle, time, o.asInstanceOf[Array[String]], true)
        case v if v == kRpc =>
        case _ =>
      }
    }
    else if (value.isInstanceOf[Boolean]) forceSetBoolean(value.asInstanceOf[Boolean])
    else if (value.isInstanceOf[Number]) forceSetNumber(value.asInstanceOf[Number])
    else if (value.isInstanceOf[String]) forceSetString(value.asInstanceOf[String])
    else if (value.isInstanceOf[Array[Byte]]) forceSetRaw(value.asInstanceOf[Array[Byte]])
    else if (value.isInstanceOf[Array[Boolean]]) forceSetBooleanArray(value.asInstanceOf[Array[Boolean]])
    else if (value.isInstanceOf[Array[Double]]) forceSetDoubleArray(value.asInstanceOf[Array[Double]])
    else if (value.isInstanceOf[Array[Number]]) forceSetNumberArray(value.asInstanceOf[Array[Number]])
    else if (value.isInstanceOf[Array[String]]) forceSetStringArray(value.asInstanceOf[Array[String]])
    else throw new IllegalArgumentException("Value of type " + value.getClass.getName + " cannot be put into a table")
  }

  /**
    * Sets the entry's value.  If the value is of different type, the type is
    * changed to match the new value.
    *
    * @param value the value to set
    */
  def forceSetBoolean(value: Boolean): Unit = {
    NetworkTablesJNI.setBoolean(m_handle, 0, value, true)
  }

  def forceSetDouble(value: Double): Unit = {
    NetworkTablesJNI.setDouble(m_handle, 0, value, true)
  }

  def forceSetNumber(value: Number): Unit = {
    NetworkTablesJNI.setDouble(m_handle, 0, value.doubleValue, true)
  }

  def forceSetString(value: String): Unit = {
    NetworkTablesJNI.setString(m_handle, 0, value, true)
  }

  def forceSetRaw(value: Array[Byte]): Unit = {
    NetworkTablesJNI.setRaw(m_handle, 0, value, true)
  }

  def forceSetBooleanArray(value: Array[Boolean]): Unit = {
    NetworkTablesJNI.setBooleanArray(m_handle, 0, value, true)
  }

  def forceSetDoubleArray(value: Array[Double]): Unit = {
    NetworkTablesJNI.setDoubleArray(m_handle, 0, value, true)
  }

  def forceSetNumberArray(value: Array[Number]): Unit = {
    NetworkTablesJNI.setDoubleArray(m_handle, 0, NetworkTableValue.toNative(value), true)
  }

  def forceSetStringArray(value: Array[String]): Unit = {
    NetworkTablesJNI.setStringArray(m_handle, 0, value, true)
  }

  /**
    * Sets flags.
    *
    * @param flags the flags to set (bitmask)
    */
  def setFlags(flags: Int): Unit = {
    NetworkTablesJNI.setEntryFlags(m_handle, getFlags | flags)
  }

  /**
    * Clears flags.
    *
    * @param flags the flags to clear (bitmask)
    */
  def clearFlags(flags: Int): Unit = {
    NetworkTablesJNI.setEntryFlags(m_handle, getFlags & ~flags)
  }

  /**
    * Make value persistent through program restarts.
    */
  def setPersistent(): Unit = {
    setFlags(NetworkTableEntry.kPersistent)
  }

  /**
    * Stop making value persistent through program restarts.
    */
  def clearPersistent(): Unit = {
    clearFlags(NetworkTableEntry.kPersistent)
  }

  /**
    * Returns whether the value is persistent through program restarts.
    *
    * @return True if the value is persistent.
    */
  def isPersistent: Boolean = (getFlags & NetworkTableEntry.kPersistent) != 0

  /**
    * Deletes the entry.
    */
  def delete(): Unit = {
    NetworkTablesJNI.deleteEntry(m_handle)
  }

  /**
    * Create a callback-based RPC entry point.  Only valid to use on the server.
    * The callback function will be called when the RPC is called.
    * This function creates RPC version 0 definitions (raw data in and out).
    *
    * @param callback callback function
    */
  private[networktables] def createRpc(callback: Consumer[RpcAnswer]): Unit = {
    m_inst.createRpc(this, callback)
  }

  /**
    * Call a RPC function.  May be used on either the client or server.
    * This function is non-blocking.  Either {@link RpcCall#GetResult()} or
    * {@link RpcCall#CancelResult()} must be called on the return value to either
    * get or ignore the result of the call.
    *
    * @param params parameter
    * @return RPC call object.
    */
  private[networktables] def callRpc(params: Array[Byte]) = new RpcCall(this, NetworkTablesJNI.callRpc(m_handle, params))

  /**
    * Add a listener for changes to the entry
    *
    * @param listener the listener to add
    * @param flags    bitmask specifying desired notifications
    * @return listener handle
    */
  def addListener(listener: Consumer[EntryNotification], flags: Int): Int = m_inst.addEntryListener(this, listener, flags)

  /**
    * Remove a listener from receiving entry events
    *
    * @param listener the listener to be removed
    */
  def removeListener(listener: Int): Unit = {
    m_inst.removeEntryListener(listener)
  }

  override def equals(o: Any): Boolean = {
    if (o.asInstanceOf[AnyRef] eq this) return true
    if (!o.isInstanceOf[NetworkTableEntry]) return false
    val other = o.asInstanceOf[NetworkTableEntry]
    m_handle == other.m_handle
  }

  override def hashCode: Int = m_handle
}

/**
  * NetworkTables Entry
  */
object NetworkTableEntry {
  /**
    * Flag values (as returned by {@link #getFlags()}).
    */
  val kPersistent = 0x01
}