/*----------------------------------------------------------------------------*/
/* Copyright (c) 2008-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.util

/**
  * Create a new exception with the given message.
  *
  * @param message the message to attach to the exception
  */
class BoundaryException(val message: String) extends RuntimeException(message) {
}

/**
  * This exception represents an error in which a lower limit was set as higher than an upper limit.
  */
object BoundaryException {
  /**
    * Make sure that the given value is between the upper and lower bounds, and throw an exception if
    * they are not.
    *
    * @param value The value to check.
    * @param lower The minimum acceptable value.
    * @param upper The maximum acceptable value.
    */
  def assertWithinBounds(value: Double, lower: Double, upper: Double): Unit = {
    if (value < lower || value > upper) throw new BoundaryException("Value must be between " + lower + " and " + upper + ", " + value + " given")
  }

  /**
    * Returns the message for a boundary exception. Used to keep the message consistent across all
    * boundary exceptions.
    *
    * @param value The given value
    * @param lower The lower limit
    * @param upper The upper limit
    * @return the message for a boundary exception
    */
  def getMessage(value: Double, lower: Double, upper: Double): String = "Value must be between " + lower + " and " + upper + ", " + value + " given"
}