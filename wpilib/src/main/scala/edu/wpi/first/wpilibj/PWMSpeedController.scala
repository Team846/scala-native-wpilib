/*----------------------------------------------------------------------------*/
/* Copyright (c) 2008-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj

/**
  * Common base class for all PWM Speed Controllers.
  *
  * Constructor.
  *
  * @param channel The PWM channel that the controller is attached to. 0-9 are on-board, 10-19 are
  *                on the MXP port
  */
abstract class PWMSpeedController protected(channel: Int) extends SafePWM(channel) with SpeedController {
  private var m_isInverted = false

  /**
    * Set the PWM value.
    *
    * <p>The PWM value is set using a range of -1.0 to 1.0, appropriately scaling the value for the
    * FPGA.
    *
    * @param speed The speed value between -1.0 and 1.0 to set.
    */
  override def set(speed: Double): Unit = {
    setSpeed(if (m_isInverted) -speed
    else speed)
    feed()
  }

  /**
    * Get the recently set value of the PWM.
    *
    * @return The most recently set value for the PWM between -1.0 and 1.0.
    */
  override def get: Double = getSpeed

  override def setInverted(isInverted: Boolean): Unit = {
    m_isInverted = isInverted
  }

  override def getInverted: Boolean = m_isInverted

  /**
    * Write out the PID value as seen in the PIDOutput base object.
    *
    * @param output Write out the PWM value as was found in the PIDController
    */
  def pidWrite(output: Double): Unit = {
    set(output)
  }
}
