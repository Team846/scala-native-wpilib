package com.lynbrookrobotics.example

import edu.wpi.first.wpilibj.{AnalogInput, IterativeRobot, SPI, Servo}
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.frc.Implicits._
import com.lynbrookrobotics.potassium.frc.SPIWrapper
import com.lynbrookrobotics.potassium.sensors.imu.ADIS16448
import squants.electro.Volts
import squants.time.Milliseconds

object Hello extends IterativeRobot {
  val in = new AnalogInput(0)
  val out = new Servo(0)

  val gyro = new ADIS16448(new SPIWrapper(new SPI(SPI.Port.kMXP)), Milliseconds(5))

  val inStream = Stream.periodic(Milliseconds(5))(in.averageVoltage)
  val outStream = inStream.map(_ / Volts(5))
  val cancel = outStream.foreach(out.set)

  override def main(args: Array[String]): Unit = {
    super.main(args)
  }

  override def teleopPeriodic(): Unit = {
    println(gyro.currentData.gyro.z)
  }
}
