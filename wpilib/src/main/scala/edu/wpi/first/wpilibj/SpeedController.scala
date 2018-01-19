/*----------------------------------------------------------------------------*/
/* Copyright (c) 2008-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj

/**
  * Interface for speed controlling devices.
  */
trait SpeedController /*extends PIDOutput*/ {
  /**
    * Common interface for setting the speed of a speed controller.
    *
    * @param speed The speed to set. Value should be between -1.0 and 1.0.
    */
  def set(speed: Double): Unit

  /**
    * Common interface for getting the current set speed of a speed controller.
    *
    * @return The current set speed. Value is between -1.0 and 1.0.
    */
  def get: Double

  /**
    * Common interface for inverting direction of a speed controller.
    *
    * @param isInverted The state of inversion true is inverted.
    */
  def setInverted(isInverted: Boolean): Unit

  /**
    * Common interface for returning if a speed controller is in the inverted state or not.
    *
    * @return isInverted The state of the inversion true is inverted.
    */
  def getInverted: Boolean

  /**
    * Disable the speed controller.
    */
  def disable(): Unit

  /**
    * Stops motor movement. Motor can be moved again by calling set without having to re-enable the
    * motor.
    */
  def stopMotor(): Unit
}
