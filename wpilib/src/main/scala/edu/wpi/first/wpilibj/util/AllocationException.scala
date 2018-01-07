/*----------------------------------------------------------------------------*/
/* Copyright (c) 2008-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.util

/**
  * Exception indicating that the resource is already allocated.
  *
  * Create a new AllocationException.
  *
  * @param msg the message to attach to the exception
  */
class AllocationException(val msg: String) extends RuntimeException(msg) {
}
