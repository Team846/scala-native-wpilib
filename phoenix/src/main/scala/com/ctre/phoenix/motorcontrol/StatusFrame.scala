package com.ctre.phoenix.motorcontrol

final class StatusFrame(val value: Int)
object StatusFrame {
  val Status_1_General = new StatusFrame(0x1400)
  val Status_2_Feedback0 = new StatusFrame(0x1440)
  val Status_4_AinTempVbat = new StatusFrame(0x14C0)
  val Status_6_Misc = new StatusFrame(0x1540)
  val Status_7_CommStatus = new StatusFrame(0x1580)
  val Status_9_MotProfBuffer = new StatusFrame(0x1600)
  val Status_10_MotionMagic = new StatusFrame(0x1640)
  val Status_12_Feedback1 = new StatusFrame(0x16C0)
  val Status_13_Base_PIDF0 = new StatusFrame(0x1700)
  val Status_14_Turn_PIDF1 = new StatusFrame(0x1740)
  val Status_15_FirmwareApiStatus = new StatusFrame(0x1780)

  def values(): Seq[StatusFrame] = Seq(
    Status_1_General,
    Status_2_Feedback0,
    Status_4_AinTempVbat,
    Status_6_Misc,
    Status_7_CommStatus,
    Status_9_MotProfBuffer,
    Status_10_MotionMagic,
    Status_12_Feedback1,
    Status_13_Base_PIDF0,
    Status_14_Turn_PIDF1,
    Status_15_FirmwareApiStatus
  )
}
