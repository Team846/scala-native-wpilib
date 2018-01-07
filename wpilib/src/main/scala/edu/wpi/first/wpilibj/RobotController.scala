/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj

//import edu.wpi.first.wpilibj.can.CANJNI
//import edu.wpi.first.wpilibj.can.CANStatus
import edu.wpi.first.wpilibj.hal.HAL
import edu.wpi.first.wpilibj.hal.HALUtil
import edu.wpi.first.wpilibj.hal.PowerJNI

/**
  * Contains functions for roboRIO functionality.
  */
object RobotController {
  /**
    * Return the FPGA Version number. For now, expect this to be the current
    * year.
    *
    * @return FPGA Version number.
    */
  @SuppressWarnings(Array("AbbreviationAsWordInName")) def getFPGAVersion: Int = HALUtil.getFPGAVersion

  /**
    * Return the FPGA Revision number. The format of the revision is 3 numbers. The 12 most
    * significant bits are the Major Revision. the next 8 bits are the Minor Revision. The 12 least
    * significant bits are the Build Number.
    *
    * @return FPGA Revision number.
    */
  @SuppressWarnings(Array("AbbreviationAsWordInName")) def getFPGARevision: Long = HALUtil.getFPGARevision.toLong

  /**
    * Read the microsecond timer from the FPGA.
    *
    * @return The current time in microseconds according to the FPGA.
    */
  def getFPGATime: Long = HALUtil.getFPGATime

  /**
    * Get the state of the "USER" button on the roboRIO.
    *
    * @return true if the button is currently pressed down
    */
  def getUserButton: Boolean = HALUtil.getFPGAButton

  /**
    * Read the battery voltage.
    *
    * @return The battery voltage in Volts.
    */
  def getBatteryVoltage: Double = PowerJNI.getVinVoltage

  /**
    * Gets a value indicating whether the FPGA outputs are enabled. The outputs may be disabled if
    * the robot is disabled or e-stopped, the watchdog has expired, or if the roboRIO browns out.
    *
    * @return True if the FPGA outputs are enabled.
    */
  def isSysActive: Boolean = HAL.getSystemActive

  /**
    * Check if the system is browned out.
    *
    * @return True if the system is browned out
    */
  def isBrownedOut: Boolean = HAL.getBrownedOut

  /**
    * Get the input voltage to the robot controller.
    *
    * @return The controller input voltage value in Volts
    */
  def getInputVoltage: Double = PowerJNI.getVinVoltage

  /**
    * Get the input current to the robot controller.
    *
    * @return The controller input current value in Amps
    */
  def getInputCurrent: Double = PowerJNI.getVinCurrent

  /**
    * Get the voltage of the 3.3V rail.
    *
    * @return The controller 3.3V rail voltage value in Volts
    */
  def getVoltage3V3: Double = PowerJNI.getUserVoltage3V3

  /**
    * Get the current output of the 3.3V rail.
    *
    * @return The controller 3.3V rail output current value in Volts
    */
  def getCurrent3V3: Double = PowerJNI.getUserCurrent3V3

  /**
    * Get the enabled state of the 3.3V rail. The rail may be disabled due to a controller brownout,
    * a short circuit on the rail, or controller over-voltage.
    *
    * @return The controller 3.3V rail enabled value
    */
  def getEnabled3V3: Boolean = PowerJNI.getUserActive3V3

  /**
    * Get the count of the total current faults on the 3.3V rail since the controller has booted.
    *
    * @return The number of faults
    */
  def getFaultCount3V3: Int = PowerJNI.getUserCurrentFaults3V3

  /**
    * Get the voltage of the 5V rail.
    *
    * @return The controller 5V rail voltage value in Volts
    */
  def getVoltage5V: Double = PowerJNI.getUserVoltage5V

  /**
    * Get the current output of the 5V rail.
    *
    * @return The controller 5V rail output current value in Amps
    */
  def getCurrent5V: Double = PowerJNI.getUserCurrent5V

  /**
    * Get the enabled state of the 5V rail. The rail may be disabled due to a controller brownout, a
    * short circuit on the rail, or controller over-voltage.
    *
    * @return The controller 5V rail enabled value
    */
  def getEnabled5V: Boolean = PowerJNI.getUserActive5V

  /**
    * Get the count of the total current faults on the 5V rail since the controller has booted.
    *
    * @return The number of faults
    */
  def getFaultCount5V: Int = PowerJNI.getUserCurrentFaults5V

  /**
    * Get the voltage of the 6V rail.
    *
    * @return The controller 6V rail voltage value in Volts
    */
  def getVoltage6V: Double = PowerJNI.getUserVoltage6V

  /**
    * Get the current output of the 6V rail.
    *
    * @return The controller 6V rail output current value in Amps
    */
  def getCurrent6V: Double = PowerJNI.getUserCurrent6V

  /**
    * Get the enabled state of the 6V rail. The rail may be disabled due to a controller brownout, a
    * short circuit on the rail, or controller over-voltage.
    *
    * @return The controller 6V rail enabled value
    */
  def getEnabled6V: Boolean = PowerJNI.getUserActive6V

  /**
    * Get the count of the total current faults on the 6V rail since the controller has booted.
    *
    * @return The number of faults
    */
  def getFaultCount6V: Int = PowerJNI.getUserCurrentFaults6V

//  /**
//    * Get the current status of the CAN bus.
//    *
//    * @return The status of the CAN bus
//    */
//  def getCANStatus: Nothing = {
//    val status = new Nothing
//    CANJNI.GetCANStatus(status)
//    status
//  }
}
