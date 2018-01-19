/*----------------------------------------------------------------------------*/
/* Copyright (c) 2008-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.util
import java.util.jar.Manifest

//import edu.wpi.cscore.CameraServerJNI
//import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.hal.FRCNetComm.tInstances
import edu.wpi.first.wpilibj.hal.FRCNetComm.tResourceType
import edu.wpi.first.wpilibj.hal.HAL
import edu.wpi.first.wpilibj.hal.HALUtil
import edu.wpi.first.wpilibj.internal.HardwareHLUsageReporting
import edu.wpi.first.wpilibj.internal.HardwareTimer
//import edu.wpi.first.wpilibj.livewindow.LiveWindow
import edu.wpi.first.wpilibj.util.WPILibVersion

/**
  * Constructor for a generic robot program. User code should be placed in the constructor that
  * runs before the Autonomous or Operator Control period starts. The constructor will run to
  * completion before Autonomous is entered.
  *
  * <p>This must be used to ensure that the communications code starts. In the future it would be
  * nice
  * to put this code into it's own task that loads on boot so ensure that it runs.
  */
abstract class RobotBase protected() {
  // originally, these happen before classloading
  System.out.println("********** Robot program starting **********")
  RobotBase.initializeHardwareConfiguration()
  HAL.report(tResourceType.kResourceType_Language, tInstances.kLanguage_Java)

//  val inst: NetworkTableInstance = NetworkTableInstance.getDefault
//  inst.setNetworkIdentity("Robot")
//  inst.startServer("/home/lvuser/networktables.ini")
  final protected var m_ds: DriverStation = DriverStation.getInstance
//  inst.getTable("LiveWindow").getSubTable(".status").getEntry("LW Enabled").setBoolean(false)
//
//  LiveWindow.setEnabled(false)

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

  // moved in to get access to this
  /**
    * Starting point for the applications.
    */
  @SuppressWarnings(Array("PMD.UnusedFormalParameter")) def main(args: Array[String]): Unit = {
    //    var robotName = ""
    //    var resources = null
    //    try
    //      resources = classOf[RobotBase].getClassLoader.getResources("META-INF/MANIFEST.MF")
    //    catch {
    //      case ex: IOException =>
    //        ex.printStackTrace()
    //    }
    //    while ( {
    //      resources != null && resources.hasMoreElements
    //    }) try {
    //      val manifest = new Manifest(resources.nextElement.openStream)
    //      robotName = manifest.getMainAttributes.getValue("Robot-Class")
    //    } catch {
    //      case ex: IOException =>
    //        ex.printStackTrace()
    //    }
    //    System.out.println("********** Robot program starting **********")
    //    var robot = null
    //    try
    //      robot = Class.forName(robotName).newInstance.asInstanceOf[RobotBase]
    //    catch {
    //      case throwable: Throwable =>
    //        val cause = throwable.getCause
    //        if (cause != null) throwable = cause
    //        DriverStation.reportError("Unhandled exception instantiating robot " + robotName + " " + throwable.toString, throwable.getStackTrace)
    //        DriverStation.reportWarning("Robots should not quit, but yours did!", false)
    //        DriverStation.reportError("Could not instantiate robot " + robotName + "!", false)
    //        System.exit(1)
    //        return
    //    }

    val robot = this
    try {
      val file = new File("/tmp/frc_versions/FRC_Lib_Version.ini")
      if (file.exists) file.delete
      file.createNewFile
      try {
        val output = new FileOutputStream(file)
        try {
          output.write("Java ".getBytes)
          output.write(WPILibVersion.Version.getBytes)
        } finally if (output != null) output.close()
      }
    } catch {
      case ex: IOException =>
        ex.printStackTrace()
    }
    var errorOnExit = false
    try
      robot.startCompetition()
    catch {
      case _throwable: Throwable =>
        var throwable = _throwable
        val cause = throwable.getCause
        if (cause != null) throwable = cause
        DriverStation.reportError("Unhandled exception: " + throwable.toString, throwable.getStackTrace)
        errorOnExit = true
    } finally {
      // startCompetition never returns unless exception occurs....
      DriverStation.reportWarning("Robots should not quit, but yours did!", false)
      if (errorOnExit) DriverStation.reportError("The startCompetition() method (or methods called by it) should have " + "handled the exception above.", false)
      else DriverStation.reportError("Unexpected return from startCompetition() method.", false)
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
    * Get if the robot is a simulation.
    *
    * @return If the robot is running in simulation.
    */
  def isSimulation: Boolean = !isReal

  /**
    * Get if the robot is real.
    *
    * @return If the robot is running in the real world.
    */
  def isReal: Boolean = HALUtil.getHALRuntimeType == 0

  @SuppressWarnings(Array("JavadocMethod")) def getBooleanProperty(name: String, defaultValue: Boolean): Boolean = {
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
    if (!HAL.initialize(500, 0)) throw new IllegalStateException("Failed to initialize. Terminating")
    // Set some implementations so that the static methods work properly
    Timer.SetImplementation(new HardwareTimer)
    HLUsageReporting.SetImplementation(new HardwareHLUsageReporting)
    RobotState.SetImplementation(DriverStation.getInstance)
    // Call a CameraServer JNI function to force OpenCV native library loading
    // Needed because all the OpenCV JNI functions don't have built in loading
//    CameraServerJNI.enumerateSinks
  }
}
