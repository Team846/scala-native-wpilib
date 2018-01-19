package com.ctre.phoenix.motorcontrol

final class LimitSwitchSource(val value: Int)
object LimitSwitchSource {
  val FeedbackConnector = new LimitSwitchSource(0)
  val RemoteTalonSRX = new LimitSwitchSource(1)
  val RemoteCANifier = new LimitSwitchSource(2)
  val Deactivated = new LimitSwitchSource(3)
}
