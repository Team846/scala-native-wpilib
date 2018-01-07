/*----------------------------------------------------------------------------*/
/* Copyright (c) 2008-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj

import edu.wpi.first.wpilibj.hal.HALUtil

/**
  * Contains global utility functions.
  *
  * @deprecated Use RobotController class instead
  */
@deprecated object Utility {
  /**
    * Read the microsecond timer from the FPGA.
    *
    * @return The current time in microseconds according to the FPGA.
    * @deprecated Use RobotController.getFPGATime()
    */
  @deprecated
  @SuppressWarnings(Array("AbbreviationAsWordInName")) def getFPGATime: Long = HALUtil.getFPGATime

  /**
    * Get the state of the "USER" button on the roboRIO.
    *
    * @return true if the button is currently pressed down
    * @deprecated Use RobotController.getUserButton()
    */
  @deprecated def getUserButton: Boolean = HALUtil.getFPGAButton
}

@deprecated final class Utility private() {
  /**
    * Return the FPGA Version number. For now, expect this to be 2009.
    *
    * @return FPGA Version number.
    * @deprecated Use RobotController.getFPGAVersion()
    */
  @SuppressWarnings(Array("AbbreviationAsWordInName"))
  @deprecated private[wpilibj] def getFPGAVersion = HALUtil.getFPGAVersion

  /**
    * Return the FPGA Revision number. The format of the revision is 3 numbers. The 12 most
    * significant bits are the Major Revision. the next 8 bits are the Minor Revision. The 12 least
    * significant bits are the Build Number.
    *
    * @return FPGA Revision number.
    * @deprecated Use RobotController.getFPGARevision()
    */
  @SuppressWarnings(Array("AbbreviationAsWordInName"))
  @deprecated private[wpilibj] def getFPGARevision = HALUtil.getFPGARevision.toLong
}
