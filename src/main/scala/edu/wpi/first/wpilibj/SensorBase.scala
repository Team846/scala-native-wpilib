/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008-2017. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj

/**
  * Base class for all sensors. Stores most recent status information as well as containing utility
  * functions for checking channels and error processing.
  */
object SensorBase {
  /**
    * Ticks per microsecond.
    */
  val kSystemClockTicksPerMicrosecond = 40
  /**
    * Number of digital channels per roboRIO.
    */
  val kDigitalChannels = 26
  /**
    * Number of analog input channels.
    */
  val kAnalogInputChannels = 8
  /**
    * Number of analog output channels.
    */
  val kAnalogOutputChannels = 2
  /**
    * Number of solenoid channels per module.
    */
  val kSolenoidChannels = 8
  /**
    * Number of solenoid modules.
    */
  val kSolenoidModules = 2
  /**
    * Number of PWM channels per roboRIO.
    */
  val kPwmChannels = 20
  /**
    * Number of relay channels per roboRIO.
    */
  val kRelayChannels = 4
  /**
    * Number of power distribution channels.
    */
  val kPDPChannels = 16
  /**
    * Number of power distribution modules.
    */
  val kPDPModules = 63
  private var m_defaultSolenoidModule = 0

  /**
    * Set the default location for the Solenoid module.
    *
    * @param moduleNumber The number of the solenoid module to use.
    */
  def setDefaultSolenoidModule(moduleNumber: Int): Unit = {
    checkSolenoidModule(moduleNumber)
    SensorBase.m_defaultSolenoidModule = moduleNumber
  }

  /**
    * Verify that the solenoid module is correct.
    *
    * @param moduleNumber The solenoid module module number to check.
    */
  private[wpilibj] def checkSolenoidModule(moduleNumber: Int): Unit = {
  }

  // Shadaj: "I had to change this from protected since Java has protected statics but Scala doesn't

  /**
    * Check that the digital channel number is valid. Verify that the channel number is one of the
    * legal channel numbers. Channel numbers are 1-based.
    *
    * @param channel The channel number to check.
    */
  private[wpilibj] def checkDigitalChannel(channel: Int): Unit = {
    if (channel < 0 || channel >= kDigitalChannels) throw new IndexOutOfBoundsException("Requested digital channel number is out of range.")
  }

  private[wpilibj] def checkRelayChannel(channel: Int): Unit = {
    if (channel < 0 || channel >= kRelayChannels) throw new IndexOutOfBoundsException("Requested relay channel number is out of range.")
  }

  private[wpilibj] def checkPWMChannel(channel: Int): Unit = {
    if (channel < 0 || channel >= kPwmChannels) throw new IndexOutOfBoundsException("Requested PWM channel number is out of range.")
  }

  /**
    * Check that the analog input number is value. Verify that the analog input number is one of the
    * legal channel numbers. Channel numbers are 0-based.
    *
    * @param channel The channel number to check.
    */
  private[wpilibj] def checkAnalogInputChannel(channel: Int): Unit = {
    if (channel < 0 || channel >= kAnalogInputChannels) throw new IndexOutOfBoundsException("Requested analog input channel number is out of range.")
  }

  private[wpilibj] def checkAnalogOutputChannel(channel: Int): Unit = {
    if (channel < 0 || channel >= kAnalogOutputChannels) throw new IndexOutOfBoundsException("Requested analog output channel number is out of range.")
  }

  /**
    * Verify that the solenoid channel number is within limits. Channel numbers are 1-based.
    *
    * @param channel The channel number to check.
    */
  private[wpilibj] def checkSolenoidChannel(channel: Int): Unit = {
    if (channel < 0 || channel >= kSolenoidChannels) throw new IndexOutOfBoundsException("Requested solenoid channel number is out of range.")
  }

  /**
    * Verify that the power distribution channel number is within limits. Channel numbers are
    * 1-based.
    *
    * @param channel The channel number to check.
    */
  private[wpilibj] def checkPDPChannel(channel: Int): Unit = {
    if (channel < 0 || channel >= kPDPChannels) throw new IndexOutOfBoundsException("Requested PDP channel number is out of range.")
  }

  /**
    * Verify that the PDP module number is within limits. module numbers are 0-based.
    *
    * @param module The module number to check.
    */
  private[wpilibj] def checkPDPModule(module: Int): Unit = {
    if (module < 0 || module > kPDPModules) throw new IndexOutOfBoundsException("Requested PDP module number is out of range.")
  }

  /**
    * Get the number of the default solenoid module.
    *
    * @return The number of the default solenoid module.
    */
  def getDefaultSolenoidModule: Int = SensorBase.m_defaultSolenoidModule
}

/**
  * Creates an instance of the sensor base and gets an FPGA handle.
  */
abstract class SensorBase {
  /**
    * Free the resources used by this object.
    */
  def free(): Unit = {
  }
}
