/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008-2018. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.util

/**
  * Thrown if there is an error caused by a basic system or setting not being properly initialized
  * before being used.
  *
  * Create a new BaseSystemNotInitializedException.
  *
  * @param message the message to attach to the exception
  */
class BaseSystemNotInitializedException(message: String) extends RuntimeException(message) {
  /**
    * Create a new BaseSystemNotInitializedException using the offending class that was not set and
    * the class that was affected.
    *
    * @param offender The class or interface that was not properly initialized.
    * @param affected The class that was was affected by this missing initialization.
    */
  def this(offender: Class[_], affected: Class[_]) {
    this("The " + offender.getSimpleName + " for the " + affected.getSimpleName + " was never set.")
  }
}
