/*----------------------------------------------------------------------------*/
/* Copyright (c) 2008-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.util

/**
  * Exception for bad status codes from the chip object.
  *
  * Create a new UncleanStatusException.
  *
  * @param status  the status code that caused the exception
  * @param message A message describing the exception
  */
final class UncleanStatusException(val m_statusCode: Int, val message: String) extends IllegalStateException(message) {
  /**
    * Create a new UncleanStatusException.
    *
    * @param status the status code that caused the exception
    */
  def this(status: Int) {
    this(status, "Status code was non-zero")
  }

  /**
    * Create a new UncleanStatusException.
    *
    * @param message a message describing the exception
    */
  def this(message: String) {
    this(-1, message)
  }

  /**
    * Create a new UncleanStatusException.
    */
  def this() {
    this(-1, "Status code was non-zero")
  }

  /**
    * Create a new UncleanStatusException.
    *
    * @return the status code that caused the exception
    */
  def getStatus: Int = m_statusCode
}
