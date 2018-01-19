package com.ctre.phoenix.motorcontrol

class StatusFrameEnhanced(var value: Int)
object StatusFrameEnhanced {

  val Status_1_General: StatusFrameEnhanced = new StatusFrameEnhanced(0x1400)

  val Status_2_Feedback0: StatusFrameEnhanced = new StatusFrameEnhanced(0x1440)

  val Status_4_AinTempVbat: StatusFrameEnhanced = new StatusFrameEnhanced(
    0x14C0)

  val Status_6_Misc: StatusFrameEnhanced = new StatusFrameEnhanced(0x1540)

  val Status_7_CommStatus: StatusFrameEnhanced = new StatusFrameEnhanced(
    0x1580)

  val Status_9_MotProfBuffer: StatusFrameEnhanced = new StatusFrameEnhanced(
    0x1600)

  val Status_10_MotionMagic: StatusFrameEnhanced = new StatusFrameEnhanced(
    0x1640)

  val Status_12_Feedback1: StatusFrameEnhanced = new StatusFrameEnhanced(
    0x16C0)

  val Status_13_Base_PIDF0: StatusFrameEnhanced = new StatusFrameEnhanced(
    0x1700)

  val Status_14_Turn_PIDF1: StatusFrameEnhanced = new StatusFrameEnhanced(
    0x1740)

  val Status_15_FirmareApiStatus: StatusFrameEnhanced =
    new StatusFrameEnhanced(0x1780)

  val Status_3_Quadrature: StatusFrameEnhanced = new StatusFrameEnhanced(
    0x1480)

  val Status_8_PulseWidth: StatusFrameEnhanced = new StatusFrameEnhanced(
    0x15C0)

  val Status_11_UartGadgeteer: StatusFrameEnhanced = new StatusFrameEnhanced(
    0x1680)
}
