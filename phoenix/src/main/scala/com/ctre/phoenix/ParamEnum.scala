package com.ctre.phoenix

final class ParamEnum(val value: Int)
object ParamEnum {
  val eOnBoot_BrakeMode = new ParamEnum(31)
  val eQuadFilterEn = new ParamEnum(91)
  val eQuadIdxPolarity = new ParamEnum(108)
  val eClearPositionOnIdx = new ParamEnum(100)
  val eMotionProfileHasUnderrunErr = new ParamEnum(119)
  val eClearPosOnLimitF = new ParamEnum(144)
  val eClearPosOnLimitR = new ParamEnum(145)

  val eStatusFramePeriod = new ParamEnum(300)
  val eOpenloopRamp = new ParamEnum(301)
  val eClosedloopRamp = new ParamEnum(302)
  val eNeutralDeadband = new ParamEnum(303)

  val ePeakPosOutput = new ParamEnum(305)
  val eNominalPosOutput = new ParamEnum(306)
  val ePeakNegOutput = new ParamEnum(307)
  val eNominalNegOutput = new ParamEnum(308)

  val eProfileParamSlot_P = new ParamEnum(310)
  val eProfileParamSlot_I = new ParamEnum(311)
  val eProfileParamSlot_D = new ParamEnum(312)
  val eProfileParamSlot_F = new ParamEnum(313)
  val eProfileParamSlot_IZone = new ParamEnum(314)
  val eProfileParamSlot_AllowableErr = new ParamEnum(315)
  val eProfileParamSlot_MaxIAccum = new ParamEnum(316)

  val eClearPositionOnLimitF = new ParamEnum(320)
  val eClearPositionOnLimitR = new ParamEnum(321)
  val eClearPositionOnQuadIdx = new ParamEnum(322)

  val eSampleVelocityPeriod = new ParamEnum(325)
  val eSampleVelocityWindow = new ParamEnum(326)

  val eFeedbackSensorType = new ParamEnum(330)
  val eSelectedSensorPosition = new ParamEnum(331)
  val eFeedbackNotContinuous = new ParamEnum(332)
  val eRemoteSensorSource = new ParamEnum(333) // RemoteSensorSource_t
  val eRemoteSensorDeviceID = new ParamEnum(334) // [0,62] DeviceID
  val eSensorTerm = new ParamEnum(335) // feedbackDevice_t (ordinal is the register)
  val eRemoteSensorClosedLoopDisableNeutralOnLOS = new ParamEnum(336)

  val eForwardSoftLimitThreshold = new ParamEnum(340)
  val eReverseSoftLimitThreshold = new ParamEnum(341)
  val eForwardSoftLimitEnable = new ParamEnum(342)
  val eReverseSoftLimitEnable = new ParamEnum(343)

  val eNominalBatteryVoltage = new ParamEnum(350)
  val eBatteryVoltageFilterSize = new ParamEnum(351)

  val eContinuousCurrentLimitAmps = new ParamEnum(360)
  val ePeakCurrentLimitMs = new ParamEnum(361)
  val ePeakCurrentLimitAmps = new ParamEnum(362)

  val eClosedLoopIAccum = new ParamEnum(370)

  val eCustomParam = new ParamEnum(380)

  val eStickyFaults = new ParamEnum(390)

  val eAnalogPosition = new ParamEnum(400)
  val eQuadraturePosition = new ParamEnum(401)
  val ePulseWidthPosition = new ParamEnum(402)

  val eMotMag_Accel = new ParamEnum(410)
  val eMotMag_VelCruise = new ParamEnum(411)

  val eLimitSwitchSource = new ParamEnum(421) // ordinal = new ParamEnum(fwd=0,reverse=1) @see LimitSwitchSource_t
  val eLimitSwitchNormClosedAndDis = new ParamEnum(422) // ordinal (fwd=0,reverse=1). @see LimitSwitchNormClosedAndDis_t
  val eLimitSwitchDisableNeutralOnLOS = new ParamEnum(423)
  val eLimitSwitchRemoteDevID = new ParamEnum(424)

  val eYawOffset = new ParamEnum(160)
  val eCompassOffset = new ParamEnum(161)
  val eBetaGain = new ParamEnum(162)
  val eEnableCompassFusion = new ParamEnum(163)
  val eGyroNoMotionCal = new ParamEnum(164)
  val eEnterCalibration = new ParamEnum(165)
  val eFusedHeadingOffset = new ParamEnum(166)
  val eStatusFrameRate = new ParamEnum(169)
  val eAccumZ = new ParamEnum(170)
  val eTempCompDisable = new ParamEnum(171)
  val eMotionMeas_tap_threshX = new ParamEnum(172)
  val eMotionMeas_tap_threshY = new ParamEnum(173)
  val eMotionMeas_tap_threshZ = new ParamEnum(174)
  val eMotionMeas_tap_count = new ParamEnum(175)
  val eMotionMeas_tap_time = new ParamEnum(176)
  val eMotionMeas_tap_time_multi = new ParamEnum(177)
  val eMotionMeas_shake_reject_thresh = new ParamEnum(178)
  val eMotionMeas_shake_reject_time = new ParamEnum(179)
  val eMotionMeas_shake_reject_timeout = new ParamEnum(180)
}
