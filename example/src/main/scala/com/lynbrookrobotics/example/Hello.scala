package com.lynbrookrobotics.example

import java.nio.ByteBuffer

import edu.wpi.first.wpilibj.hal.HALUtil
import edu.wpi.first.wpilibj._

object Hello extends IterativeRobot {
  val joy = new Joystick(0)
//  val pwm = new Servo(0)
//  val buf = ByteBuffer.allocateDirect(5)

  import com.lynbrookrobotics.scalanativejni._
//  MockJNI.testVM(vm, env, buf)
//  println(buf.get(0), buf.get(1))

//  println(HALUtil.getHALErrorMessage(1001))

//  println(PWMJNI.initializePWMPort(-1))

  override def teleopPeriodic(): Unit = {
    println(joy.getRawButton(1))
//    pwm.setPosition(math.abs(math.sin(Timer.getFPGATimestamp)))
  }

  override def main(args: Array[String]): Unit = {
    super.main(args)
  }
}
