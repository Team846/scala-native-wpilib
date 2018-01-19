package com.ctre.phoenix.motorcontrol

final class ControlFrame(val value: Int)
object ControlFrame {
  val Control_3_General = new ControlFrame(0x040080)
  val Control_4_Advanced = new ControlFrame(0x0400C0)
  val Control_6_MotProfAddTrajPoint = new ControlFrame(0x040140)

  val values = Seq(Control_3_General, Control_4_Advanced, Control_6_MotProfAddTrajPoint)
}
