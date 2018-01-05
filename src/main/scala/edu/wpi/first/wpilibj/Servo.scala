/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008-2017. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj

import edu.wpi.first.wpilibj.hal.FRCNetComm.tResourceType
import edu.wpi.first.wpilibj.hal.HAL
//import edu.wpi.first.wpilibj.livewindow.LiveWindow
//import edu.wpi.first.wpilibj.tables.ITable
//import edu.wpi.first.wpilibj.tables.ITableListener

class Servo(val channel: Int)

/**
  * Constructor.<br>
  *
  * <p>By default {@value #kDefaultMaxServoPWM} ms is used as the maxPWM value<br> By default
  * {@value #kDefaultMinServoPWM} ms is used as the minPWM value<br>
  *
  * @param channel The PWM channel to which the servo is attached. 0-9 are on-board, 10-19 are on
  *                the MXP port
  */
  extends PWM(channel) {
  setBounds(Servo.kDefaultMaxServoPWM, 0, 0, 0, Servo.kDefaultMinServoPWM)
  setPeriodMultiplier(PWM.PeriodMultiplier.k4X)
//  LiveWindow.addActuator("Servo", getChannel, this)
  HAL.report(tResourceType.kResourceType_Servo, getChannel)

  /**
    * Set the servo position.
    *
    * <p>Servo values range from 0.0 to 1.0 corresponding to the range of full left to full right.
    *
    * @param value Position from 0.0 to 1.0.
    */
  def set(value: Double): Unit = {
    setPosition(value)
  }

  /**
    * Get the servo position.
    *
    * <p>Servo values range from 0.0 to 1.0 corresponding to the range of full left to full right.
    *
    * @return Position from 0.0 to 1.0.
    */
  def get: Double = getPosition

  /**
    * Set the servo angle.
    *
    * <p>Assume that the servo angle is linear with respect to the PWM value (big assumption, need to
    * test).
    *
    * <p>Servo angles that are out of the supported range of the servo simply "saturate" in that
    * direction In other words, if the servo has a range of (X degrees to Y degrees) than angles of
    * less than X result in an angle of X being set and angles of more than Y degrees result in an
    * angle of Y being set.
    *
    * @param degrees The angle in degrees to set the servo.
    */
  def setAngle(_degrees: Double): Unit = {
    var degrees = _degrees
    if (degrees < Servo.kMinServoAngle) degrees = Servo.kMinServoAngle
    else if (degrees > Servo.kMaxServoAngle) degrees = Servo.kMaxServoAngle
    setPosition((degrees - Servo.kMinServoAngle) / getServoAngleRange)
  }

  /**
    * Get the servo angle.
    *
    * <p>Assume that the servo angle is linear with respect to the PWM value (big assumption, need to
    * test).
    *
    * @return The angle in degrees to which the servo is set.
    */
  def getAngle: Double = getPosition * getServoAngleRange + Servo.kMinServoAngle

  private def getServoAngleRange = Servo.kMaxServoAngle - Servo.kMinServoAngle

//  /*
//     * Live Window code, only does anything if live window is activated.
//     */ def getSmartDashboardType = "Servo"
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
//    if (m_table != null) m_table.putNumber("Value", get)
//  }
//
//  def startLiveWindowMode(): Unit = {
//    m_tableListener = new Nothing() {
//      def valueChanged(itable: Nothing, key: String, value: Any, bln: Boolean): Unit = {
//        set(value.asInstanceOf[Double].doubleValue)
//      }
//    }
//    m_table.addTableListener("Value", m_tableListener, true)
//  }
//
//  def stopLiveWindowMode(): Unit = { // TODO: Broken, should only remove the listener from "Value" only.
//    m_table.removeTableListener(m_tableListener)
//  }
}

/**
  * Standard hobby style servo.
  *
  * <p>The range parameters default to the appropriate values for the Hitec HS-322HD servo provided
  * in the FIRST Kit of Parts in 2008.
  */
object Servo {
  private val kMaxServoAngle = 180.0
  private val kMinServoAngle = 0.0
  protected val kDefaultMaxServoPWM = 2.4
  protected val kDefaultMinServoPWM = .6
}
