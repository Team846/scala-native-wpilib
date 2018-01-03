import edu.wpi.first.wpilibj.hal.{ConstantsJNI, HAL, NotifierJNI}

import scala.scalanative.native.{Ptr, extern, link, name}

object Hello extends App {
  val env = Test.createEnv()
  val vm = Test.createVM(env)
  println("created!")
//  Test.testVM(vm, env)
  HAL.JNI_OnLoad(vm, null)
  assert(HAL.initialize(env, null, 0) == 1)

  println("observing starting")
  HAL.observeUserProgramStarting(env, null)
  println("observed starting")

  var lastTime = System.currentTimeMillis()
  while (true) {
    println("getting data!")
    HAL.waitForDSData(env, null)
    println(s"got data ${System.currentTimeMillis() - lastTime}")
    println("observing disabled!")
    HAL.observeUserProgramDisabled(env, null)
    lastTime = System.currentTimeMillis()
  }

  println("bad crashed")
}


@extern
object Test {
  def createEnv(): Ptr[Unit] = extern
  def createVM(env: Ptr[Unit]): Ptr[Unit] = extern
  def testVM(vm: Ptr[Unit], env: Ptr[Unit]): Unit = extern
}
