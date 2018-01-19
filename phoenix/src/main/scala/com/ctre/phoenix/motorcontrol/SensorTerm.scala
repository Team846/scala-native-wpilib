package com.ctre.phoenix.motorcontrol

final class SensorTerm(val value: Int)
object SensorTerm {
  val Sum0 = new SensorTerm(0)
  val Sum1 = new SensorTerm(1)
  val Diff0 = new SensorTerm(2)
  val Diff1 = new SensorTerm(3)
}
