/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2017-2018. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.networktables

import java.util.Objects

final class NetworkTableValue private[networktables](var m_type: NetworkTableType, var m_value: Any, var m_time: Long) {
  def this(`type`: NetworkTableType, value: Any) {
    this(`type`, value, NetworkTablesJNI.now)
  }

  def this(`type`: Int, value: Any, time: Long) {
    this(NetworkTableType.getFromInt(`type`), value, time)
  }

  /**
    * Get the data type.
    *
    * @return The type.
    */
  def getType: NetworkTableType = m_type

  /**
    * Get the data value stored.
    *
    * @return The type.
    */
  def getValue: Any = m_value

  /**
    * Get the creation time of the value.
    *
    * @return The time, in the units returned by NetworkTablesJNI.now().
    */
  def getTime: Long = m_time

  /**
    * Determine if entry value contains a value or is unassigned.
    *
    * @return True if the entry value contains a value.
    */
  def isValid: Boolean = m_type ne NetworkTableType.kUnassigned

  /**
    * Determine if entry value contains a boolean.
    *
    * @return True if the entry value is of boolean type.
    */
  def isBoolean: Boolean = m_type eq NetworkTableType.kBoolean

  /**
    * Determine if entry value contains a double.
    *
    * @return True if the entry value is of double type.
    */
  def isDouble: Boolean = m_type eq NetworkTableType.kDouble

  /**
    * Determine if entry value contains a string.
    *
    * @return True if the entry value is of string type.
    */
  def isString: Boolean = m_type eq NetworkTableType.kString

  /**
    * Determine if entry value contains a raw.
    *
    * @return True if the entry value is of raw type.
    */
  def isRaw: Boolean = m_type eq NetworkTableType.kRaw

  /**
    * Determine if entry value contains a rpc definition.
    *
    * @return True if the entry value is of rpc definition type.
    */
  def isRpc: Boolean = m_type eq NetworkTableType.kRpc

  /**
    * Determine if entry value contains a boolean array.
    *
    * @return True if the entry value is of boolean array type.
    */
  def isBooleanArray: Boolean = m_type eq NetworkTableType.kBooleanArray

  /**
    * Determine if entry value contains a double array.
    *
    * @return True if the entry value is of double array type.
    */
  def isDoubleArray: Boolean = m_type eq NetworkTableType.kDoubleArray

  /**
    * Determine if entry value contains a string array.
    *
    * @return True if the entry value is of string array type.
    */
  def isStringArray: Boolean = m_type eq NetworkTableType.kStringArray

  /**
    * Get the entry's boolean value.
    *
    * @throws ClassCastException if the entry value is not of boolean type.
    * @return The boolean value.
    */
  def getBoolean: Boolean = {
    if (m_type ne NetworkTableType.kBoolean) throw new ClassCastException("cannot convert " + m_type + " to boolean")
    m_value.asInstanceOf[Boolean].booleanValue
  }

  /**
    * Get the entry's double value.
    *
    * @throws ClassCastException if the entry value is not of double type.
    * @return The double value.
    */
  def getDouble: Double = {
    if (m_type ne NetworkTableType.kDouble) throw new ClassCastException("cannot convert " + m_type + " to double")
    m_value.asInstanceOf[Number].doubleValue
  }

  /**
    * Get the entry's string value.
    *
    * @throws ClassCastException if the entry value is not of string type.
    * @return The string value.
    */
  def getString: String = {
    if (m_type ne NetworkTableType.kString) throw new ClassCastException("cannot convert " + m_type + " to string")
    m_value.asInstanceOf[String]
  }

  /**
    * Get the entry's raw value.
    *
    * @throws ClassCastException if the entry value is not of raw type.
    * @return The raw value.
    */
  def getRaw: Array[Byte] = {
    if (m_type ne NetworkTableType.kRaw) throw new ClassCastException("cannot convert " + m_type + " to raw")
    m_value.asInstanceOf[Array[Byte]]
  }

  /**
    * Get the entry's rpc definition value.
    *
    * @throws ClassCastException if the entry value is not of rpc definition type.
    * @return The rpc definition value.
    */
  def getRpc: Array[Byte] = {
    if (m_type ne NetworkTableType.kRpc) throw new ClassCastException("cannot convert " + m_type + " to rpc")
    m_value.asInstanceOf[Array[Byte]]
  }

  /**
    * Get the entry's boolean array value.
    *
    * @throws ClassCastException if the entry value is not of boolean array type.
    * @return The boolean array value.
    */
  def getBooleanArray: Array[Boolean] = {
    if (m_type ne NetworkTableType.kBooleanArray) throw new ClassCastException("cannot convert " + m_type + " to boolean array")
    m_value.asInstanceOf[Array[Boolean]]
  }

  /**
    * Get the entry's double array value.
    *
    * @throws ClassCastException if the entry value is not of double array type.
    * @return The double array value.
    */
  def getDoubleArray: Array[Double] = {
    if (m_type ne NetworkTableType.kDoubleArray) throw new ClassCastException("cannot convert " + m_type + " to double array")
    m_value.asInstanceOf[Array[Double]]
  }

  /**
    * Get the entry's string array value.
    *
    * @throws ClassCastException if the entry value is not of string array type.
    * @return The string array value.
    */
  def getStringArray: Array[String] = {
    if (m_type ne NetworkTableType.kStringArray) throw new ClassCastException("cannot convert " + m_type + " to string array")
    m_value.asInstanceOf[Array[String]]
  }

  override def equals(o: Any): Boolean = {
    if (o.asInstanceOf[AnyRef] eq this) return true
    if (!o.isInstanceOf[NetworkTableValue]) return false
    val other = o.asInstanceOf[NetworkTableValue]
    (m_type eq other.m_type) && m_value == other.m_value
  }

  override def hashCode: Int = Objects.hash(m_type, m_value.asInstanceOf[AnyRef])
}

/**
  * A network table entry value.
  */
object NetworkTableValue {
  /**
    * Creates a boolean entry value.
    *
    * @param value the value
    * @return The entry value
    */
  def makeBoolean(value: Boolean) = new NetworkTableValue(NetworkTableType.kBoolean, value)

  /**
    * Creates a boolean entry value.
    *
    * @param value the value
    * @param time  the creation time to use (instead of the current time)
    * @return The entry value
    */
  def makeBoolean(value: Boolean, time: Long) = new NetworkTableValue(NetworkTableType.kBoolean, value, time)

  /**
    * Creates a double entry value.
    *
    * @param value the value
    * @return The entry value
    */
  def makeDouble(value: Double) = new NetworkTableValue(NetworkTableType.kDouble, value)

  /**
    * Creates a double entry value.
    *
    * @param value the value
    * @param time  the creation time to use (instead of the current time)
    * @return The entry value
    */
  def makeDouble(value: Double, time: Long) = new NetworkTableValue(NetworkTableType.kDouble, value, time)

  /**
    * Creates a string entry value.
    *
    * @param value the value
    * @return The entry value
    */
  def makeString(value: String) = new NetworkTableValue(NetworkTableType.kString, value)

  /**
    * Creates a string entry value.
    *
    * @param value the value
    * @param time  the creation time to use (instead of the current time)
    * @return The entry value
    */
  def makeString(value: String, time: Long) = new NetworkTableValue(NetworkTableType.kString, value, time)

  /**
    * Creates a raw entry value.
    *
    * @param value the value
    * @return The entry value
    */
  def makeRaw(value: Array[Byte]) = new NetworkTableValue(NetworkTableType.kRaw, value)

  /**
    * Creates a raw entry value.
    *
    * @param value the value
    * @param time  the creation time to use (instead of the current time)
    * @return The entry value
    */
  def makeRaw(value: Array[Byte], time: Long) = new NetworkTableValue(NetworkTableType.kRaw, value, time)

  /**
    * Creates a rpc entry value.
    *
    * @param value the value
    * @return The entry value
    */
  def makeRpc(value: Array[Byte]) = new NetworkTableValue(NetworkTableType.kRpc, value)

  /**
    * Creates a rpc entry value.
    *
    * @param value the value
    * @param time  the creation time to use (instead of the current time)
    * @return The entry value
    */
  def makeRpc(value: Array[Byte], time: Long) = new NetworkTableValue(NetworkTableType.kRpc, value, time)

  /**
    * Creates a boolean array entry value.
    *
    * @param value the value
    * @return The entry value
    */
  def makeBooleanArray(value: Array[Boolean]) = new NetworkTableValue(NetworkTableType.kBooleanArray, value)

  /**
    * Creates a boolean array entry value.
    *
    * @param value the value
    * @param time  the creation time to use (instead of the current time)
    * @return The entry value
    */
  def makeBooleanArray(value: Array[Boolean], time: Long) = new NetworkTableValue(NetworkTableType.kBooleanArray, value, time)

  /**
    * Creates a double array entry value.
    *
    * @param value the value
    * @return The entry value
    */
  def makeDoubleArray(value: Array[Double]) = new NetworkTableValue(NetworkTableType.kDoubleArray, value)

  /**
    * Creates a double array entry value.
    *
    * @param value the value
    * @param time  the creation time to use (instead of the current time)
    * @return The entry value
    */
  def makeDoubleArray(value: Array[Double], time: Long) = new NetworkTableValue(NetworkTableType.kDoubleArray, value, time)

  def makeDoubleArray(value: Array[Number]) = new NetworkTableValue(NetworkTableType.kDoubleArray, toNative(value))

  def makeDoubleArray(value: Array[Number], time: Long) = new NetworkTableValue(NetworkTableType.kDoubleArray, toNative(value), time)

  /**
    * Creates a string array entry value.
    *
    * @param value the value
    * @return The entry value
    */
  def makeStringArray(value: Array[String]) = new NetworkTableValue(NetworkTableType.kStringArray, value)

  /**
    * Creates a string array entry value.
    *
    * @param value the value
    * @param time  the creation time to use (instead of the current time)
    * @return The entry value
    */
  def makeStringArray(value: Array[String], time: Long) = new NetworkTableValue(NetworkTableType.kStringArray, value, time)

  private[networktables] def toNative(arr: Array[Boolean]): Array[Boolean] = {
    arr
  }

  private[networktables] def toNative(arr: Array[Double]): Array[Double] = {
    arr
  }

  private[networktables] def toNative(arr: Array[Number]): Array[Double] = {
    arr.map(_.doubleValue())
  }

  private[networktables] def fromNative(arr: Array[Boolean]): Array[Boolean] = {
    arr
  }

  private[networktables] def fromNative(arr: Array[Double]): Array[Double] = {
    arr
  }
}
