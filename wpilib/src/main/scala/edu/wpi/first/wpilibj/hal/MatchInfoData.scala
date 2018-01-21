/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.hal

/**
  * Structure for holding the match info data request.
  */
class MatchInfoData {
  /**
    * Stores the event name.
    */
  @SuppressWarnings(Array("MemberName"))
  var eventName: String = ""

  /**
    * Stores the game specific message.
    */
  @SuppressWarnings(Array("MemberName"))
  var gameSpecificMessage: String = ""

  /**
    * Stores the match number.
    */
  @SuppressWarnings(Array("MemberName"))
  var matchNumber = 0

  /**
    * Stores the replay number.
    */
  @SuppressWarnings(Array("MemberName"))
  var replayNumber = 0

  /**
    * Stores the match type.
    */
  @SuppressWarnings(Array("MemberName"))
  var matchType = 0

  /**
    * Called from JNI to set the structure data.
    */
  @SuppressWarnings(Array("JavadocMethod"))
  def setData(eventName: String, gameSpecificMessage: String, matchNumber: Int, replayNumber: Int, matchType: Int): Unit = {
    this.eventName = eventName
    this.gameSpecificMessage = gameSpecificMessage
    this.matchNumber = matchNumber
    this.replayNumber = replayNumber
    this.matchType = matchType
  }
}
