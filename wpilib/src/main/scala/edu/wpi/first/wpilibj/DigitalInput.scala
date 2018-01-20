/*----------------------------------------------------------------------------*/
/* Copyright (c) 2008-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj

import edu.wpi.first.wpilibj.hal.DIOJNI
import edu.wpi.first.wpilibj.hal.FRCNetComm.tResourceType
import edu.wpi.first.wpilibj.hal.HAL
//import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder

/**
  * Class to read a digital input. This class will read digital inputs and return the current value
  * on the channel. Other devices such as encoders, gear tooth sensors, etc. that are implemented
  * elsewhere will automatically allocate digital inputs and outputs as required. This class is only
  * for devices like switches etc. that aren't implemented anywhere else.
  *
  * Create an instance of a Digital Input class. Creates a digital input given a channel.
  *
  * @param channel the DIO channel for the digital input 0-9 are on-board, 10-25 are on the MXP
  */
class DigitalInput(val channel: Int) extends DigitalSource /*with Sendable*/ {
  private var m_channel = 0
  private var m_handle = 0

  import SensorBase._
  checkDigitalChannel(channel)
  m_channel = channel
  m_handle = DIOJNI.initializeDIOPort(DIOJNI.getPort(channel.toByte), true)
  HAL.report(tResourceType.kResourceType_DigitalInput, channel)
  //  setName("DigitalInput", channel)

  /**
    * Frees the resources for this output.
    */
  override def free(): Unit = {
    super.free()
    if (m_interrupt != 0) cancelInterrupts()
    DIOJNI.freeDIOPort(m_handle)
  }

  /**
    * Get the value from a digital input channel. Retrieve the value of a single digital input
    * channel from the FPGA.
    *
    * @return the status of the digital input
    */
  def get: Boolean = DIOJNI.getDIO(m_handle)

  /**
    * Get the channel of the digital input.
    *
    * @return The GPIO channel number that this object represents.
    */
  override def getChannel: Int = m_channel

  /**
    * Get the analog trigger type.
    *
    * @return false
    */
  override def getAnalogTriggerTypeForRouting = 0

  /**
    * Is this an analog trigger.
    *
    * @return true if this is an analog trigger
    */
  override def isAnalogTrigger = false

  /**
    * Get the HAL Port Handle.
    *
    * @return The HAL Handle to the specified source.
    */
  override def getPortHandleForRouting: Int = m_handle

//  def initSendable(builder: SendableBuilder): Unit = {
//    builder.setSmartDashboardType("Digital Input")
//    builder.addBooleanProperty("Value", this.get, null)
//  }
}
