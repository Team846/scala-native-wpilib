/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008-2017. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
//import java.net.URL
//import java.util
//import java.util.jar.Manifest
//import org.opencv.core.Core

import edu.wpi.first.wpilibj.hal.FRCNetComm.tInstances
import edu.wpi.first.wpilibj.hal.FRCNetComm.tResourceType
import edu.wpi.first.wpilibj.hal.HAL
//import edu.wpi.first.wpilibj.internal.HardwareHLUsageReporting
import edu.wpi.first.wpilibj.internal.HardwareTimer
//import edu.wpi.first.wpilibj.networktables.NetworkTable
//import edu.wpi.first.wpilibj.util.WPILibVersion

/**
  * Constructor for a generic robot program. User code should be placed in the constructor that
  * runs before the Autonomous or Operator Control period starts. The constructor will run to
  * completion before Autonomous is entered.
  *
  * <p>This must be used to ensure that the communications code starts. In the future it would be
  * nice
  * to put this code into it's own task that loads on boot so ensure that it runs.
  */
abstract class RobotBase {
  // TODO: StartCAPI();
  // TODO: See if the next line is necessary
  // Resource.RestartProgram();
//  NetworkTable.setNetworkIdentity("Robot")
//  NetworkTable.setPersistentFilename("/home/lvuser/networktables.ini")
//  NetworkTable.setServerMode // must be before b

  val m_ds = DriverStation.getInstance
//  NetworkTable.getTable("") // forces network tables to initialize
//  NetworkTable.getTable("LiveWindow").getSubTable("~STATUS~").putBoolean("LW Enabled", false)

  /**
    * Free the resources for a RobotBase class.
    */
  def free(): Unit = {
  }

  /**
    * Determine if the Robot is currently disabled.
    *
    * @return True if the Robot is currently disabled by the field controls.
    */
  def isDisabled: Boolean = m_ds.isDisabled

  /**
    * Determine if the Robot is currently enabled.
    *
    * @return True if the Robot is currently enabled by the field controls.
    */
  def isEnabled: Boolean = m_ds.isEnabled

  /**
    * Determine if the robot is currently in Autonomous mode as determined by the field
    * controls.
    *
    * @return True if the robot is currently operating Autonomously.
    */
  def isAutonomous: Boolean = m_ds.isAutonomous

  /**
    * Determine if the robot is currently in Test mode as determined by the driver
    * station.
    *
    * @return True if the robot is currently operating in Test mode.
    */
  def isTest: Boolean = m_ds.isTest

  /**
    * Determine if the robot is currently in Operator Control mode as determined by the field
    * controls.
    *
    * @return True if the robot is currently operating in Tele-Op mode.
    */
  def isOperatorControl: Boolean = m_ds.isOperatorControl

  /**
    * Indicates if new data is available from the driver station.
    *
    * @return Has new data arrived over the network since the last time this function was called?
    */
  def isNewDataAvailable: Boolean = m_ds.isNewControlData

  /**
    * Provide an alternate "main loop" via startCompetition().
    */
  def startCompetition(): Unit

  /**
    * Starting point for the applications.
    */
  def main(args: Array[String]): Unit = {
    RobotBase.initializeHardwareConfiguration()
    HAL.report(tResourceType.kResourceType_Language, tInstances.kLanguage_Java, 0, "")

    val robot = this

    try {
      val file = new File("/tmp/frc_versions/FRC_Lib_Version.ini")

      if (file.exists) file.delete

      file.createNewFile

      try {
        val output = new FileOutputStream(file)
        try {
          output.write("Java ".getBytes)
          //output.write(WPILibVersion.Version.getBytes)
        } finally if (output != null) output.close()
      }
    } catch {
      case ex: IOException =>
        ex.printStackTrace()
    }

    var errorOnExit = false
    try {
      System.out.println("********** Robot program starting **********")
      robot.startCompetition()
    } catch {
      case throwable: Throwable =>
        //DriverStation.reportError("ERROR Unhandled exception: " + throwable.toString + " at " + util.Arrays.toString(throwable.getStackTrace), false)
        errorOnExit = true
    } finally {
      // startCompetition never returns unless exception occurs....
      System.err.println("WARNING: Robots don't quit!")
      if (errorOnExit) System.err.println("---> The startCompetition() method (or methods called by it) should have " + "handled the exception above.")
      else System.err.println("---> Unexpected return from startCompetition() method.")
    }
    System.exit(1)
  }
}

/**
  * Implement a Robot Program framework. The RobotBase class is intended to be subclassed by a user
  * creating a robot program. Overridden autonomous() and operatorControl() methods are called at the
  * appropriate time as the match proceeds. In the current implementation, the Autonomous code will
  * run to completion before the OperatorControl code could start. In the future the Autonomous code
  * might be spawned as a task, then killed at the end of the Autonomous period.
  */
object RobotBase {
  /**
    * The VxWorks priority that robot code should work at (so Java code should run at).
    */
  val ROBOT_TASK_PRIORITY = 101
  /**
    * The ID of the main Java thread.
    */
  // This is usually 1, but it is best to make sure
  val MAIN_THREAD_ID: Long = Thread.currentThread.getId

  /**
    * @return If the robot is running in simulation.
    */
  def isSimulation = false

  /**
    * @return If the robot is running in the real world.
    */
  def isReal = true

  def getBooleanProperty(name: String, defaultValue: Boolean): Boolean = {
    val propVal = System.getProperty(name)
    if (propVal == null) return defaultValue
    if (propVal.equalsIgnoreCase("false")) false
    else if (propVal.equalsIgnoreCase("true")) true
    else throw new IllegalStateException(propVal)
  }

  /**
    * Common initialization for all robot programs.
    */
  def initializeHardwareConfiguration(): Unit = {
    val rv = HAL.initialize(0)
    assert(rv == 1)
    // Set some implementations so that the static methods work properly
    Timer.SetImplementation(new HardwareTimer())
//    HLUsageReporting.SetImplementation(new HardwareHLUsageReporting())
    RobotState.SetImplementation(DriverStation.getInstance)
    // Load opencv
//    try
//      System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
//    catch {
//      case ex: UnsatisfiedLinkError =>
//        System.out.println("OpenCV Native Libraries could not be loaded.")
//        System.out.println("Please try redeploying, or reimage your roboRIO and try again.")
//        ex.printStackTrace()
//    }
  }
}