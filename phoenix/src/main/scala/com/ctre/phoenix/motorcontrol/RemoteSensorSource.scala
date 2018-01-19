package com.ctre.phoenix.motorcontrol

final class RemoteSensorSource(val value: Int)
object RemoteSensorSource {
  val Off = new RemoteSensorSource(0)
  val TalonSRX_SelectedSensor = new RemoteSensorSource(1)
  val Pigeon_Yaw = new RemoteSensorSource(2)
  val Pigeon_Pitch = new RemoteSensorSource(3)
  val Pigeon_Roll = new RemoteSensorSource(4)
  val CANifier_Quadrature = new RemoteSensorSource(5)
  val CANifier_PWMInput0 = new RemoteSensorSource(6)
  val CANifier_PWMInput1 = new RemoteSensorSource(7)
  val CANifier_PWMInput2 = new RemoteSensorSource(8)
  val CANifier_PWMInput3 = new RemoteSensorSource(9)
}
