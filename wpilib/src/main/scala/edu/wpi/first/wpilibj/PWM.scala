/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008-2017. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj

import edu.wpi.first.wpilibj.hal.DIOJNI
import edu.wpi.first.wpilibj.hal.FRCNetComm.tResourceType
import edu.wpi.first.wpilibj.hal.HAL
import edu.wpi.first.wpilibj.hal.PWMJNI
//import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable
//import edu.wpi.first.wpilibj.tables.ITable
//import edu.wpi.first.wpilibj.tables.ITableListener

/**
  * Allocate a PWM given a channel.
  *
  * @param channel The PWM channel number. 0-9 are on-board, 10-19 are on the MXP port
  */
class PWM(channel: Int) extends SensorBase /*with LiveWindowSendable*/ {
  import SensorBase._ // Shadaj: "extending doesn't bring companion object members into scope like it does for statics"

  checkPWMChannel(channel)
  private var m_channel = channel

  private var m_handle = PWMJNI.initializePWMPort(DIOJNI.getPort(m_channel.toByte))

  setDisabled()

  PWMJNI.setPWMEliminateDeadband(m_handle, false)

  HAL.report(tResourceType.kResourceType_PWM, m_channel)

  /**
    * Free the PWM channel.
    *
    * <p>Free the resource associated with the PWM channel and set the value to 0.
    */
  override def free(): Unit = {
    if (m_handle == 0) return
    setDisabled()
    PWMJNI.freePWMPort(m_handle)
    m_handle = 0
  }

  /**
    * Optionally eliminate the deadband from a speed controller.
    *
    * @param eliminateDeadband If true, set the motor curve on the Jaguar to eliminate the deadband
    *                          in the middle of the range. Otherwise, keep the full range without
    *                          modifying any values.
    */
  def enableDeadbandElimination(eliminateDeadband: Boolean): Unit = {
    PWMJNI.setPWMEliminateDeadband(m_handle, eliminateDeadband)
  }

  /**
    * Set the bounds on the PWM values. This sets the bounds on the PWM values for a particular each
    * type of controller. The values determine the upper and lower speeds as well as the deadband
    * bracket.
    *
    * @param max         The Minimum pwm value
    * @param deadbandMax The high end of the deadband range
    * @param center      The center speed (off)
    * @param deadbandMin The low end of the deadband range
    * @param min         The minimum pwm value
    * @deprecated Recommended to set bounds in ms using { @link
  }
    */
  @deprecated def setRawBounds(max: Int, deadbandMax: Int, center: Int, deadbandMin: Int, min: Int): Unit = {
    PWMJNI.setPWMConfigRaw(m_handle, max, deadbandMax, center, deadbandMin, min)
  }

  /**
    * Set the bounds on the PWM pulse widths. This sets the bounds on the PWM values for a particular
    * type of controller. The values determine the upper and lower speeds as well as the deadband
    * bracket.
    *
    * @param max         The max PWM pulse width in ms
    * @param deadbandMax The high end of the deadband range pulse width in ms
    * @param center      The center (off) pulse width in ms
    * @param deadbandMin The low end of the deadband pulse width in ms
    * @param min         The minimum pulse width in ms
    */
  def setBounds(max: Double, deadbandMax: Double, center: Double, deadbandMin: Double, min: Double): Unit = {
    PWMJNI.setPWMConfig(m_handle, max, deadbandMax, center, deadbandMin, min)
  }

//  /**
//    * Gets the bounds on the PWM pulse widths. This Gets the bounds on the PWM values for a
//    * particular type of controller. The values determine the upper and lower speeds as well
//    * as the deadband bracket.
//    */
//  def getRawBounds: PWMConfigDataResult = PWMJNI.getPWMConfigRaw(m_handle)
  // Shadaj: "creating objects in JNI, oh dear"

  /**
    * Gets the channel number associated with the PWM Object.
    *
    * @return The channel number.
    */
  def getChannel: Int = m_channel

  /**
    * Set the PWM value based on a position.
    *
    * <p>This is intended to be used by servos.
    *
    * @param pos The position to set the servo between 0.0 and 1.0.
    * @pre SetMaxPositivePwm() called.
    * @pre SetMinNegativePwm() called.
    */
  def setPosition(pos: Double): Unit = {
    PWMJNI.setPWMPosition(m_handle, pos)
  }

  /**
    * Get the PWM value in terms of a position.
    *
    * <p>This is intended to be used by servos.
    *
    * @return The position the servo is set to between 0.0 and 1.0.
    * @pre SetMaxPositivePwm() called.
    * @pre SetMinNegativePwm() called.
    */
  def getPosition: Double = PWMJNI.getPWMPosition(m_handle)

  /**
    * Set the PWM value based on a speed.
    *
    * <p>This is intended to be used by speed controllers.
    *
    * @param speed The speed to set the speed controller between -1.0 and 1.0.
    * @pre SetMaxPositivePwm() called.
    * @pre SetMinPositivePwm() called.
    * @pre SetCenterPwm() called.
    * @pre SetMaxNegativePwm() called.
    * @pre SetMinNegativePwm() called.
    */
  def setSpeed(speed: Double): Unit = {
    PWMJNI.setPWMSpeed(m_handle, speed)
  }

  /**
    * Get the PWM value in terms of speed.
    *
    * <p>This is intended to be used by speed controllers.
    *
    * @return The most recently set speed between -1.0 and 1.0.
    * @pre SetMaxPositivePwm() called.
    * @pre SetMinPositivePwm() called.
    * @pre SetMaxNegativePwm() called.
    * @pre SetMinNegativePwm() called.
    */
  def getSpeed: Double = PWMJNI.getPWMSpeed(m_handle)

  /**
    * Set the PWM value directly to the hardware.
    *
    * <p>Write a raw value to a PWM channel.
    *
    * @param value Raw PWM value. Range 0 - 255.
    */
  def setRaw(value: Int): Unit = {
    PWMJNI.setPWMRaw(m_handle, value.toShort)
  }

  /**
    * Get the PWM value directly from the hardware.
    *
    * <p>Read a raw value from a PWM channel.
    *
    * @return Raw PWM control value. Range: 0 - 255.
    */
  def getRaw: Int = PWMJNI.getPWMRaw(m_handle)

  /**
    * Temporarily disables the PWM output. The next set call will reenable
    * the output.
    */
  def setDisabled(): Unit = {
    PWMJNI.setPWMDisabled(m_handle)
  }

  /**
    * Slow down the PWM signal for old devices.
    *
    * @param mult The period multiplier to apply to this channel
    */
  def setPeriodMultiplier(mult: PWM.PeriodMultiplier): Unit = {
    mult match {
      case PWM.PeriodMultiplier.k4X =>
        // Squelch 3 out of 4 outputs
        PWMJNI.setPWMPeriodScale(m_handle, 3)
      case PWM.PeriodMultiplier.k2X =>
        // Squelch 1 out of 2 outputs
        PWMJNI.setPWMPeriodScale(m_handle, 1)
      case PWM.PeriodMultiplier.k1X =>
        // Don't squelch any outputs
        PWMJNI.setPWMPeriodScale(m_handle, 0)
      case _ => // Cannot hit this, limited by PeriodMultiplier enum
    }
  }

  protected def setZeroLatch(): Unit = {
    PWMJNI.latchPWMZero(m_handle)
  }

//  /*
//   * Live Window code, only does anything if live window is activated.
//   */
//  def getSmartDashboardType = "Speed Controller"
//
//  private var m_table = null
//  private var m_tableListener = null
//
//  def initTable(subtable: Nothing): Unit = {
//    m_table = subtable
//    updateTable()
//  }
//
//  def updateTable(): Unit = {
//    if (m_table != null) m_table.putNumber("Value", getSpeed)
//  }
//
//  def getTable: Nothing = m_table
//
//  def startLiveWindowMode(): Unit = {
//    setSpeed(0) // Stop for safety
//
//    m_tableListener = new Nothing() {
//      def valueChanged(itable: Nothing, key: String, value: Any, bln: Boolean): Unit = {
//        setSpeed(value.asInstanceOf[Double])
//      }
//    }
//    m_table.addTableListener("Value", m_tableListener, true)
//  }
//
//  def stopLiveWindowMode(): Unit = {
//    setSpeed(0)
//    // TODO: Broken, should only remove the listener from "Value" only.
//    m_table.removeTableListener(m_tableListener)
//  }
}

/**
  * Class implements the PWM generation in the FPGA.
  *
  * <p>The values supplied as arguments for PWM outputs range from -1.0 to 1.0. They are mapped to
  * the hardware dependent values, in this case 0-2000 for the FPGA. Changes are immediately sent to
  * the FPGA, and the update occurs at the next FPGA cycle. There is no delay.
  *
  * <p>As of revision 0.1.10 of the FPGA, the FPGA interprets the 0-2000 values as follows: - 2000 =
  * maximum pulse width - 1999 to 1001 = linear scaling from "full forward" to "center" - 1000 =
  * center value - 999 to 2 = linear scaling from "center" to "full reverse" - 1 = minimum pulse
  * width (currently .5ms) - 0 = disabled (i.e. PWM output is held low)
  */
object PWM {
  /**
    * Represents the amount to multiply the minimum servo-pulse pwm period by.
    */
  sealed trait PeriodMultiplier

  object PeriodMultiplier {
    /**
      * Period Multiplier: don't skip pulses.
      */
    case object k1X extends PeriodMultiplier

    /**
      * Period Multiplier: skip every other pulse.
      */
    case object k2X extends PeriodMultiplier

    /**
      * Period Multiplier: skip three out of four pulses.
      */
    case object k4X extends PeriodMultiplier
  }
}
