package com.lynbrookrobotics.example

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import edu.wpi.first.wpilibj.{AnalogInput, IterativeRobot, Notifier, Servo}

object Hello extends IterativeRobot {
  val ct = new TalonSRX(0)

  override def teleopPeriodic(): Unit = {
    println(s"wow I have a talon with version ${ct.getFirmwareVersion}")
  }

  override def main(args: Array[String]): Unit = {
    super.main(args)
  }
}
