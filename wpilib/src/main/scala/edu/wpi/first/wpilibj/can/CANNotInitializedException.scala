/*----------------------------------------------------------------------------*/
/* Copyright (c) 2008-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.can

/**
  * Exception indicating that the CAN driver layer has not been initialized. This happens when an
  * entry-point is called before a CAN driver plugin has been installed.
  */
class CANNotInitializedException(msg: String) extends RuntimeException(msg) {
  def this() {
    this(null)
  }
}
