package com.ctre.phoenix.motorcontrol

final class FeedbackDevice(val value: Int)
object FeedbackDevice {
  val None = new FeedbackDevice(-1)
  
  val QuadEncoder = new FeedbackDevice(0)
  val Analog = new FeedbackDevice(2)
  val Tachometer = new FeedbackDevice(4)
  val PulseWidthEncodedPosition = new FeedbackDevice(8)
  
  val SensorSum = new FeedbackDevice(9)
  val SensorDifference = new FeedbackDevice(10)
  val RemoteSensor0 = new FeedbackDevice(11)
  val RemoteSensor1 = new FeedbackDevice(12)
  val SoftwareEmulatedSensor = new FeedbackDevice(15)
  
  val CTRE_MagEncoder_Absolute = new FeedbackDevice(8)
  val CTRE_MagEncoder_Relative = new FeedbackDevice(0)
}