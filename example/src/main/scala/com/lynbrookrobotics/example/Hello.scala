package com.lynbrookrobotics.example

import java.io.{FileWriter, PrintWriter}
import java.nio.ByteBuffer

import com.lynbrookrobotics.scalanativejni._
import edu.wpi.first.wpilibj.hal.{HAL, HALUtil, ThreadsJNI}
import edu.wpi.first.wpilibj._

object Hello extends RobotBase {
//  println("WOW WOW WOW")
//  val joy = new Joystick(0)

  val pre = 5
  val seconds = 60
  val ticks = new Array[Double](seconds * 200)
  var i = 0
  var done = false

  var lastTime = Timer.getFPGATimestamp
  val notifier: Notifier = new Notifier(new Runnable {
    override def run(): Unit = {
      if (i == 0) {
        Threads.setCurrentThreadPriority(true, 99)
        println(Threads.getCurrentThreadPriority)
        println(Threads.getCurrentThreadIsRealTime)
      }

      val time = Timer.getFPGATimestamp

      if (i >= pre * 200) {
        if ((i - pre * 200) < seconds * 200) {
          ticks(i - pre * 200) = time - lastTime
        } else if (!done) {
          done = true
          val out = new PrintWriter(new FileWriter("/home/lvuser/ticks.txt"))
          ticks.foreach(out.println)
          out.flush()
          out.close()
          println("done")
          throw new Exception("I'm done!")
        }
      }

      i += 1
      lastTime = time
    }
  })

  override def startCompetition(): Unit = {
    HAL.observeUserProgramStarting()
    notifier.startPeriodic(0.005)
    while (true) {
      Thread.sleep(100000)
    }
  }

  override def main(args: Array[String]): Unit = {
    super.main(args)
  }
}
