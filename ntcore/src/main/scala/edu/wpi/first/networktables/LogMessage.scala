/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2017-2018. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.networktables

/** Constructor.
  * This should generally only be used internally to NetworkTables.
  *
  * @param inst     Instance
  * @param logger   Logger
  * @param level    Log level
  * @param filename Filename
  * @param line     Line number
  * @param message  Message
  */
final class LogMessage(val inst: NetworkTableInstance,

                       /**
                         * The logger that generated the message.
                         */
                       val logger: Int,

                       /**
                         * Log level of the message.
                         */
                       val level: Int,

                       /**
                         * The filename of the source file that generated the message.
                         */
                       val filename: String,

                       /**
                         * The line number in the source file that generated the message.
                         */
                       val line: Int,

                       /**
                         * The message.
                         */
                       val message: String) {
  private[networktables] def getInstance = inst
}

/**
  * NetworkTables log message.
  */
object LogMessage {
  /**
    * Logging levels.
    */
  val kCritical = 50
  val kError = 40
  val kWarning = 30
  val kInfo = 20
  val kDebug = 10
  val kDebug1 = 9
  val kDebug2 = 8
  val kDebug3 = 7
  val kDebug4 = 6
}
