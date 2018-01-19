package com.ctre.phoenix.motorcontrol

final class LimitSwitchNormal(val value: Int)
object LimitSwitchNormal {
  val NormallyOpen = new LimitSwitchNormal(0)
  val NormallyClosed = new LimitSwitchNormal(1)
  val Disabled = new LimitSwitchNormal(2)
}
