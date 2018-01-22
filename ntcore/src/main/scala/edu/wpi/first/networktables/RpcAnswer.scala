/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2017-2018. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.networktables

/**
  * NetworkTables Remote Procedure Call (Server Side).
  */
object RpcAnswer {
  private[networktables] val emptyResponse = new Array[Byte](0)
}

/**
  * Constructor.
  * This should generally only be used internally to NetworkTables.
  *
  * @param inst   Instance
  * @param entry  Entry handle
  * @param call   Call handle
  * @param name   Entry name
  * @param params Call raw parameters
  * @param conn   Connection info
  */
final class RpcAnswer(/* Network table instance. */ val inst: NetworkTableInstance,

                      /** Entry handle. */
                      val entry: Int,

                      /** Call handle. */
                      var call: Int,

                      /** Entry name. */
                      val name: String,

                      /** Call raw parameters. */
                      val params: String,

                      /** Connection that called the RPC. */
                      val conn: ConnectionInfo) {
  /**
    * Posts an empty response if one was not previously sent.
    */
  def free(): Unit = {
    if (call != 0) postResponse(RpcAnswer.emptyResponse)
  }

  /**
    * Determines if the native handle is valid.
    *
    * @return True if the native handle is valid, false otherwise.
    */
  def isValid: Boolean = call != 0

  /**
    * Post RPC response (return value) for a polled RPC.
    *
    * @param result result raw data that will be provided to remote caller
    */
  def postResponse(result: Array[Byte]): Unit = {
    NetworkTablesJNI.postRpcResponse(entry, call, result)
    call = 0
  }

  /* Cached entry object. */ private[networktables] var entryObject: NetworkTableEntry = null

  /**
    * Get the entry as an object.
    *
    * @return NetworkTableEntry for the RPC.
    */
  private[networktables] def getEntry = {
    if (entryObject == null) entryObject = new NetworkTableEntry(inst, entry)
    entryObject
  }
}
