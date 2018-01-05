/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2016-2017. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj

import edu.wpi.first.wpilibj.util.BaseSystemNotInitializedException

class HLUsageReporting

/**
  * Support for high level usage reporting.
  */
@SuppressWarnings(Array("JavadocMethod"))
object HLUsageReporting {
  private var impl: Interface = null

  @SuppressWarnings(Array("MethodName"))
  def SetImplementation(implementation: HLUsageReporting.Interface): Unit = {
    impl = implementation
  }

  def reportScheduler(): Unit = {
    if (impl != null) impl.reportScheduler()
    else throw new BaseSystemNotInitializedException(classOf[HLUsageReporting.Interface], classOf[HLUsageReporting])
  }

  def reportPIDController(num: Int): Unit = {
    if (impl != null) impl.reportPIDController(num)
    else throw new BaseSystemNotInitializedException(classOf[HLUsageReporting.Interface], classOf[HLUsageReporting])
  }

  def reportSmartDashboard(): Unit = {
    if (impl != null) impl.reportSmartDashboard()
    else throw new BaseSystemNotInitializedException(classOf[HLUsageReporting.Interface], classOf[HLUsageReporting])
  }

  trait Interface {
    def reportScheduler(): Unit

    def reportPIDController(num: Int): Unit

    def reportSmartDashboard(): Unit
  }

  class Null extends HLUsageReporting.Interface {
    override def reportScheduler(): Unit = {
    }

    @SuppressWarnings(Array("PMD.UnusedFormalParameter"))
    override def reportPIDController(num: Int): Unit = {
    }

    override def reportSmartDashboard(): Unit = {
    }
  }
}
