package com.ctre.phoenix.motorcontrol

import com.ctre.phoenix.ErrorCode
import com.ctre.phoenix.ParamEnum
import com.ctre.phoenix.motion.MotionProfileStatus
import com.ctre.phoenix.motion.TrajectoryPoint

trait IMotorController extends com.ctre.phoenix.signals.IOutputSignal with com.ctre.phoenix.signals.IInvertable with IFollower { // ------ Set output routines. ----------//
  def set(Mode: ControlMode, demand: Double): Unit

  def set(Mode: ControlMode, demand0: Double, demand1: Double): Unit

  def neutralOutput(): Unit

  def setNeutralMode(neutralMode: NeutralMode): Unit

  // ------ Invert behavior ----------//
  def setSensorPhase(PhaseSensor: Boolean): Unit

  def setInverted(invert: Boolean): Unit

  override def getInverted: Boolean

  // ----- general output shaping ------------------//
  def configOpenloopRamp(secondsFromNeutralToFull: Double, timeoutMs: Int): ErrorCode

  def configClosedloopRamp(secondsFromNeutralToFull: Double, timeoutMs: Int): ErrorCode

  def configPeakOutputForward(percentOut: Double, timeoutMs: Int): ErrorCode

  def configPeakOutputReverse(percentOut: Double, timeoutMs: Int): ErrorCode

  def configNominalOutputForward(percentOut: Double, timeoutMs: Int): ErrorCode

  def configNominalOutputReverse(percentOut: Double, timeoutMs: Int): ErrorCode

  def configNeutralDeadband(percentDeadband: Double, timeoutMs: Int): ErrorCode

  // ------ Voltage Compensation ----------//
  def configVoltageCompSaturation(voltage: Double, timeoutMs: Int): ErrorCode

  def configVoltageMeasurementFilter(filterWindowSamples: Int, timeoutMs: Int): ErrorCode

  def enableVoltageCompensation(enable: Boolean): Unit

  // ------ General Status ----------//
  def getBusVoltage: Double

  def getMotorOutputPercent: Double

  def getMotorOutputVoltage: Double

  def getOutputCurrent: Double

  def getTemperature: Double

  // ------ sensor selection ----------//
  def configSelectedFeedbackSensor(feedbackDevice: RemoteFeedbackDevice, pidIdx: Int, timeoutMs: Int): ErrorCode

  def configRemoteFeedbackFilter(deviceID: Int, remoteSensorSource: RemoteSensorSource, remoteOrdinal: Int, timeoutMs: Int): ErrorCode

  def configSensorTerm(sensorTerm: SensorTerm, feedbackDevice: FeedbackDevice, timeoutMs: Int): ErrorCode

  // ------- sensor status --------- //
  def getSelectedSensorPosition(pidIdx: Int): Int

  def getSelectedSensorVelocity(pidIdx: Int): Int

  def setSelectedSensorPosition(sensorPos: Int, pidIdx: Int, timeoutMs: Int): ErrorCode

  // ------ status frame period changes ----------//
  def setControlFramePeriod(frame: ControlFrame, periodMs: Int): ErrorCode

  def setStatusFramePeriod(frame: StatusFrame, periodMs: Int, timeoutMs: Int): ErrorCode

  def getStatusFramePeriod(frame: StatusFrame, timeoutMs: Int): Int

  //------ remote limit switch ----------//
  //----- velocity signal conditionaing ------//
  /* not supported */ def configForwardLimitSwitchSource(`type`: RemoteLimitSwitchSource, normalOpenOrClose: LimitSwitchNormal, deviceID: Int, timeoutMs: Int): ErrorCode

  def configReverseLimitSwitchSource(`type`: RemoteLimitSwitchSource, normalOpenOrClose: LimitSwitchNormal, deviceID: Int, timeoutMs: Int): ErrorCode

  def overrideLimitSwitchesEnable(enable: Boolean): Unit

  // ------ soft limit ----------//
  // ------ local limit switch ----------//
  def configForwardSoftLimitThreshold(forwardSensorLimit: Int, timeoutMs: Int): ErrorCode

  def configReverseSoftLimitThreshold(reverseSensorLimit: Int, timeoutMs: Int): ErrorCode

  def configForwardSoftLimitEnable(enable: Boolean, timeoutMs: Int): ErrorCode

  def configReverseSoftLimitEnable(enable: Boolean, timeoutMs: Int): ErrorCode

  def overrideSoftLimitsEnable(enable: Boolean): Unit

  // ------ General Close loop ----------//
  // ------ Current Lim ----------//
  def config_kP(slotIdx: Int, value: Double, timeoutMs: Int): ErrorCode

  def config_kI(slotIdx: Int, value: Double, timeoutMs: Int): ErrorCode

  def config_kD(slotIdx: Int, value: Double, timeoutMs: Int): ErrorCode

  def config_kF(slotIdx: Int, value: Double, timeoutMs: Int): ErrorCode

  def config_IntegralZone(slotIdx: Int, izone: Int, timeoutMs: Int): ErrorCode

  def configAllowableClosedloopError(slotIdx: Int, allowableCloseLoopError: Int, timeoutMs: Int): ErrorCode

  def configMaxIntegralAccumulator(slotIdx: Int, iaccum: Double, timeoutMs: Int): ErrorCode

  //------ Close loop State ----------//
  def setIntegralAccumulator(iaccum: Double, pidIdx: Int, timeoutMs: Int): ErrorCode

  def getClosedLoopError(pidIdx: Int): Int

  def getIntegralAccumulator(pidIdx: Int): Double

  def getErrorDerivative(pidIdx: Int): Double

  def selectProfileSlot(slotIdx: Int, pidIdx: Int): Unit

  //public int getClosedLoopTarget(int pidIdx); // will be added to JNI
  def getActiveTrajectoryPosition: Int

  def getActiveTrajectoryVelocity: Int

  def getActiveTrajectoryHeading: Double

  // ------ Motion Profile Settings used in Motion Magic and Motion Profile
  def configMotionCruiseVelocity(sensorUnitsPer100ms: Int, timeoutMs: Int): ErrorCode

  def configMotionAcceleration(sensorUnitsPer100msPerSec: Int, timeoutMs: Int): ErrorCode

  // ------ Motion Profile Buffer ----------//
  def clearMotionProfileTrajectories: ErrorCode

  def getMotionProfileTopLevelBufferCount: Int

  def pushMotionProfileTrajectory(trajPt: TrajectoryPoint): ErrorCode

  def isMotionProfileTopLevelBufferFull: Boolean

  def processMotionProfileBuffer(): Unit

  def getMotionProfileStatus(statusToFill: MotionProfileStatus): ErrorCode

  def clearMotionProfileHasUnderrun(timeoutMs: Int): ErrorCode

  def changeMotionControlFramePeriod(periodMs: Int): ErrorCode

  // ------ error ----------//
  def getLastError: ErrorCode

  // ------ Faults ----------//
  def getFaults(toFill: Faults): ErrorCode

  def getStickyFaults(toFill: StickyFaults): ErrorCode

  def clearStickyFaults(timeoutMs: Int): ErrorCode

  // ------ Firmware ----------//
  def getFirmwareVersion: Int

  def hasResetOccurred: Boolean

  // ------ Custom Persistent Params ----------//
  def configSetCustomParam(newValue: Int, paramIndex: Int, timeoutMs: Int): ErrorCode

  def configGetCustomParam(paramIndex: Int, timoutMs: Int): Int

  //------ Generic Param API, typically not used ----------//
  def configSetParameter(param: ParamEnum, value: Double, subValue: Int, ordinal: Int, timeoutMs: Int): ErrorCode

  def configSetParameter(param: Int, value: Double, subValue: Int, ordinal: Int, timeoutMs: Int): ErrorCode

  def configGetParameter(paramEnum: ParamEnum, ordinal: Int, timeoutMs: Int): Double

  def configGetParameter(paramEnum: Int, ordinal: Int, timeoutMs: Int): Double

  //------ Misc. ----------//
  def getBaseID: Int

  def getDeviceID: Int

  // ----- Follower ------//
  /* in parent interface */
}