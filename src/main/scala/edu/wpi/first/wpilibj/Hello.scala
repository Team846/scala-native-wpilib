package edu.wpi.first.wpilibj

import edu.wpi.first.wpilibj.hal.{HAL, HALUtil}

object Hello extends RobotBase {
  override def main(args: Array[String]): Unit = {
    super.main(args)
  }

  /**
    * Provide an alternate "main loop" via startCompetition().
    */
  override def startCompetition(): Unit = {
    println("starting!")
    val foo = jString2String(HALUtil.getHALErrorMessage(env, cls, 1004))
    println(s"got it $foo")
//    var lastTime = System.currentTimeMillis()
//    while (true) {
////      println("getting data!")
//      HAL.waitForDSData(env, null)
////      println(s"got data ${System.currentTimeMillis() - lastTime}")
////      println("observing disabled!")
//      HAL.observeUserProgramDisabled(env, null)
//      lastTime = System.currentTimeMillis()
//    }
  }
}
