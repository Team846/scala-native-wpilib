/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2017-2018. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.networktables


/**
  * NetworkTables Remote Procedure Call.
  *
  * Constructor.
  * This should generally only be used internally to NetworkTables.
  *
  * @param entry Entry
  * @param call  Call handle
  */
final class RpcCall(val m_entry: NetworkTableEntry, var m_call: Int) {
  /**
    * Cancels the result if no other action taken.
    */
  def free(): Unit = {
    if (m_call != 0) cancelResult()
  }

  /**
    * Determines if the native handle is valid.
    *
    * @return True if the native handle is valid, false otherwise.
    */
  def isValid: Boolean = m_call != 0

  /**
    * Get the RPC entry.
    *
    * @return NetworkTableEntry for the RPC.
    */
  def getEntry: NetworkTableEntry = m_entry

  /**
    * Get the call native handle.
    *
    * @return Native handle.
    */
  def getCall: Int = m_call

  /**
    * Get the result (return value).  This function blocks until
    * the result is received.
    *
    * @return Received result (output)
    */
  def getResult: Array[Byte] = {
    val result = NetworkTablesJNI.getRpcResult(m_entry.getHandle, m_call)
    if (result.length != 0) m_call = 0
    result
  }

  /**
    * Get the result (return value).  This function blocks until
    * the result is received or it times out.
    *
    * @param timeout timeout, in seconds
    * @return Received result (output)
    */
  def getResult(timeout: Double): Array[Byte] = {
    val result = NetworkTablesJNI.getRpcResult(m_entry.getHandle, m_call, timeout)
    if (result.length != 0) m_call = 0
    result
  }

  /**
    * Ignore the result.  This function is non-blocking.
    */
  def cancelResult(): Unit = {
    NetworkTablesJNI.cancelRpcResult(m_entry.getHandle, m_call)
  }
}
