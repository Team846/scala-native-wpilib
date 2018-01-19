package com.lynbrookrobotics.example

import edu.wpi.first.wpilibj.{AnalogInput, IterativeRobot, Notifier, Servo}

object Hello extends IterativeRobot {
  lazy val in = new AnalogInput(0)
  lazy val out = new Servo(0)

  val notifier = new Notifier(new Runnable {
    override def run(): Unit = out.set(in.getAverageVoltage / 5)
  })

  notifier.startPeriodic(0.005)

  override def main(args: Array[String]): Unit = {
    super.main(args)
  }
}
