package edu.wpi.first.wpilibj

import java.nio.ByteBuffer

import edu.wpi.first.wpilibj.hal.{HAL, HALUtil}
import com.lynbrookrobotics.scalanativejni

object Hello extends RobotBase {
  override def main(args: Array[String]): Unit = {
    super.main(args)
  }

  /**
    * Provide an alternate "main loop" via startCompetition().
    */
  override def startCompetition(): Unit = {
    HAL.observeUserProgramStarting()
    println("starting!")
    val foo = HALUtil.getHALErrorMessage(1004)
    println(s"got it $foo")
    var lastTime = System.currentTimeMillis()
    while (true) {
      val before = Array[Float](1, 2, 3)
      println(s"before: ${before.toList}")
      scalanativejni.MockJNI.testVM(scalanativejni.vm, scalanativejni.env, before)
      println(s"after: ${before.toList}")

//      println("getting data!")
      HAL.waitForDSData()
//      println(s"got data ${System.currentTimeMillis() - lastTime}")
//      println("observing disabled!")
      HAL.observeUserProgramDisabled()
      lastTime = System.currentTimeMillis()
    }
  }
}
