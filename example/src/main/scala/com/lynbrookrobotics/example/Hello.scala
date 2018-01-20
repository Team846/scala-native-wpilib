package com.lynbrookrobotics.example

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import edu.wpi.first.wpilibj.{AnalogInput, IterativeRobot, Notifier, Servo}

object Hello extends IterativeRobot {
  val left = new TalonSRX(50)
  val leftFront = new TalonSRX(51)
  leftFront.follow(left)
  left.setInverted(true)
  leftFront.setInverted(true)

  override def teleopPeriodic(): Unit = {
    left.set(ControlMode.PercentOutput, 0.5)
  }

  override def main(args: Array[String]): Unit = {
    super.main(args)
  }
}
