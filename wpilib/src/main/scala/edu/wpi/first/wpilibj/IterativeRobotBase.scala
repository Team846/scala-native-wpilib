/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj

import edu.wpi.first.wpilibj.hal.HAL
//import edu.wpi.first.wpilibj.livewindow.LiveWindow
//import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard

/**
  * IterativeRobotBase implements a specific type of robot program framework, extending the RobotBase
  * class.
  *
  * <p>The IterativeRobotBase class does not implement startCompetition(), so it should not be used
  * by teams directly.
  *
  * <p>This class provides the following functions which are called by the main loop,
  * startCompetition(), at the appropriate times:
  *
  * <p>robotInit() -- provide for initialization at robot power-on
  *
  * <p>init() functions -- each of the following functions is called once when the
  * appropriate mode is entered:
  *   - disabledInit()   -- called only when first disabled
  *   - autonomousInit() -- called each and every time autonomous is entered from
  * another mode
  *   - teleopInit()     -- called each and every time teleop is entered from
  * another mode
  *   - testInit()       -- called each and every time test is entered from
  * another mode
  *
  * <p>periodic() functions -- each of these functions is called on an interval:
  *   - robotPeriodic()
  *   - disabledPeriodic()
  *   - autonomousPeriodic()
  *   - teleopPeriodic()
  *   - testPeriodic()
  */
abstract class IterativeRobotBase extends RobotBase {
  private var m_lastMode = IterativeRobotBase.Mode.kNone

  /**
    * Provide an alternate "main loop" via startCompetition().
    */
  override def startCompetition(): Unit

  /**
    * Robot-wide initialization code should go here.
    *
    * <p>Users should override this method for default Robot-wide initialization which will be called
    * when the robot is first powered on. It will be called exactly one time.
    *
    * <p>Warning: the Driver Station "Robot Code" light and FMS "Robot Ready" indicators will be off
    * until RobotInit() exits. Code in RobotInit() that waits for enable will cause the robot to
    * never indicate that the code is ready, causing the robot to be bypassed in a match.
    */
  def robotInit(): Unit = {
    System.out.println("Default robotInit() method... Overload me!")
  }

  /**
    * Initialization code for disabled mode should go here.
    *
    * <p>Users should override this method for initialization code which will be called each time the
    * robot enters disabled mode.
    */
  def disabledInit(): Unit = {
    System.out.println("Default disabledInit() method... Overload me!")
  }

  /**
    * Initialization code for autonomous mode should go here.
    *
    * <p>Users should override this method for initialization code which will be called each time the
    * robot enters autonomous mode.
    */
  def autonomousInit(): Unit = {
    System.out.println("Default autonomousInit() method... Overload me!")
  }

  /**
    * Initialization code for teleop mode should go here.
    *
    * <p>Users should override this method for initialization code which will be called each time the
    * robot enters teleop mode.
    */
  def teleopInit(): Unit = {
    System.out.println("Default teleopInit() method... Overload me!")
  }

  /**
    * Initialization code for test mode should go here.
    *
    * <p>Users should override this method for initialization code which will be called each time the
    * robot enters test mode.
    */
  @SuppressWarnings(Array("PMD.JUnit4TestShouldUseTestAnnotation")) def testInit(): Unit = {
    System.out.println("Default testInit() method... Overload me!")
  }

  private var m_rpFirstRun = true

  /**
    * Periodic code for all robot modes should go here.
    */
  def robotPeriodic(): Unit = {
    if (m_rpFirstRun) {
      System.out.println("Default robotPeriodic() method... Overload me!")
      m_rpFirstRun = false
    }
  }

  private var m_dpFirstRun = true

  /**
    * Periodic code for disabled mode should go here.
    */
  def disabledPeriodic(): Unit = {
    if (m_dpFirstRun) {
      System.out.println("Default disabledPeriodic() method... Overload me!")
      m_dpFirstRun = false
    }
  }

  private var m_apFirstRun = true

  /**
    * Periodic code for autonomous mode should go here.
    */
  def autonomousPeriodic(): Unit = {
    if (m_apFirstRun) {
      System.out.println("Default autonomousPeriodic() method... Overload me!")
      m_apFirstRun = false
    }
  }

  private var m_tpFirstRun = true

  /**
    * Periodic code for teleop mode should go here.
    */
  def teleopPeriodic(): Unit = {
    if (m_tpFirstRun) {
      System.out.println("Default teleopPeriodic() method... Overload me!")
      m_tpFirstRun = false
    }
  }

  private var m_tmpFirstRun = true

  /**
    * Periodic code for test mode should go here.
    */
  @SuppressWarnings(Array("PMD.JUnit4TestShouldUseTestAnnotation")) def testPeriodic(): Unit = {
    if (m_tmpFirstRun) {
      System.out.println("Default testPeriodic() method... Overload me!")
      m_tmpFirstRun = false
    }
  }

  protected def loopFunc(): Unit = { // Call the appropriate function depending upon the current robot mode
    if (isDisabled) { // call DisabledInit() if we are now just entering disabled mode from
      // either a different mode or from power-on
      if (m_lastMode ne IterativeRobotBase.Mode.kDisabled) {
//        LiveWindow.setEnabled(false)
        disabledInit()
        m_lastMode = IterativeRobotBase.Mode.kDisabled
      }
      HAL.observeUserProgramDisabled()
      disabledPeriodic()
    }
    else if (isAutonomous) { // call Autonomous_Init() if this is the first time
      // we've entered autonomous_mode
      if (m_lastMode ne IterativeRobotBase.Mode.kAutonomous) {
//        LiveWindow.setEnabled(false)
        // KBS NOTE: old code reset all PWMs and relays to "safe values"
        // whenever entering autonomous mode, before calling
        // "Autonomous_Init()"
        autonomousInit()
        m_lastMode = IterativeRobotBase.Mode.kAutonomous
      }
      HAL.observeUserProgramAutonomous()
      autonomousPeriodic()
    }
    else if (isOperatorControl) { // call Teleop_Init() if this is the first time
      // we've entered teleop_mode
      if (m_lastMode ne IterativeRobotBase.Mode.kTeleop) {
//        LiveWindow.setEnabled(false)
        teleopInit()
        m_lastMode = IterativeRobotBase.Mode.kTeleop
      }
      HAL.observeUserProgramTeleop()
      teleopPeriodic()
    }
    else { // call TestInit() if we are now just entering test mode from either
      // a different mode or from power-on
      if (m_lastMode ne IterativeRobotBase.Mode.kTest) {
//        LiveWindow.setEnabled(true)
        testInit()
        m_lastMode = IterativeRobotBase.Mode.kTest
      }
      HAL.observeUserProgramTest()
      testPeriodic()
    }
    robotPeriodic()
//    SmartDashboard.updateValues
//    LiveWindow.updateValues
  }
}

object IterativeRobotBase {

  private object Mode extends Enumeration {
    type Mode = Value
    val kNone, kDisabled, kAutonomous, kTeleop, kTest = Value
  }

}