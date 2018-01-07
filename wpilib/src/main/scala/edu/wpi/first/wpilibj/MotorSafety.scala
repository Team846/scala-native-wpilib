/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008-2018. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj

trait MotorSafety {
  def setExpiration(timeout: Double): Unit

  def getExpiration: Double

  def isAlive: Boolean

  def stopMotor(): Unit

  def setSafetyEnabled(enabled: Boolean): Unit

  def isSafetyEnabled: Boolean

  def getDescription: String
}

/**
  * Shuts off motors when their outputs aren't updated often enough.
  */
object MotorSafety {
  val DEFAULT_SAFETY_EXPIRATION = 0.1
}
