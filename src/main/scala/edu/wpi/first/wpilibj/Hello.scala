package edu.wpi.first.wpilibj

import java.nio.ByteBuffer

import edu.wpi.first.wpilibj.hal.{HAL, HALUtil}
import com.lynbrookrobotics.scalanativejni

object Hello extends IterativeRobot {
  override def main(args: Array[String]): Unit = {
    super.main(args)
  }

  override def robotPeriodic(): Unit = {
    println(s"time: ${Timer.getFPGATimestamp}, matchTime: ${Timer.getMatchTime}")
  }
}
