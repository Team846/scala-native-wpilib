/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2016-2018. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.hal

/**
  * A wrapper for the HALControlWord bitfield.
  */
class ControlWord {
  private var m_enabled = false
  private var m_autonomous = false
  private var m_test = false
  private var m_emergencyStop = false
  private var m_fmsAttached = false
  private var m_dsAttached = false

  private[hal] def update(enabled: Boolean, autonomous: Boolean, test: Boolean, emergencyStop: Boolean, fmsAttached: Boolean, dsAttached: Boolean): Unit = {
    m_enabled = enabled
    m_autonomous = autonomous
    m_test = test
    m_emergencyStop = emergencyStop
    m_fmsAttached = fmsAttached
    m_dsAttached = dsAttached
  }

  def getEnabled: Boolean = m_enabled

  def getAutonomous: Boolean = m_autonomous

  def getTest: Boolean = m_test

  def getEStop: Boolean = m_emergencyStop

  def getFMSAttached: Boolean = m_fmsAttached

  def getDSAttached: Boolean = m_dsAttached
}
