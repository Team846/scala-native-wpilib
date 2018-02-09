package com.ctre.phoenix

class CANifierStatusFrame(initValue: Int) {
  val value: Int = initValue
}

object CANifierStatusFrame {
  val Status_1_General: CANifierStatusFrame = new CANifierStatusFrame(0x041400)

  val Status_2_General: CANifierStatusFrame = new CANifierStatusFrame(0x041440)

  val Status_3_PwmInputs0: CANifierStatusFrame = new CANifierStatusFrame(0x041480)

  val Status_4_PwmInputs1: CANifierStatusFrame = new CANifierStatusFrame(0x0414C0)

  val Status_5_PwmInputs2: CANifierStatusFrame = new CANifierStatusFrame(0x041500)

  val Status_6_PwmInputs3: CANifierStatusFrame = new CANifierStatusFrame(0x041540)

  val Status_8_Misc: CANifierStatusFrame = new CANifierStatusFrame(0x0415C0)
}
