/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2017-2018. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.networktables

import java.io.IOException

/**
  * An exception thrown when persistent load/save fails in a {@link NetworkTable}
  *
  * @param message The error message
  */
@SerialVersionUID(0)
final class PersistentException(val message: String) extends IOException(message) {
}
