/*----------------------------------------------------------------------------*/
/* Copyright (c) 2008-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.can

/**
  * Exception indicating that a CAN driver library entry-point was passed an invalid buffer.
  * Typically, this is due to a buffer being too small to include the needed safety token.
  */
class CANInvalidBufferException(msg: String) extends RuntimeException(msg) {
  def this() {
    this(null)
  }
}
