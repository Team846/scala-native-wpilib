package com.ctre.phoenix

class CANifierControlFrame(initValue: Int) {
  val value: Int = initValue
}

object CANifierControlFrame {
  val Control_1_General: CANifierControlFrame = new CANifierControlFrame(0x040000)

  val Control_2_PwmOutput: CANifierControlFrame = new CANifierControlFrame(0x040040)
}
