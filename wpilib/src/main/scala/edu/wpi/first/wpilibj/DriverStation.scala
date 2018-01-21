/*----------------------------------------------------------------------------*/
/* Copyright (c) 2008-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj

import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

//import edu.wpi.first.networktables.NetworkTable
//import edu.wpi.first.networktables.NetworkTableEntry
//import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.hal.AllianceStationID
import edu.wpi.first.wpilibj.hal.ControlWord
import edu.wpi.first.wpilibj.hal.HAL
import edu.wpi.first.wpilibj.hal.MatchInfoData
import edu.wpi.first.wpilibj.hal.PowerJNI

/**
  * DriverStation constructor.
  *
  * <p>The single DriverStation instance is created statically with the instance static member
  * variable.
  */
class DriverStation private() extends RobotState.Interface {
  private[wpilibj] class HALJoystickButtons {
    var m_buttons = 0
    var m_count = 0
  }

  private[wpilibj] class HALJoystickAxes private[wpilibj](val count: Int) {
    var m_axes: Array[Float] = new Array[Float](count)
    var m_count = 0
  }

  private[wpilibj] class HALJoystickPOVs private[wpilibj](val count: Int) {
    var m_povs: Array[Short] = new Array[Short](count)
    var m_count = 0
  }

  private var m_nextMessageTime = 0.0
  
  // Joystick User Data
  private var m_joystickAxes = new Array[HALJoystickAxes](DriverStation.kJoystickPorts)
  private var m_joystickPOVs = new Array[HALJoystickPOVs](DriverStation.kJoystickPorts)
  private var m_joystickButtons = new Array[HALJoystickButtons](DriverStation.kJoystickPorts)
  private var m_matchInfo = new MatchInfoData
  
  // Joystick Cached Data
  private var m_joystickAxesCache = new Array[HALJoystickAxes](DriverStation.kJoystickPorts)
  private var m_joystickPOVsCache = new Array[HALJoystickPOVs](DriverStation.kJoystickPorts)
  private var m_joystickButtonsCache = new Array[HALJoystickButtons](DriverStation.kJoystickPorts)
  private var m_matchInfoCache = new MatchInfoData
  
  // Joystick button rising/falling edge flags
  private[wpilibj] val m_joystickButtonsPressed = new Array[HALJoystickButtons](DriverStation.kJoystickPorts)
  private[wpilibj] val m_joystickButtonsReleased = new Array[HALJoystickButtons](DriverStation.kJoystickPorts)

  // preallocated byte buffer for button count
  private val m_buttonCountBuffer = ByteBuffer.allocateDirect(1)
  private var m_matchDataSender = new DriverStation.MatchDataSender

  // Internal Driver Station thread
  private var m_thread = new Thread(new DriverStation.DriverStationTask(this), "FRCDriverStation")
  private var m_threadKeepAlive = true
  final private var m_cacheDataMutex = new Object
  final private var m_waitForDataMutex = new ReentrantLock
  final private var m_waitForDataCond = m_waitForDataMutex.newCondition
  private var m_waitForDataCount = 0

  // Robot state status variables
  private var m_userInDisabled = false
  private var m_userInAutonomous = false
  private var m_userInTeleop = false
  private var m_userInTest = false

  // Control word variables
  final private var m_controlWordMutex = new Object
  private var m_controlWordCache = new ControlWord
  private var m_lastControlWordUpdate = 0L

  var i = 0
  while ( {
    i < DriverStation.kJoystickPorts
  }) {
    m_joystickButtons(i) = new HALJoystickButtons
    m_joystickAxes(i) = new HALJoystickAxes(HAL.kMaxJoystickAxes)
    m_joystickPOVs(i) = new HALJoystickPOVs(HAL.kMaxJoystickPOVs)
    m_joystickButtonsCache(i) = new HALJoystickButtons
    m_joystickAxesCache(i) = new HALJoystickAxes(HAL.kMaxJoystickAxes)
    m_joystickPOVsCache(i) = new HALJoystickPOVs(HAL.kMaxJoystickPOVs)
    m_joystickButtonsPressed(i) = new HALJoystickButtons
    m_joystickButtonsReleased(i) = new HALJoystickButtons

    {
      i += 1; i - 1
    }
  }

  m_thread.setPriority((Thread.NORM_PRIORITY + Thread.MAX_PRIORITY) / 2)
  m_thread.start()

  /**
    * Kill the thread.
    */
  def release(): Unit = {
    m_threadKeepAlive = false
  }

  /**
    * The state of one joystick button. Button indexes begin at 1.
    *
    * @param stick  The joystick to read.
    * @param button The button index, beginning at 1.
    * @return The state of the joystick button.
    */
  def getStickButton(stick: Int, button: Int): Boolean = {
    if (button <= 0) {
      reportJoystickUnpluggedError("Button indexes begin at 1 in WPILib for C++ and Java\n")
      return false
    }
    if (stick < 0 || stick >= DriverStation.kJoystickPorts) throw new RuntimeException("Joystick index is out of range, should be 0-3")
    var error = false
    var retVal = false
    m_cacheDataMutex.synchronized {
      if (button > m_joystickButtons(stick).m_count) {
        error = true
        retVal = false
      }
      else retVal = (m_joystickButtons(stick).m_buttons & 1 << (button - 1)) != 0
    }

    if (error) reportJoystickUnpluggedWarning("Joystick Button " + button + " on port " + stick + " not available, check if controller is plugged in")
    retVal
  }

  /**
    * Whether one joystick button was pressed since the last check. Button indexes begin at 1.
    *
    * @param stick  The joystick to read.
    * @param button The button index, beginning at 1.
    * @return Whether the joystick button was pressed since the last check.
    */
  private[wpilibj] def getStickButtonPressed(stick: Int, button: Int): Boolean = {
    if (button <= 0) {
      reportJoystickUnpluggedError("Button indexes begin at 1 in WPILib for C++ and Java\n")
      return false
    }
    if (stick < 0 || stick >= DriverStation.kJoystickPorts) throw new RuntimeException("Joystick index is out of range, should be 0-3")
    // If button was pressed, clear flag and return true
    if ((m_joystickButtonsPressed(stick).m_buttons & 1 << (button - 1)) != 0) {
      m_joystickButtonsPressed(stick).m_buttons &= ~(1 << (button - 1))
      true
    }
    else false
  }

  /**
    * Whether one joystick button was released since the last check. Button indexes
    * begin at 1.
    *
    * @param stick  The joystick to read.
    * @param button The button index, beginning at 1.
    * @return Whether the joystick button was released since the last check.
    */
  private[wpilibj] def getStickButtonReleased(stick: Int, button: Int): Boolean = {
    if (button <= 0) {
      reportJoystickUnpluggedError("Button indexes begin at 1 in WPILib for C++ and Java\n")
      return false
    }
    if (stick < 0 || stick >= DriverStation.kJoystickPorts) throw new RuntimeException("Joystick index is out of range, should be 0-3")
    // If button was released, clear flag and return true
    if ((m_joystickButtonsReleased(stick).m_buttons & 1 << (button - 1)) != 0) {
      m_joystickButtonsReleased(stick).m_buttons &= ~(1 << (button - 1))
      true
    }
    else false
  }

  /**
    * Get the value of the axis on a joystick. This depends on the mapping of the joystick connected
    * to the specified port.
    *
    * @param stick The joystick to read.
    * @param axis  The analog axis value to read from the joystick.
    * @return The value of the axis on the joystick.
    */
  def getStickAxis(stick: Int, axis: Int): Double = {
    if (stick < 0 || stick >= DriverStation.kJoystickPorts) throw new RuntimeException("Joystick index is out of range, should be 0-5")
    if (axis < 0 || axis >= HAL.kMaxJoystickAxes) throw new RuntimeException("Joystick axis is out of range")
    var error = false
    var retVal = 0.0
    m_cacheDataMutex.synchronized {
      if (axis >= m_joystickAxes(stick).m_count) { // set error
        error = true
        retVal = 0.0
      }
      else retVal = m_joystickAxes(stick).m_axes(axis)
    }

    if (error) reportJoystickUnpluggedWarning("Joystick axis " + axis + " on port " + stick + " not available, check if controller is plugged in")
    retVal
  }

  /**
    * Get the state of a POV on the joystick.
    *
    * @return the angle of the POV in degrees, or -1 if the POV is not pressed.
    */
  def getStickPOV(stick: Int, pov: Int): Int = {
    if (stick < 0 || stick >= DriverStation.kJoystickPorts) throw new RuntimeException("Joystick index is out of range, should be 0-5")
    if (pov < 0 || pov >= HAL.kMaxJoystickPOVs) throw new RuntimeException("Joystick POV is out of range")
    var error = false
    var retVal = -1
    m_cacheDataMutex.synchronized {
      if (pov >= m_joystickPOVs(stick).m_count) {
        error = true
        retVal = -1
      }
      else retVal = m_joystickPOVs(stick).m_povs(pov)
    }

    if (error) reportJoystickUnpluggedWarning("Joystick POV " + pov + " on port " + stick + " not available, check if controller is plugged in")
    retVal
  }

  /**
    * The state of the buttons on the joystick.
    *
    * @param stick The joystick to read.
    * @return The state of the buttons on the joystick.
    */
  def getStickButtons(stick: Int): Int = {
    if (stick < 0 || stick >= DriverStation.kJoystickPorts) throw new RuntimeException("Joystick index is out of range, should be 0-3")
    m_cacheDataMutex.synchronized {
      m_joystickButtons(stick).m_buttons
    }
  }

  /**
    * Returns the number of axes on a given joystick port.
    *
    * @param stick The joystick port number
    * @return The number of axes on the indicated joystick
    */
  def getStickAxisCount(stick: Int): Int = {
    if (stick < 0 || stick >= DriverStation.kJoystickPorts) throw new RuntimeException("Joystick index is out of range, should be 0-5")
    m_cacheDataMutex.synchronized {
      m_joystickAxes(stick).m_count
    }
  }

  /**
    * Returns the number of POVs on a given joystick port.
    *
    * @param stick The joystick port number
    * @return The number of POVs on the indicated joystick
    */
  def getStickPOVCount(stick: Int): Int = {
    if (stick < 0 || stick >= DriverStation.kJoystickPorts) throw new RuntimeException("Joystick index is out of range, should be 0-5")
    m_cacheDataMutex.synchronized {
      m_joystickPOVs(stick).m_count
    }
  }

  /**
    * Gets the number of buttons on a joystick.
    *
    * @param stick The joystick port number
    * @return The number of buttons on the indicated joystick
    */
  def getStickButtonCount(stick: Int): Int = {
    if (stick < 0 || stick >= DriverStation.kJoystickPorts) throw new RuntimeException("Joystick index is out of range, should be 0-5")
    m_cacheDataMutex.synchronized {
      m_joystickButtons(stick).m_count
    }
  }

  /**
    * Gets the value of isXbox on a joystick.
    *
    * @param stick The joystick port number
    * @return A boolean that returns the value of isXbox
    */
  def getJoystickIsXbox(stick: Int): Boolean = {
    if (stick < 0 || stick >= DriverStation.kJoystickPorts) throw new RuntimeException("Joystick index is out of range, should be 0-5")
    var error = false
    var retVal = false
    m_cacheDataMutex.synchronized { // TODO: Remove this when calling for descriptor on empty stick no longer
      // crashes
      if (1 > m_joystickButtons(stick).m_count && 1 > m_joystickAxes(stick).m_count) {
        error = true
        retVal = false
      }
      else if (HAL.getJoystickIsXbox(stick.toByte) == 1) retVal = true
    }

    if (error) reportJoystickUnpluggedWarning("Joystick on port " + stick + " not available, check if controller is plugged in")
    retVal
  }

  /**
    * Gets the value of type on a joystick.
    *
    * @param stick The joystick port number
    * @return The value of type
    */
  def getJoystickType(stick: Int): Int = {
    if (stick < 0 || stick >= DriverStation.kJoystickPorts) throw new RuntimeException("Joystick index is out of range, should be 0-5")
    var error = false
    var retVal = -1
    m_cacheDataMutex.synchronized {
      if (1 > m_joystickButtons(stick).m_count && 1 > m_joystickAxes(stick).m_count) {
        error = true
        retVal = -1
      }
      else retVal = HAL.getJoystickType(stick.toByte)
    }

    if (error) reportJoystickUnpluggedWarning("Joystick on port " + stick + " not available, check if controller is plugged in")
    retVal
  }

  /**
    * Gets the name of the joystick at a port.
    *
    * @param stick The joystick port number
    * @return The value of name
    */
  def getJoystickName(stick: Int): String = {
    if (stick < 0 || stick >= DriverStation.kJoystickPorts) throw new RuntimeException("Joystick index is out of range, should be 0-5")
    var error = false
    var retVal = ""
    m_cacheDataMutex.synchronized {
      if (1 > m_joystickButtons(stick).m_count && 1 > m_joystickAxes(stick).m_count) {
        error = true
        retVal = ""
      }
      else retVal = HAL.getJoystickName(stick.toByte)
    }

    if (error) reportJoystickUnpluggedWarning("Joystick on port " + stick + " not available, check if controller is plugged in")
    retVal
  }

  /**
    * Returns the types of Axes on a given joystick port.
    *
    * @param stick The joystick port number
    * @param axis  The target axis
    * @return What type of axis the axis is reporting to be
    */
  def getJoystickAxisType(stick: Int, axis: Int): Int = {
    if (stick < 0 || stick >= DriverStation.kJoystickPorts) throw new RuntimeException("Joystick index is out of range, should be 0-5")
    var retVal = -1
    m_cacheDataMutex.synchronized {
      retVal = HAL.getJoystickAxisType(stick.toByte, axis.toByte)
    }

    retVal
  }

  /**
    * Gets a value indicating whether the Driver Station requires the robot to be enabled.
    *
    * @return True if the robot is enabled, false otherwise.
    */
  override def isEnabled: Boolean = {
    m_controlWordMutex.synchronized {
      updateControlWord(false)
    }
    m_controlWordCache.getEnabled && m_controlWordCache.getDSAttached

  }

  /**
    * Gets a value indicating whether the Driver Station requires the robot to be disabled.
    *
    * @return True if the robot should be disabled, false otherwise.
    */
  override def isDisabled: Boolean = !isEnabled

  /**
    * Gets a value indicating whether the Driver Station requires the robot to be running in
    * autonomous mode.
    *
    * @return True if autonomous mode should be enabled, false otherwise.
    */
  override def isAutonomous: Boolean = {
    m_controlWordMutex.synchronized {
      updateControlWord(false)
    }
    m_controlWordCache.getAutonomous

  }

  /**
    * Gets a value indicating whether the Driver Station requires the robot to be running in
    * operator-controlled mode.
    *
    * @return True if operator-controlled mode should be enabled, false otherwise.
    */
  override def isOperatorControl: Boolean = !(isAutonomous || isTest)

  /**
    * Gets a value indicating whether the Driver Station requires the robot to be running in test
    * mode.
    *
    * @return True if test mode should be enabled, false otherwise.
    */
  override def isTest: Boolean = {
    m_controlWordMutex.synchronized {
      updateControlWord(false)
    }
    m_controlWordCache.getTest

  }

  /**
    * Gets a value indicating whether the Driver Station is attached.
    *
    * @return True if Driver Station is attached, false otherwise.
    */
  def isDSAttached: Boolean = {
    m_controlWordMutex.synchronized {
      updateControlWord(false)
    }
    m_controlWordCache.getDSAttached

  }

  /**
    * Gets if a new control packet from the driver station arrived since the last time this function
    * was called.
    *
    * @return True if the control data has been updated since the last call.
    */
  def isNewControlData: Boolean = HAL.isNewControlData

  /**
    * Gets if the driver station attached to a Field Management System.
    *
    * @return true if the robot is competing on a field being controlled by a Field Management System
    */
  def isFMSAttached: Boolean = {
    m_controlWordMutex.synchronized {
      updateControlWord(false)
    }
    m_controlWordCache.getFMSAttached

  }

  /**
    * Gets a value indicating whether the FPGA outputs are enabled. The outputs may be disabled if
    * the robot is disabled or e-stopped, the watchdog has expired, or if the roboRIO browns out.
    *
    * @return True if the FPGA outputs are enabled.
    * @deprecated Use RobotController.isSysActive()
    */
  @deprecated def isSysActive: Boolean = HAL.getSystemActive

  /**
    * Check if the system is browned out.
    *
    * @return True if the system is browned out
    * @deprecated Use RobotController.isBrownedOut()
    */
  @deprecated def isBrownedOut: Boolean = HAL.getBrownedOut

  /**
    * Get the game specific message.
    *
    * @return the game specific message
    */
  def getGameSpecificMessage: String = {
    m_cacheDataMutex.synchronized {
      m_matchInfo.gameSpecificMessage
    }
  }

  /**
    * Get the event name.
    *
    * @return the event name
    */
  def getEventName: String = {
    m_cacheDataMutex.synchronized {
      m_matchInfo.eventName
    }
  }

  /**
    * Get the match type.
    *
    * @return the match type
    */
  def getMatchType: DriverStation.MatchType = {
    var matchType = 0
    m_cacheDataMutex.synchronized {
      matchType = m_matchInfo.matchType
    }

    matchType match {
      case 1 =>
        DriverStation.MatchType.Practice
      case 2 =>
        DriverStation.MatchType.Qualification
      case 3 =>
        DriverStation.MatchType.Elimination
      case _ =>
        DriverStation.MatchType.None
    }
  }

  /**
    * Get the match number.
    *
    * @return the match number
    */
  def getMatchNumber: Int = {
    m_cacheDataMutex.synchronized {
      m_matchInfo.matchNumber
    }
  }

  /**
    * Get the replay number.
    *
    * @return the replay number
    */
  def getReplayNumber: Int = {
    m_cacheDataMutex.synchronized {
      m_matchInfo.replayNumber
    }
  }

  /**
    * Get the current alliance from the FMS.
    *
    * @return the current alliance
    */
  def getAlliance: DriverStation.Alliance = {
    val allianceStationID = HAL.getAllianceStation
    if (allianceStationID == null) return DriverStation.Alliance.Invalid
    import AllianceStationID._
    allianceStationID match {
      case Red1 => DriverStation.Alliance.Red
      case Red2 => DriverStation.Alliance.Red
      case Red3 => DriverStation.Alliance.Red
      case Blue1 => DriverStation.Alliance.Blue
      case Blue2 => DriverStation.Alliance.Blue
      case Blue3 => DriverStation.Alliance.Blue
      case _ => DriverStation.Alliance.Invalid
    }
  }

  /**
    * Gets the location of the team's driver station controls.
    *
    * @return the location of the team's driver station controls: 1, 2, or 3
    */
  def getLocation: Int = {
    val allianceStationID = HAL.getAllianceStation
    if (allianceStationID == null) return 0
    import AllianceStationID._
    allianceStationID match {
      case Red1 => 1
      case Blue1 => 1
      case Red2 => 2
      case Blue2 => 2
      case Blue3 => 3
      case Red3 => 3
      case _ => 0
    }
  }

  /**
    * Wait for new data from the driver station.
    */
  def waitForData(): Unit = {
    waitForData(0)
  }

  /**
    * Wait for new data or for timeout, which ever comes first. If timeout is 0, wait for new data
    * only.
    *
    * @param timeout The maximum time in seconds to wait.
    * @return true if there is new data, otherwise false
    */
  def waitForData(timeout: Double): Boolean = {
    val startTime = RobotController.getFPGATime
    val timeoutMicros = (timeout * 1000000).toLong
    m_waitForDataMutex.lock()
    try {
      val currentCount = m_waitForDataCount
      while ( {
        m_waitForDataCount == currentCount
      }) if (timeout > 0) {
        val now = RobotController.getFPGATime
        if (now < startTime + timeoutMicros) { // We still have time to wait
          val signaled = m_waitForDataCond.await(startTime + timeoutMicros - now, TimeUnit.MICROSECONDS)
          if (!signaled) { // Return false if a timeout happened
            return false
          }
        }
        else { // Time has elapsed.
          return false
        }
      }
      else m_waitForDataCond.await()
      // Return true if we have received a proper signal
      true
    } catch {
      case ex: InterruptedException =>
        // return false on a thread interrupt
        false
    } finally m_waitForDataMutex.unlock()
  }

  /**
    * Return the approximate match time. The FMS does not send an official match time to the robots,
    * but does send an approximate match time. The value will count down the time remaining in the
    * current period (auto or teleop). Warning: This is not an official time (so it cannot be used to
    * dispute ref calls or guarantee that a function will trigger before the match ends) The
    * Practice Match function of the DS approximates the behaviour seen on the field.
    *
    * @return Time remaining in current match period (auto or teleop) in seconds
    */
  def getMatchTime: Double = HAL.getMatchTime

  /**
    * Read the battery voltage.
    *
    * @return The battery voltage in Volts.
    * @deprecated Use RobotController.getBatteryVoltage
    */
  @deprecated def getBatteryVoltage: Double = PowerJNI.getVinVoltage

  /**
    * Only to be used to tell the Driver Station what code you claim to be executing for diagnostic
    * purposes only.
    *
    * @param entering If true, starting disabled code; if false, leaving disabled code
    */
  @SuppressWarnings(Array("MethodName")) def InDisabled(entering: Boolean): Unit = {
    m_userInDisabled = entering
  }

  /**
    * Only to be used to tell the Driver Station what code you claim to be executing for diagnostic
    * purposes only.
    *
    * @param entering If true, starting autonomous code; if false, leaving autonomous code
    */
  @SuppressWarnings(Array("MethodName")) def InAutonomous(entering: Boolean): Unit = {
    m_userInAutonomous = entering
  }

  /**
    * Only to be used to tell the Driver Station what code you claim to be executing for diagnostic
    * purposes only.
    *
    * @param entering If true, starting teleop code; if false, leaving teleop code
    */
  @SuppressWarnings(Array("MethodName")) def InOperatorControl(entering: Boolean): Unit = {
    m_userInTeleop = entering
  }

  /**
    * Only to be used to tell the Driver Station what code you claim to be executing for diagnostic
    * purposes only.
    *
    * @param entering If true, starting test code; if false, leaving test code
    */
  @SuppressWarnings(Array("MethodName")) def InTest(entering: Boolean): Unit = {
    m_userInTest = entering
  }

  private def sendMatchData(): Unit = {
    val alliance = HAL.getAllianceStation
    var isRedAlliance = false
    var stationNumber = 1
    import AllianceStationID._
    alliance match {
      case Blue1 =>
        isRedAlliance = false
        stationNumber = 1
      case Blue2 =>
        isRedAlliance = false
        stationNumber = 2
      case Blue3 =>
        isRedAlliance = false
        stationNumber = 3
      case Red1 =>
        isRedAlliance = true
        stationNumber = 1
      case Red2 =>
        isRedAlliance = true
        stationNumber = 2
      case _ =>
        isRedAlliance = true
        stationNumber = 3
    }

    var eventName: String = null
    var gameSpecificMessage: String = null
    var matchNumber = 0
    var replayNumber = 0
    var matchType = 0
    m_cacheDataMutex.synchronized {
      eventName = m_matchInfo.eventName
      gameSpecificMessage = m_matchInfo.gameSpecificMessage
      matchNumber = m_matchInfo.matchNumber
      replayNumber = m_matchInfo.replayNumber
      matchType = m_matchInfo.matchType
    }

//    m_matchDataSender.alliance.setBoolean(isRedAlliance)
//    m_matchDataSender.station.setDouble(stationNumber)
//    m_matchDataSender.eventName.setString(eventName)
//    m_matchDataSender.gameSpecificMessage.setString(gameSpecificMessage)
//    m_matchDataSender.matchNumber.setDouble(matchNumber)
//    m_matchDataSender.replayNumber.setDouble(replayNumber)
//    m_matchDataSender.matchType.setDouble(matchType)
//    m_matchDataSender.controlWord.setDouble(HAL.nativeGetControlWord)
  }

  /**
    * Copy data from the DS task for the user. If no new data exists, it will just be returned,
    * otherwise the data will be copied from the DS polling loop.
    */
  protected def getData(): Unit = { // Get the status of all of the joysticks
    var stick: Byte = 0
    while ( {
      stick < DriverStation.kJoystickPorts
    }) {
      m_joystickAxesCache(stick).m_count = HAL.getJoystickAxes(stick, m_joystickAxesCache(stick).m_axes)
      m_joystickPOVsCache(stick).m_count = HAL.getJoystickPOVs(stick, m_joystickPOVsCache(stick).m_povs)
      m_joystickButtonsCache(stick).m_buttons = HAL.getJoystickButtons(stick, m_buttonCountBuffer)
      m_joystickButtonsCache(stick).m_count = m_buttonCountBuffer.get(0)

      stick = (stick + 1).toByte
    }
    HAL.getMatchInfo(m_matchInfoCache)
    // Force a control word update, to make sure the data is the newest.
    updateControlWord(true)
    // lock joystick mutex to swap cache data
    m_cacheDataMutex.synchronized {
      var i = 0
      while ( {
        i < DriverStation.kJoystickPorts
      }) { // If buttons weren't pressed and are now, set flags in m_buttonsPressed
        m_joystickButtonsPressed(i).m_buttons |= ~m_joystickButtons(i).m_buttons & m_joystickButtonsCache(i).m_buttons
        // If buttons were pressed and aren't now, set flags in m_buttonsReleased
        m_joystickButtonsReleased(i).m_buttons |= m_joystickButtons(i).m_buttons & ~m_joystickButtonsCache(i).m_buttons

        {
          i += 1;
          i - 1
        }
      }
      // move cache to actual data
      val currentAxes = m_joystickAxes
      m_joystickAxes = m_joystickAxesCache
      m_joystickAxesCache = currentAxes
      val currentButtons = m_joystickButtons
      m_joystickButtons = m_joystickButtonsCache
      m_joystickButtonsCache = currentButtons
      val currentPOVs = m_joystickPOVs
      m_joystickPOVs = m_joystickPOVsCache
      m_joystickPOVsCache = currentPOVs
      val currentInfo = m_matchInfo
      m_matchInfo = m_matchInfoCache
      m_matchInfoCache = currentInfo
    }

    m_waitForDataMutex.lock()
    m_waitForDataCount += 1
    m_waitForDataCond.signalAll()
    m_waitForDataMutex.unlock()
    sendMatchData()
  }

  /**
    * Reports errors related to unplugged joysticks Throttles the errors so that they don't overwhelm
    * the DS.
    */
  private def reportJoystickUnpluggedError(message: String): Unit = {
    val currentTime = Timer.getFPGATimestamp
    if (currentTime > m_nextMessageTime) {
      DriverStation.reportError(message, false)
      m_nextMessageTime = currentTime + DriverStation.JOYSTICK_UNPLUGGED_MESSAGE_INTERVAL
    }
  }

  private def reportJoystickUnpluggedWarning(message: String): Unit = {
    val currentTime = Timer.getFPGATimestamp
    if (currentTime > m_nextMessageTime) {
      DriverStation.reportWarning(message, false)
      m_nextMessageTime = currentTime + DriverStation.JOYSTICK_UNPLUGGED_MESSAGE_INTERVAL
    }
  }

  /**
    * Provides the service routine for the DS polling m_thread.
    */
  private def run(): Unit = {
    var safetyCounter = 0
    while ( {
      m_threadKeepAlive
    }) {
      HAL.waitForDSData()
      getData()
      if (isDisabled) safetyCounter = 0
      if ( {
        safetyCounter += 1; safetyCounter
      } >= 4) {
        MotorSafetyHelper.checkMotors()
        safetyCounter = 0
      }
      if (m_userInDisabled) HAL.observeUserProgramDisabled()
      if (m_userInAutonomous) HAL.observeUserProgramAutonomous()
      if (m_userInTeleop) HAL.observeUserProgramTeleop()
      if (m_userInTest) HAL.observeUserProgramTest()
    }
  }

  /**
    * Updates the data in the control word cache. Updates if the force parameter is set, or if
    * 50ms have passed since the last update.
    *
    * @param force True to force an update to the cache, otherwise update if 50ms have passed.
    */
  private def updateControlWord(force: Boolean): Unit = {
    val now = System.currentTimeMillis
    m_controlWordMutex.synchronized {
      if (now - m_lastControlWordUpdate > 50 || force) {
        HAL.getControlWord(m_controlWordCache)
        m_lastControlWordUpdate = now
      }
    }
  }
}

/**
  * Provide access to the network communication data to / from the Driver Station.
  */
object DriverStation {
  /**
    * Number of Joystick Ports.
    */
  val kJoystickPorts = 6

  /**
    * The robot alliance that the robot is a part of.
    */
  type Alliance = Alliance.Value
  object Alliance extends Enumeration {
    val Red, Blue, Invalid = Value
  }

  type MatchType = MatchType.Value
  object MatchType extends Enumeration {
    val None, Practice, Qualification, Elimination = Value
  }

  private val JOYSTICK_UNPLUGGED_MESSAGE_INTERVAL = 1.0

  private class DriverStationTask private[wpilibj](var m_ds: DriverStation) extends Runnable {
    override def run(): Unit = {
      m_ds.run()
    }

    /* DriverStationTask */
  }

  private class MatchDataSender private[wpilibj]() {
//    @SuppressWarnings(Array("MemberName")) private[wpilibj] var table = NetworkTableInstance.getDefault.getTable("FMSInfo")
//    @SuppressWarnings(Array("MemberName")) private[wpilibj] var typeMetadata = table.getEntry(".type")
//    typeMetadata.forceSetString("FMSInfo")
//    @SuppressWarnings(Array("MemberName")) private[wpilibj] var gameSpecificMessage = table.getEntry("GameSpecificMessage")
//    gameSpecificMessage.forceSetString("")
//    @SuppressWarnings(Array("MemberName")) private[wpilibj] var eventName = table.getEntry("EventName")
//    eventName.forceSetString("")
//    @SuppressWarnings(Array("MemberName")) private[wpilibj] var matchNumber = table.getEntry("MatchNumber")
//    matchNumber.forceSetDouble(0)
//    @SuppressWarnings(Array("MemberName")) private[wpilibj] var replayNumber = table.getEntry("ReplayNumber")
//    replayNumber.forceSetDouble(0)
//    @SuppressWarnings(Array("MemberName")) private[wpilibj] var matchType = table.getEntry("MatchType")
//    matchType.forceSetDouble(0)
//    @SuppressWarnings(Array("MemberName")) private[wpilibj] var alliance = table.getEntry("IsRedAlliance")
//    alliance.forceSetBoolean(true)
//    @SuppressWarnings(Array("MemberName")) private[wpilibj] var station = table.getEntry("StationNumber")
//    station.forceSetDouble(1)
//    @SuppressWarnings(Array("MemberName")) private[wpilibj] var controlWord = table.getEntry("FMSControlData")
//    controlWord.forceSetDouble(0)
  }

  private val instance = new DriverStation

  /**
    * Gets an instance of the DriverStation.
    *
    * @return The DriverStation.
    */
  def getInstance: DriverStation = DriverStation.instance

  /**
    * Report error to Driver Station. Optionally appends Stack trace
    * to error message.
    *
    * @param printTrace If true, append stack trace to error string
    */
  def reportError(error: String, printTrace: Boolean): Unit = {
    reportErrorImpl(true, 1, error, printTrace)
  }

  /**
    * Report error to Driver Station. Appends provided stack trace
    * to error message.
    *
    * @param stackTrace The stack trace to append
    */
  def reportError(error: String, stackTrace: Array[StackTraceElement]): Unit = {
    reportErrorImpl(true, 1, error, stackTrace)
  }

  /**
    * Report warning to Driver Station. Optionally appends Stack
    * trace to warning message.
    *
    * @param printTrace If true, append stack trace to warning string
    */
  def reportWarning(error: String, printTrace: Boolean): Unit = {
    reportErrorImpl(false, 1, error, printTrace)
  }

  /**
    * Report warning to Driver Station. Appends provided stack
    * trace to warning message.
    *
    * @param stackTrace The stack trace to append
    */
  def reportWarning(error: String, stackTrace: Array[StackTraceElement]): Unit = {
    reportErrorImpl(false, 1, error, stackTrace)
  }

  private def reportErrorImpl(isError: Boolean, code: Int, error: String, printTrace: Boolean): Unit = {
    reportErrorImpl(isError, code, error, printTrace, Thread.currentThread.getStackTrace, 3)
  }

  private def reportErrorImpl(isError: Boolean, code: Int, error: String, stackTrace: Array[StackTraceElement]): Unit = {
    reportErrorImpl(isError, code, error, true, stackTrace, 0)
  }

  private def reportErrorImpl(isError: Boolean, code: Int, error: String, printTrace: Boolean, stackTrace: Array[StackTraceElement], stackTraceFirst: Int): Unit = {
    var locString: String = null
    if (stackTrace.length >= stackTraceFirst + 1) locString = stackTrace(stackTraceFirst).toString
    else locString = ""
    var traceString = ""
    if (printTrace) {
      var haveLoc = false
      var i = stackTraceFirst
      while ( {
        i < stackTrace.length
      }) {
        val loc = stackTrace(i).toString
        traceString += "\tat " + loc + "\n"
        // get first user function
        if (!haveLoc && !loc.startsWith("edu.wpi.first")) {
          locString = loc
          haveLoc = true
        }

        {
          i += 1; i - 1
        }
      }
    }
    HAL.sendError(isError, code, false, error, locString, traceString, true)
  }
}
