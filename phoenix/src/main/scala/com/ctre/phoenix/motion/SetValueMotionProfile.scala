package com.ctre.phoenix.motion

final class SetValueMotionProfile(val value: Int)
object SetValueMotionProfile {
  val Invalid = new SetValueMotionProfile(-1)
  val Disable = new SetValueMotionProfile(0)
  val Enable = new SetValueMotionProfile(1)
  val Hold = new SetValueMotionProfile(2)

  val values = Seq(
    Invalid, Disable, Enable, Hold
  )

  def valueOf(value: Int): SetValueMotionProfile = {
    for (e <- SetValueMotionProfile.values) {
      if (e.value == value) return e
    }
    Invalid
  }
}
