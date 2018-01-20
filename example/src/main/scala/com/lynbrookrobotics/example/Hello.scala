package com.lynbrookrobotics.example

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import edu.wpi.first.wpilibj.{AnalogInput, IterativeRobot, Notifier, Servo}

object Hello extends IterativeRobot {
  val ct = new TalonSRX(50)

  override def teleopPeriodic(): Unit = {
    ct.set(ControlMode.PercentOutput, 0.5)
  }

  override def main(args: Array[String]): Unit = {
    super.main(args)
  }
}
