/*----------------------------------------------------------------------------*/
/* Copyright (c) 2008-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.util

/**
  * Exception indicating that an error has occured with a HAL Handle.
  *
  * Create a new HalHandleException.
  *
  * @param msg the message to attach to the exception
  */
class HalHandleException(val msg: String) extends RuntimeException(msg) {
}
