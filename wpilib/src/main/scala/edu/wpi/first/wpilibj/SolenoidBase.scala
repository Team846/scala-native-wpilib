/*----------------------------------------------------------------------------*/
/* Copyright (c) 2008-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj

import edu.wpi.first.wpilibj.hal.SolenoidJNI

/**
  * Constructor.
  *
  * @param moduleNumber The PCM CAN ID
  */
abstract class SolenoidBase(val m_moduleNumber: Int // The number of the solenoid module being used.
                           ) /*extends SendableBase*/ {
  /**
    * Read all 8 solenoids from the module used by this solenoid as a single byte.
    *
    * @return The current value of all 8 solenoids on this module.
    */
  def getAll: Int = SolenoidBase.getAll(m_moduleNumber)

  /**
    * Reads complete solenoid blacklist for all 8 solenoids as a single byte. If a solenoid is
    * shorted, it is added to the blacklist and disabled until power cycle, or until faults are
    * cleared.
    *
    * @return The solenoid blacklist of all 8 solenoids on the module.
    * @see #clearAllPCMStickyFaults()
    */
  def getPCMSolenoidBlackList: Int = SolenoidBase.getPCMSolenoidBlackList(m_moduleNumber)

  /**
    * If true, the common highside solenoid voltage rail is too low, most likely a solenoid channel
    * is shorted.
    *
    * @return true if PCM sticky fault is set
    */
  def getPCMSolenoidVoltageStickyFault: Boolean = SolenoidBase.getPCMSolenoidVoltageStickyFault(m_moduleNumber)

  /**
    * The common highside solenoid voltage rail is too low, most likely a solenoid channel is
    * shorted.
    *
    * @return true if PCM is in fault state.
    */
  def getPCMSolenoidVoltageFault: Boolean = SolenoidBase.getPCMSolenoidVoltageFault(m_moduleNumber)

  /**
    * Clear ALL sticky faults inside PCM that Compressor is wired to.
    *
    * <p>If a sticky fault is set, then it will be persistently cleared. Compressor drive maybe
    * momentarily disable while flags are being cleared. Care should be taken to not call this too
    * frequently, otherwise normal compressor functionality may be prevented.
    *
    * <p>If no sticky faults are set then this call will have no effect.
    */
  def clearAllPCMStickyFaults(): Unit = {
    SolenoidBase.clearAllPCMStickyFaults(m_moduleNumber)
  }
}

/**
  * SolenoidBase class is the common base class for the {@link Solenoid} and {@link DoubleSolenoid}
  * classes.
  */
object SolenoidBase {
  /**
    * Read all 8 solenoids from the specified module as a single byte.
    *
    * @param moduleNumber the module number to read
    * @return The current value of all 8 solenoids on the module.
    */
  def getAll(moduleNumber: Int): Int = SolenoidJNI.getAllSolenoids(moduleNumber)

  /**
    * Reads complete solenoid blacklist for all 8 solenoids as a single byte. If a solenoid is
    * shorted, it is added to the blacklist and disabled until power cycle, or until faults are
    * cleared.
    *
    * @param moduleNumber the module number to read
    * @return The solenoid blacklist of all 8 solenoids on the module.
    * @see #clearAllPCMStickyFaults()
    */
  def getPCMSolenoidBlackList(moduleNumber: Int): Int = SolenoidJNI.getPCMSolenoidBlackList(moduleNumber)

  /**
    * If true, the common highside solenoid voltage rail is too low, most likely a solenoid channel
    * is shorted.
    *
    * @param moduleNumber the module number to read
    * @return true if PCM sticky fault is set
    */
  def getPCMSolenoidVoltageStickyFault(moduleNumber: Int): Boolean = SolenoidJNI.getPCMSolenoidVoltageStickyFault(moduleNumber)

  /**
    * The common highside solenoid voltage rail is too low, most likely a solenoid channel is
    * shorted.
    *
    * @param moduleNumber the module number to read
    * @return true if PCM is in fault state.
    */
  def getPCMSolenoidVoltageFault(moduleNumber: Int): Boolean = SolenoidJNI.getPCMSolenoidVoltageFault(moduleNumber)

  /**
    * Clear ALL sticky faults inside PCM that Compressor is wired to.
    *
    * <p>If a sticky fault is set, then it will be persistently cleared. Compressor drive maybe
    * momentarily disable while flags are being cleared. Care should be taken to not call this too
    * frequently, otherwise normal compressor functionality may be prevented.
    *
    * <p>If no sticky faults are set then this call will have no effect.
    *
    * @param moduleNumber the module number to read
    */
  def clearAllPCMStickyFaults(moduleNumber: Int): Unit = {
    SolenoidJNI.clearAllPCMStickyFaults(moduleNumber)
  }
}
