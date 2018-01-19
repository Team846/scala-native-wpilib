package com.ctre.phoenix.motorcontrol

final class RemoteFeedbackDevice(val value: Int)
object RemoteFeedbackDevice {
  val None = new RemoteFeedbackDevice(-1)

  val SensorSum = new RemoteFeedbackDevice(9)
  val SensorDifference = new RemoteFeedbackDevice(10)
  val RemoteSensor0 = new RemoteFeedbackDevice(11)
  val RemoteSensor1 = new RemoteFeedbackDevice(12)
  val SoftwareEmulatedSensor = new RemoteFeedbackDevice(15)
}
