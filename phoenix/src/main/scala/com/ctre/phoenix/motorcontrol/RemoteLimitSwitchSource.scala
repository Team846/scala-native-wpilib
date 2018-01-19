package com.ctre.phoenix.motorcontrol

final class RemoteLimitSwitchSource(val value: Int)
object RemoteLimitSwitchSource {
  val RemoteTalonSRX = new RemoteLimitSwitchSource(1)
  val RemoteCANifier = new RemoteLimitSwitchSource(2)
  val Deactivated = new RemoteLimitSwitchSource(3)
}
