package com.lynbrookrobotics.example

import java.nio.ByteBuffer

import edu.wpi.first.wpilibj.hal.HALUtil
import edu.wpi.first.wpilibj.{DriverStation, IterativeRobot, Servo, Timer}

object Hello extends IterativeRobot {
  val pwm = new Servo(0)
  val buf = ByteBuffer.allocateDirect(5)

  import com.lynbrookrobotics.scalanativejni._
//  MockJNI.testVM(vm, env, buf)
//  println(buf.get(0), buf.get(1))

  println(HALUtil.getHALErrorMessage(1001))

//  println(PWMJNI.initializePWMPort(-1))

  override def teleopPeriodic(): Unit = {
    println(s"game specific message is ${DriverStation.getInstance.getGameSpecificMessage}")
    pwm.setPosition(math.abs(math.sin(Timer.getFPGATimestamp)))
  }

  override def main(args: Array[String]): Unit = {
    super.main(args)
  }
}
