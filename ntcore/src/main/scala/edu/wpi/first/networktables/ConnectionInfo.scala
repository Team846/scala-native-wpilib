/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2017-2018. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.networktables

/**
  * NetworkTables Connection information.
  *
  * Constructor.
  * This should generally only be used internally to NetworkTables.
  *
  * @param remote_id        Remote identifier
  * @param remote_ip        Remote IP address
  * @param remote_port      Remote port number
  * @param last_update      Last time an update was received
  * @param protocol_version The protocol version used for the connection
  */
final class ConnectionInfo(/**
                             * The remote identifier (as set on the remote node by
                             * {@link NetworkTableInstance#setNetworkIdentity(String)}).
                             */
                           val remote_id: String,

                           /**
                             * The IP address of the remote node.
                             */
                           val remote_ip: String,

                           /**
                             * The port number of the remote node.
                             */
                           val remote_port: Int,

                           /**
                             * The last time any update was received from the remote node (same scale as
                             * returned by {@link NetworkTablesJNI#now()}).
                             */
                           val last_update: Long,

                           /**
                             * The protocol version being used for this connection.  This is in protocol
                             * layer format, so 0x0200 = 2.0, 0x0300 = 3.0).
                             */
                           val protocol_version: Int) {
}
