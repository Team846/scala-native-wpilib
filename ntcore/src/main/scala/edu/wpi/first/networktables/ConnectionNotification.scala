/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2017-2018. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.networktables

/**
  * NetworkTables Connection notification.
  *
  * Constructor.
  * This should generally only be used internally to NetworkTables.
  *
  * @param inst      Instance
  * @param listener  Listener that was triggered
  * @param connected Connected if true
  * @param conn      Connection information
  */
final class ConnectionNotification(val inst: NetworkTableInstance,

                                   /**
                                     * Listener that was triggered.
                                     */
                                   val listener: Int,

                                   /**
                                     * True if event is due to connection being established.
                                     */
                                   val connected: Boolean,

                                   /**
                                     * Connection information.
                                     */
                                   val conn: ConnectionInfo) {
  def getInstance: NetworkTableInstance = inst
}
