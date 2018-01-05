/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2016-2017. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj

import edu.wpi.first.wpilibj.util.BaseSystemNotInitializedException

class RobotState

@SuppressWarnings(Array("JavadocMethod"))
object RobotState {
  private var m_impl: Interface = null

  @SuppressWarnings(Array("MethodName"))
  def SetImplementation(implementation: RobotState.Interface): Unit = {
    m_impl = implementation
  }

  def isDisabled: Boolean = if (m_impl != null) m_impl.isDisabled
  else throw new BaseSystemNotInitializedException(classOf[RobotState.Interface], classOf[RobotState])

  def isEnabled: Boolean = if (m_impl != null) m_impl.isEnabled
  else throw new BaseSystemNotInitializedException(classOf[RobotState.Interface], classOf[RobotState])

  def isOperatorControl: Boolean = if (m_impl != null) m_impl.isOperatorControl
  else throw new BaseSystemNotInitializedException(classOf[RobotState.Interface], classOf[RobotState])

  def isAutonomous: Boolean = if (m_impl != null) m_impl.isAutonomous
  else throw new BaseSystemNotInitializedException(classOf[RobotState.Interface], classOf[RobotState])

  def isTest: Boolean = if (m_impl != null) m_impl.isTest
  else throw new BaseSystemNotInitializedException(classOf[RobotState.Interface], classOf[RobotState])

  private[wpilibj] trait Interface {
    def isDisabled: Boolean

    def isEnabled: Boolean

    def isOperatorControl: Boolean

    def isAutonomous: Boolean

    def isTest: Boolean
  }
}
