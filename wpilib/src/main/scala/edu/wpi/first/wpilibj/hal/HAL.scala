package edu.wpi.first.wpilibj.hal

import java.nio.ByteBuffer

import com.lynbrookrobotics.scalanativejni._

@jnilib("wpilibJavaJNI")
object HAL extends JNIWrapper {
  def waitForDSData(): Unit = jni

  def initialize(mode: Int): Int = jni

  def observeUserProgramStarting(): Unit = jni

  def observeUserProgramDisabled(): Unit = jni

  def observeUserProgramAutonomous(): Unit = jni

  def observeUserProgramTeleop(): Unit = jni

  def observeUserProgramTest(): Unit = jni

  def report(resource: Int, instanceNumber: Int): Unit = {
    report(resource, instanceNumber, 0, "")
  }

  def report(resource: Int, instanceNumber: Int, context: Int): Unit = {
    report(resource, instanceNumber, context, "")
  }

  /**
    * Report the usage of a resource of interest. <br>
    *
    * <p>Original signature: <code>uint32_t report(tResourceType, uint8_t, uint8_t, const
    * char*)</code>
    *
    * @param resource       one of the values in the tResourceType above (max value 51). <br>
    * @param instanceNumber an index that identifies the resource instance. <br>
    * @param context        an optional additional context number for some cases (such as module
    *                       number). Set to 0 to omit. <br>
    * @param feature        a string to be included describing features in use on a specific
    *                       resource. Setting the same resource more than once allows you to change
    *                       the feature string.
    */
  def report(resource: Int, instanceNumber: Int, context: Int, feature: String): Int = jni

  private def nativeGetControlWord(): Int = jni

  @SuppressWarnings(Array("JavadocMethod"))
  def getControlWord(controlWord: ControlWord): Unit = {
    val word = nativeGetControlWord
    controlWord.update((word & 1) != 0, ((word >> 1) & 1) != 0, ((word >> 2) & 1) != 0,
      ((word >> 3) & 1) != 0, ((word >> 4) & 1) != 0, ((word >> 5) & 1) != 0)
  }

  private def nativeGetAllianceStation(): Int = jni

  @SuppressWarnings(Array("JavadocMethod"))
  def getAllianceStation: AllianceStationID = nativeGetAllianceStation match {
    case 0 =>
      AllianceStationID.Red1
    case 1 =>
      AllianceStationID.Red2
    case 2 =>
      AllianceStationID.Red3
    case 3 =>
      AllianceStationID.Blue1
    case 4 =>
      AllianceStationID.Blue2
    case 5 =>
      AllianceStationID.Blue3
    case _ =>
      null
  }

  var kMaxJoystickAxes = 12
  var kMaxJoystickPOVs = 12

  def getJoystickAxes(joystickNum: Byte, axesArray: Array[Float]): Short = jni

  def getJoystickPOVs(joystickNum: Byte, povsArray: Array[Short]): Short = jni

  def getJoystickButtons(joystickNum: Byte, count: ByteBuffer): Int = jni

  def setJoystickOutputs(joystickNum: Byte, outputs: Int, leftRumble: Short, rightRumble: Short): Int = jni

  def getJoystickIsXbox(joystickNum: Byte): Int = jni

  def getJoystickType(joystickNum: Byte): Int = jni

  def getJoystickName(joystickNum: Byte): String = jni

  def getJoystickAxisType(joystickNum: Byte, axis: Byte): Int = jni

  def getMatchTime: Double = jni

  def getSystemActive: Boolean = jni

  def getBrownedOut: Boolean = jni

  def setErrorData(error: String): Int = jni

  def sendError(isError: Boolean, errorCode: Int, isLVCode: Boolean, details: String, location: String, callStack: String, printMsg: Boolean): Int = jni
}
