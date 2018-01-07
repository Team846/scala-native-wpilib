package edu.wpi.first.wpilibj

/*----------------------------------------------------------------------------*/
/* Copyright (c) 2008-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

/**
  * Structure for holding the config data result for PWM.
  */
class PWMConfigDataResult (/**
                             * The maximum PWM value.
                             */
                           var max: Int,

                           /**
                             * The deadband maximum PWM value.
                             */
                           var deadbandMax: Int,

                           /**
                             * The center PWM value.
                             */
                           var center: Int,

                           /**
                             * The deadband minimum PWM value.
                             */
                           var deadbandMin: Int,

                           /**
                             * The minimum PWM value.
                             */
                           var min: Int) {
}
