package com.ctre.phoenix.motorcontrol

import com.ctre.phoenix.ErrorCode

trait IMotorControllerEnhanced extends IMotorController {
  //------ Set output routines. ----------//
  /* in parent */

  //------ Invert behavior ----------//
  /* in parent */

  //----- general output shaping ------------------//
  /* in parent */

  //------ Voltage Compensation ----------//
  /* in parent */

  //------ General Status ----------//
  /* in parent */

  //------ sensor selection ----------//
  /* expand the options */
  def configSelectedFeedbackSensor(feedbackDevice: FeedbackDevice, pidIdx: Int, timeoutMs: Int): ErrorCode

  //------- sensor status --------- //
  /* in parent */

  //------ status frame period changes ----------//
  def setStatusFramePeriod(frame: StatusFrameEnhanced, periodMs: Int, timeoutMs: Int): ErrorCode
  def getStatusFramePeriod(frame: StatusFrameEnhanced, timeoutMs: Int): Int

  //----- velocity signal conditionaing ------//
  def configVelocityMeasurementPeriod(period: VelocityMeasPeriod, timeoutMs: Int): ErrorCode
  def configVelocityMeasurementWindow(windowSize: Int, timeoutMs: Int): ErrorCode

  //------ remote limit switch ----------//
  /* in parent */

  //------ local limit switch ----------//
  def configForwardLimitSwitchSource(`type`: LimitSwitchSource, normalOpenOrClose: LimitSwitchNormal, timeoutMs: Int): ErrorCode
  def configReverseLimitSwitchSource(`type`: LimitSwitchSource, normalOpenOrClose: LimitSwitchNormal, timeoutMs: Int): ErrorCode

  //------ soft limit ----------//
  /* in parent */

  //------ Current Lim ----------//
  def configPeakCurrentLimit(amps: Int, timeoutMs: Int): ErrorCode
  def configPeakCurrentDuration(milliseconds: Int, timeoutMs: Int): ErrorCode
  def configContinuousCurrentLimit(amps: Int, timeoutMs: Int): ErrorCode
  def enableCurrentLimit(enable: Boolean): Unit

  //------ General Close loop ----------//
  /* in parent */

  //------ Motion Profile Settings used in Motion Magic and Motion Profile ----------//
  /* in parent */

  //------ Motion Profile Buffer ----------//
  /* in parent */

  //------ error ----------//
  /* in parent */

  //------ Faults ----------//
  /* in parent */

  //------ Firmware ----------//
  /* in parent */

  //------ Custom Persistent Params ----------//
  /* in parent */

  //------ Generic Param API, typically not used ----------//
  /* in parent */

  //------ Misc. ----------//
  /* in parent */

  //------ RAW Sensor API ----------//
  /**
    * @retrieve object that can get/set individual RAW sensor values.
    */
  //SensorCollection SensorCollection { get; }
}
