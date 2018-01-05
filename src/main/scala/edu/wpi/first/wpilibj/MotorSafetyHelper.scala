/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008-2017. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj

/**
  * The constructor for a MotorSafetyHelper object. The helper object is constructed for every
  * object that wants to implement the Motor Safety protocol. The helper object has the code to
  * actually do the timing and call the motors Stop() method when the timeout expires. The motor
  * object is expected to call the Feed() method whenever the motors value is updated.
  *
  * @param safeObject a pointer to the motor object implementing MotorSafety. This is used to call
  *                   the Stop() method on the motor.
  */
final class MotorSafetyHelper(val m_safeObject: MotorSafety) {
  private var m_enabled = false
  private var m_expiration = MotorSafety.DEFAULT_SAFETY_EXPIRATION
  private var m_stopTime = Timer.getFPGATimestamp
  final private var m_nextHelper = MotorSafetyHelper.headHelper
  MotorSafetyHelper.headHelper = this

  /**
    * Feed the motor safety object. Resets the timer on this object that is used to do the timeouts.
    */
  def feed(): Unit = {
    m_stopTime = Timer.getFPGATimestamp + m_expiration
  }

  /**
    * Set the expiration time for the corresponding motor safety object.
    *
    * @param expirationTime The timeout value in seconds.
    */
  def setExpiration(expirationTime: Double): Unit = {
    m_expiration = expirationTime
  }

  /**
    * Retrieve the timeout value for the corresponding motor safety object.
    *
    * @return the timeout value in seconds.
    */
  def getExpiration: Double = m_expiration

  /**
    * Determine of the motor is still operating or has timed out.
    *
    * @return a true value if the motor is still operating normally and hasn't timed out.
    */
  def isAlive: Boolean = !m_enabled || m_stopTime > Timer.getFPGATimestamp

  /**
    * Check if this motor has exceeded its timeout. This method is called periodically to determine
    * if this motor has exceeded its timeout value. If it has, the stop method is called, and the
    * motor is shut down until its value is updated again.
    */
  def check(): Unit = {
    if (!m_enabled || RobotState.isDisabled || RobotState.isTest) return
    if (m_stopTime < Timer.getFPGATimestamp) {
      DriverStation.reportError(m_safeObject.getDescription + "... Output not updated often " + "enough.", false)
      m_safeObject.stopMotor()
    }
  }

  /**
    * Enable/disable motor safety for this device Turn on and off the motor safety option for this
    * PWM object.
    *
    * @param enabled True if motor safety is enforced for this object
    */
  def setSafetyEnabled(enabled: Boolean): Unit = {
    m_enabled = enabled
  }

  /**
    * Return the state of the motor safety enabled flag Return if the motor safety is currently
    * enabled for this devicce.
    *
    * @return True if motor safety is enforced for this device
    */
  def isSafetyEnabled: Boolean = m_enabled
}


/**
  * The MotorSafetyHelper object is constructed for every object that wants to implement the Motor
  * Safety protocol. The helper object has the code to actually do the timing and call the motors
  * Stop() method when the timeout expires. The motor object is expected to call the Feed() method
  * whenever the motors value is updated.
  */
object MotorSafetyHelper {
  private var headHelper: MotorSafetyHelper = null

  /**
    * Check the motors to see if any have timed out. This static method is called periodically to
    * poll all the motors and stop any that have timed out.
    */
  // TODO: these should be synchronized with the setting methods in case it's
  // called from a different thread
  def checkMotors(): Unit = {
    var msh = headHelper
    while (msh != null) {
      msh.check()

      msh = msh.m_nextHelper
    }
  }
}