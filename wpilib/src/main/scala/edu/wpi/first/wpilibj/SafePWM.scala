/*----------------------------------------------------------------------------*/
/* Copyright (c) 2008-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj

/**
  * Manages a PWM object.
  *
  * Constructor for a SafePWM object taking a channel number.
  *
  * @param channel The channel number to be used for the underlying PWM object. 0-9 are on-board,
  *                10-19 are on the MXP port.
  */
class SafePWM(val channel: Int) extends PWM(channel) with MotorSafety {
  final private var m_safetyHelper = new MotorSafetyHelper(this)
  m_safetyHelper.setExpiration(0.0)
  m_safetyHelper.setSafetyEnabled(false)

  /**
    * Set the expiration time for the PWM object.
    *
    * @param timeout The timeout (in seconds) for this motor object
    */
  override def setExpiration(timeout: Double): Unit = {
    m_safetyHelper.setExpiration(timeout)
  }

  /**
    * Return the expiration time for the PWM object.
    *
    * @return The expiration time value.
    */
  override def getExpiration: Double = m_safetyHelper.getExpiration

  /**
    * Check if the PWM object is currently alive or stopped due to a timeout.
    *
    * @return a bool value that is true if the motor has NOT timed out and should still be running.
    */
  override def isAlive: Boolean = m_safetyHelper.isAlive

  /**
    * Stop the motor associated with this PWM object. This is called by the MotorSafetyHelper object
    * when it has a timeout for this PWM and needs to stop it from running.
    */
  override def stopMotor(): Unit = {
    disable()
  }

  /**
    * Check if motor safety is enabled for this object.
    *
    * @return True if motor safety is enforced for this object
    */
  override def isSafetyEnabled: Boolean = m_safetyHelper.isSafetyEnabled

  /**
    * Feed the MotorSafety timer. This method is called by the subclass motor whenever it updates its
    * speed, thereby resetting the timeout value.
    *
    * @deprecated Use { @link #feed()} instead.
    */
  @deprecated
  @SuppressWarnings(Array("MethodName")) def Feed(): Unit = {
    feed()
  }

  /**
    * Feed the MotorSafety timer. This method is called by the subclass motor whenever it updates its
    * speed, thereby resetting the timeout value.
    */
  def feed(): Unit = {
    m_safetyHelper.feed()
  }

  override def setSafetyEnabled(enabled: Boolean): Unit = {
    m_safetyHelper.setSafetyEnabled(enabled)
  }

  override def getDescription: String = "PWM " + getChannel

  def disable(): Unit = {
    setDisabled()
  }
}
