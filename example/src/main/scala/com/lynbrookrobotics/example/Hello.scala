package com.lynbrookrobotics.example

import java.nio.ByteBuffer

import edu.wpi.first.wpilibj.hal.{HAL, HALUtil, PWMJNI}
import edu.wpi.first.wpilibj.{IterativeRobot, Servo, Timer}

object Hello extends IterativeRobot {
  val pwm = new Servo(0)
  val buf = ByteBuffer.allocateDirect(5)

  import com.lynbrookrobotics.scalanativejni._
  MockJNI.testVM(vm, env, buf)
  println(buf.get(0), buf.get(1))

  println(HALUtil.getHALErrorMessage(1001))

//  println(PWMJNI.initializePWMPort(-1))

  override def teleopPeriodic(): Unit = {
    pwm.setPosition(math.abs(math.sin(Timer.getFPGATimestamp)))
  }

  override def main(args: Array[String]): Unit = {
    super.main(args)
  }
}
