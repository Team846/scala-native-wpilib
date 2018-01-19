package com.ctre.phoenix.motorcontrol

final class ControlMode(val value: Int)
object ControlMode {
  val PercentOutput = new ControlMode(0)
  val Position = new ControlMode(1)
  val Velocity = new ControlMode(2)
  val Current = new ControlMode(3)
  val Follower = new ControlMode(5)
  val MotionProfile = new ControlMode(6)
  val MotionMagic = new ControlMode(7)
  val MotionMagicArc = new ControlMode(8)
  //val TimedPercentOutput = new ControlMode(9)
  val MotionProfileArc = new ControlMode(10)

  val Disabled = new ControlMode(15)

  val values = Seq(
    PercentOutput, Position, Velocity, Current, Follower,
    MotionProfile, MotionMagic, MotionMagicArc, MotionProfileArc, Disabled
  )
}