/*----------------------------------------------------------------------------*/
/* Copyright (c) 2008-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.can

/**
  * Exception indicating that a can message is not available from Network Communications. This
  * usually just means we already have the most recent value cached locally.
  */
class CANMessageNotFoundException(msg: String) extends RuntimeException(msg) {
  def this() {
    this(null)
  }
}
