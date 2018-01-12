/*----------------------------------------------------------------------------*/
/* Copyright (c) 2008-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj

import edu.wpi.first.wpilibj.hal.AnalogJNI
import edu.wpi.first.wpilibj.hal.ConstantsJNI
import edu.wpi.first.wpilibj.hal.DIOJNI
//import edu.wpi.first.wpilibj.hal.PDPJNI
import edu.wpi.first.wpilibj.hal.PWMJNI
import edu.wpi.first.wpilibj.hal.PortsJNI
//import edu.wpi.first.wpilibj.hal.RelayJNI
//import edu.wpi.first.wpilibj.hal.SolenoidJNI

/**
  * Base class for all sensors. Stores most recent status information as well as containing utility
  * functions for checking channels and error processing.
  */
object SensorBase {
  /**
    * Ticks per microsecond.
    */
  val kSystemClockTicksPerMicrosecond: Int = ConstantsJNI.getSystemClockTicksPerMicrosecond
  /**
    * Number of digital channels per roboRIO.
    */
  val kDigitalChannels: Int = PortsJNI.getNumDigitalChannels
  /**
    * Number of analog input channels per roboRIO.
    */
  val kAnalogInputChannels: Int = PortsJNI.getNumAnalogInputs
  /**
    * Number of analog output channels per roboRIO.
    */
  val kAnalogOutputChannels: Int = PortsJNI.getNumAnalogOutputs
  /**
    * Number of solenoid channels per module.
    */
  val kSolenoidChannels: Int = PortsJNI.getNumSolenoidChannels
  /**
    * Number of PWM channels per roboRIO.
    */
  val kPwmChannels: Int = PortsJNI.getNumPWMChannels
  /**
    * Number of relay channels per roboRIO.
    */
  val kRelayChannels: Int = PortsJNI.getNumRelayHeaders
  /**
    * Number of power distribution channels per PDP.
    */
  val kPDPChannels: Int = PortsJNI.getNumPDPChannels
  /**
    * Number of power distribution modules per PDP.
    */
  val kPDPModules: Int = PortsJNI.getNumPDPModules
  /**
    * Number of PCM Modules.
    */
  val kPCMModules: Int = PortsJNI.getNumPCMModules
  private var m_defaultSolenoidModule = 0

//  /**
//    * Set the default location for the Solenoid module.
//    *
//    * @param moduleNumber The number of the solenoid module to use.
//    */
//  def setDefaultSolenoidModule(moduleNumber: Int): Unit = {
//    checkSolenoidModule(moduleNumber)
//    SensorBase.m_defaultSolenoidModule = moduleNumber
//  }

//  /**
//    * Verify that the solenoid module is correct.
//    *
//    * @param moduleNumber The solenoid module module number to check.
//    */
//  def checkSolenoidModule(moduleNumber: Int): Unit = {
//    if (!SolenoidJNI.checkSolenoidModule(moduleNumber)) {
//      val buf = new StringBuilder
//      buf.append("Requested solenoid module is out of range. Minimum: 0, Maximum: ").append(kPCMModules).append(", Requested: ").append(moduleNumber)
//      throw new IndexOutOfBoundsException(buf.toString)
//    }
//  }

  /**
    * Check that the digital channel number is valid. Verify that the channel number is one of the
    * legal channel numbers. Channel numbers are 0-based.
    *
    * @param channel The channel number to check.
    */
  def checkDigitalChannel(channel: Int): Unit = {
    if (!DIOJNI.checkDIOChannel(channel)) {
      val buf = new StringBuilder
      buf.append("Requested DIO channel is out of range. Minimum: 0, Maximum: ").append(kDigitalChannels).append(", Requested: ").append(channel)
      throw new IndexOutOfBoundsException(buf.toString)
    }
  }

//  def checkRelayChannel(channel: Int): Unit = {
//    if (!RelayJNI.checkRelayChannel(channel)) {
//      val buf = new StringBuilder
//      buf.append("Requested relay channel is out of range. Minimum: 0, Maximum: ").append(kRelayChannels).append(", Requested: ").append(channel)
//      throw new IndexOutOfBoundsException(buf.toString)
//    }
//  }

  def checkPWMChannel(channel: Int): Unit = {
    if (!PWMJNI.checkPWMChannel(channel)) {
      val buf = new StringBuilder
      buf.append("Requested PWM channel is out of range. Minimum: 0, Maximum: ").append(kPwmChannels).append(", Requested: ").append(channel)
      throw new IndexOutOfBoundsException(buf.toString)
    }
  }

  /**
    * Check that the analog input number is value. Verify that the analog input number is one of the
    * legal channel numbers. Channel numbers are 0-based.
    *
    * @param channel The channel number to check.
    */
  def checkAnalogInputChannel(channel: Int): Unit = {
    if (!AnalogJNI.checkAnalogInputChannel(channel)) {
      val buf = new StringBuilder
      buf.append("Requested analog input channel is out of range. Minimum: 0, Maximum: ").append(kAnalogInputChannels).append(", Requested: ").append(channel)
      throw new IndexOutOfBoundsException(buf.toString)
    }
  }

  def checkAnalogOutputChannel(channel: Int): Unit = {
    if (!AnalogJNI.checkAnalogOutputChannel(channel)) {
      val buf = new StringBuilder
      buf.append("Requested analog output channel is out of range. Minimum: 0, Maximum: ").append(kAnalogOutputChannels).append(", Requested: ").append(channel)
      throw new IndexOutOfBoundsException(buf.toString)
    }
  }

//  /**
//    * Verify that the solenoid channel number is within limits. Channel numbers are 0-based.
//    *
//    * @param channel The channel number to check.
//    */
//  def checkSolenoidChannel(channel: Int): Unit = {
//    if (!SolenoidJNI.checkSolenoidChannel(channel)) {
//      val buf = new StringBuilder
//      buf.append("Requested solenoid channel is out of range. Minimum: 0, Maximum: ").append(kSolenoidChannels).append(", Requested: ").append(channel)
//      throw new IndexOutOfBoundsException(buf.toString)
//    }
//  }

//  /**
//    * Verify that the power distribution channel number is within limits. Channel numbers are
//    * 0-based.
//    *
//    * @param channel The channel number to check.
//    */
//  def checkPDPChannel(channel: Int): Unit = {
//    if (!PDPJNI.checkPDPChannel(channel)) {
//      val buf = new StringBuilder
//      buf.append("Requested PDP channel is out of range. Minimum: 0, Maximum: ").append(kPDPChannels).append(", Requested: ").append(channel)
//      throw new IndexOutOfBoundsException(buf.toString)
//    }
//  }

//  /**
//    * Verify that the PDP module number is within limits. module numbers are 0-based.
//    *
//    * @param module The module number to check.
//    */
//  def checkPDPModule(module: Int): Unit = {
//    if (!PDPJNI.checkPDPModule(module)) {
//      val buf = new StringBuilder
//      buf.append("Requested PDP module is out of range. Minimum: 0, Maximum: ").append(kPDPModules).append(", Requested: ").append(module)
//      throw new IndexOutOfBoundsException(buf.toString)
//    }
//  }

  /**
    * Get the number of the default solenoid module.
    *
    * @return The number of the default solenoid module.
    */
  def getDefaultSolenoidModule: Int = SensorBase.m_defaultSolenoidModule
}
