/*----------------------------------------------------------------------------*/
/* Copyright (c) 2014-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/


package edu.wpi.first.wpilibj

import edu.wpi.first.wpilibj.hal.PDPJNI
//import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder

/**
  * Class for getting voltage, current, temperature, power and energy from the Power Distribution
  * Panel over CAN.
  *
  * Constructor.
  *
  * @param module The CAN ID of the PDP
  */
class PowerDistributionPanel(val m_module: Int) /*extends SendableBuilder*/ {
  import SensorBase._

  checkPDPModule(m_module)
  PDPJNI.initializePDP(m_module)
  //setName("PowerDistributionPanel", m_module)

  /**
    * Constructor.  Uses the default CAN ID (0).
    */
  def this() = {
    this(0)
  }

  /**
    * Query the input voltage of the PDP.
    *
    * @return The voltage of the PDP in volts
    */
  def getVoltage: Double = PDPJNI.getPDPVoltage(m_module)

  /**
    * Query the temperature of the PDP.
    *
    * @return The temperature of the PDP in degrees Celsius
    */
  def getTemperature: Double = PDPJNI.getPDPTemperature(m_module)

  /**
    * Query the current of a single channel of the PDP.
    *
    * @return The current of one of the PDP channels (channels 0-15) in Amperes
    */
  def getCurrent(channel: Int): Double = {
    val current = PDPJNI.getPDPChannelCurrent(channel.toByte, m_module)
    checkPDPChannel(channel)
    current
  }

  /**
    * Query the current of all monitored PDP channels (0-15).
    *
    * @return The current of all the channels in Amperes
    */
  def getTotalCurrent: Double = PDPJNI.getPDPTotalCurrent(m_module)

  /**
    * Query the total power drawn from the monitored PDP channels.
    *
    * @return the total power in Watts
    */
  def getTotalPower: Double = PDPJNI.getPDPTotalPower(m_module)

  /**
    * Query the total energy drawn from the monitored PDP channels.
    *
    * @return the total energy in Joules
    */
  def getTotalEnergy: Double = PDPJNI.getPDPTotalEnergy(m_module)

  /**
    * Reset the total energy to 0.
    */
  def resetTotalEnergy(): Unit = PDPJNI.resetPDPTotalEnergy(m_module)

  /**
    * Clear all PDP sticky faults.
    */
  def clearStickyFaults(): Unit = PDPJNI.clearPDPStickyFaults(m_module)

//  def initSendable(builder: Nothing): Unit = {
//    builder.setSmartDashboardType("PowerDistributionPanel")
//    var i = 0
//    while ( {
//      i < kPDPChannels
//    }) {
//      val chan = i
//      builder.addDoubleProperty("Chan" + i, () => getCurrent(chan), null)
//
//      {
//        i += 1; i
//      }
//    }
//    builder.addDoubleProperty("Voltage", this.getVoltage, null)
//    builder.addDoubleProperty("TotalCurrent", this.getTotalCurrent, null)
//  }
}
