package com.lynbrookrobotics.example

import java.io.{FileWriter, PrintWriter}
import java.nio.ByteBuffer

import com.lynbrookrobotics.scalanativejni._
import edu.wpi.first.wpilibj.hal.{HALUtil, ThreadsJNI}
import edu.wpi.first.wpilibj._

object Hello extends IterativeRobot {
//  println("WOW WOW WOW")
//  val joy = new Joystick(0)

  val ticks = new Array[Double](20 * 200)
  var i = 0
  var done = false

  var lastTime = Timer.getFPGATimestamp
  val notifier: Notifier = new Notifier(new Runnable {
    override def run(): Unit = {
      if (i == 0) {
        MockJNI.testVM(vm, env)
      }

      val time = Timer.getFPGATimestamp
      if (i < 20 * 200) {
        ticks(i) = time - lastTime
        i += 1
        lastTime = time
      } else if (!done) {
        done = true
        val out = new PrintWriter(new FileWriter("ticks.txt"))
        ticks.foreach(out.println)
        out.flush()
        out.close()
        System.exit(0)
      }
    }
  })
//  val pwm = new Servo(0)
//  val buf = ByteBuffer.allocateDirect(5)

  import com.lynbrookrobotics.scalanativejni._
//  MockJNI.testVM(vm, env, buf)
//  println(buf.get(0), buf.get(1))

//  println(HALUtil.getHALErrorMessage(1001))

//  println(PWMJNI.initializePWMPort(-1))

  override def teleopPeriodic(): Unit = {
//    println(joy.getRawButton(1))
//    pwm.setPosition(math.abs(math.sin(Timer.getFPGATimestamp)))
  }

  override def robotInit(): Unit = {
    notifier.startPeriodic(0.005)
  }

  override def main(args: Array[String]): Unit = {
    super.main(args)
  }
}
