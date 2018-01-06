package com.lynbrookrobotics.example

import edu.wpi.first.wpilibj.{IterativeRobot, Servo, Timer}

object Hello extends IterativeRobot {
  val pwm = new Servo(0)

  override def teleopPeriodic(): Unit = {
    pwm.setPosition(math.abs(math.sin(Timer.getFPGATimestamp)))
  }

  override def main(args: Array[String]): Unit = {
    super.main(args)
  }
}
