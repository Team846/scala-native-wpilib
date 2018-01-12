package com.lynbrookrobotics.example

import java.io.{FileWriter, PrintWriter}

import edu.wpi.first.wpilibj.hal.HAL
import edu.wpi.first.wpilibj._

object Hello extends IterativeRobot {
  val in = new AnalogInput(0)
  override def disabledPeriodic(): Unit = {
    println(in.getAverageVoltage)
  }

  override def main(args: Array[String]): Unit = {
    super.main(args)
  }
}
