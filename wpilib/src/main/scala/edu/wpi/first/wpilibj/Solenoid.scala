/*----------------------------------------------------------------------------*/
/* Copyright (c) 2008-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj

import edu.wpi.first.wpilibj.hal.FRCNetComm.tResourceType
import edu.wpi.first.wpilibj.hal.HAL
import edu.wpi.first.wpilibj.hal.SolenoidJNI
//import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder

/**
  * Solenoid class for running high voltage Digital Output on the PCM.
  *
  * <p>The Solenoid class is typically used for pneumatic solenoids, but could be used for any
  * device within the current spec of the PCM.
  *
  * Constructor.
  *
  * @param moduleNumber The CAN ID of the PCM the solenoid is attached to.
  * @param channel      The channel on the PCM to control (0..7).
  */
class Solenoid(val moduleNumber: Int,
               val m_channel: Int // The channel to control.
              ) extends SolenoidBase(moduleNumber)/* with SendableBase */ {
  private var m_solenoidHandle = 0

  SensorBase.checkSolenoidModule(m_moduleNumber)
  SensorBase.checkSolenoidChannel(m_channel)
  val portHandle: Int = SolenoidJNI.getPortWithModule(m_moduleNumber.asInstanceOf[Byte], m_channel.toByte)
  m_solenoidHandle = SolenoidJNI.initializeSolenoidPort(portHandle)
  HAL.report(tResourceType.kResourceType_Solenoid, m_channel, m_moduleNumber)
  //  setName("Solenoid", m_moduleNumber, m_channel)

  /**
    * Constructor using the default PCM ID (defaults to 0).
    *
    * @param channel The channel on the PCM to control (0..7).
    */
  def this(channel: Int) {
    this(SensorBase.getDefaultSolenoidModule, channel)
  }

  /**
    * Destructor.
    */
  def free(): Unit = {
//    super.free()
    SolenoidJNI.freeSolenoidPort(m_solenoidHandle)
    m_solenoidHandle = 0
  }

  /**
    * Set the value of a solenoid.
    *
    * @param on True will turn the solenoid output on. False will turn the solenoid output off.
    */
  def set(on: Boolean): Unit = {
    SolenoidJNI.setSolenoid(m_solenoidHandle, on)
  }

  /**
    * Read the current value of the solenoid.
    *
    * @return True if the solenoid output is on or false if the solenoid output is off.
    */
  def get: Boolean = SolenoidJNI.getSolenoid(m_solenoidHandle)

  /**
    * Check if solenoid is blacklisted. If a solenoid is shorted, it is added to the blacklist and
    * disabled until power cycle, or until faults are cleared.
    *
    * @return If solenoid is disabled due to short.
    * @see #clearAllPCMStickyFaults()
    */
  def isBlackListed: Boolean = {
    val value = getPCMSolenoidBlackList & (1 << m_channel)
    value != 0
  }

  /**
    * Set the pulse duration in the PCM. This is used in conjunction with
    * the startPulse method to allow the PCM to control the timing of a pulse.
    * The timing can be controlled in 0.01 second increments.
    *
    * @param durationSeconds The duration of the pulse, from 0.01 to 2.55 seconds.
    * @see #startPulse()
    */
  def setPulseDuration(durationSeconds: Double): Unit = {
    val durationMS = (durationSeconds * 1000).toLong
    SolenoidJNI.setOneShotDuration(m_solenoidHandle, durationMS)
  }

  /**
    * Trigger the PCM to generate a pulse of the duration set in
    * setPulseDuration.
    *
    * @see #setPulseDuration(double)
    */
  def startPulse(): Unit = {
    SolenoidJNI.fireOneShot(m_solenoidHandle)
  }

//  def initSendable(builder: SendableBuilder): Unit = {
//    builder.setSmartDashboardType("Solenoid")
//    builder.setSafeState(() => set(false))
//    builder.addBooleanProperty("Value", this.get, this.set)
//  }
}
