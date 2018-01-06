/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2016-2017. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.hal

/**
  * JNI wrapper for library <b>FRC_NetworkCommunication</b><br>.
  */
object FRCNetComm extends JNIWrapper {
  /**
    * Module type from LoadOut.h
    */
  object tModuleType {
    val kModuleType_Unknown = 0x00
    val kModuleType_Analog = 0x01
    val kModuleType_Digital = 0x02
    val kModuleType_Solenoid = 0x03
  }

  /**
    * Target class from LoadOut.h
    */
  object tTargetClass {
    val kTargetClass_Unknown = 0x00
    val kTargetClass_FRC1 = 0x10
    val kTargetClass_FRC2 = 0x20
    val kTargetClass_FRC3 = 0x30
    val kTargetClass_RoboRIO = 0x40
    val kTargetClass_FRC2_Analog: Int = kTargetClass_FRC2 | tModuleType.kModuleType_Analog
    val kTargetClass_FRC2_Digital: Int = kTargetClass_FRC2 | tModuleType.kModuleType_Digital
    val kTargetClass_FRC2_Solenoid: Int = kTargetClass_FRC2 | tModuleType.kModuleType_Solenoid
    val kTargetClass_FamilyMask = 0xF0
    val kTargetClass_ModuleMask = 0x0F
  }

  /**
    * Resource type from UsageReporting.h
    */
  object tResourceType {
    val kResourceType_Controller = 0
    val kResourceType_Module = 1
    val kResourceType_Language = 2
    val kResourceType_CANPlugin = 3
    val kResourceType_Accelerometer = 4
    val kResourceType_ADXL345 = 5
    val kResourceType_AnalogChannel = 6
    val kResourceType_AnalogTrigger = 7
    val kResourceType_AnalogTriggerOutput = 8
    val kResourceType_CANJaguar = 9
    val kResourceType_Compressor = 10
    val kResourceType_Counter = 11
    val kResourceType_Dashboard = 12
    val kResourceType_DigitalInput = 13
    val kResourceType_DigitalOutput = 14
    val kResourceType_DriverStationCIO = 15
    val kResourceType_DriverStationEIO = 16
    val kResourceType_DriverStationLCD = 17
    val kResourceType_Encoder = 18
    val kResourceType_GearTooth = 19
    val kResourceType_Gyro = 20
    val kResourceType_I2C = 21
    val kResourceType_Framework = 22
    val kResourceType_Jaguar = 23
    val kResourceType_Joystick = 24
    val kResourceType_Kinect = 25
    val kResourceType_KinectStick = 26
    val kResourceType_PIDController = 27
    val kResourceType_Preferences = 28
    val kResourceType_PWM = 29
    val kResourceType_Relay = 30
    val kResourceType_RobotDrive = 31
    val kResourceType_SerialPort = 32
    val kResourceType_Servo = 33
    val kResourceType_Solenoid = 34
    val kResourceType_SPI = 35
    val kResourceType_Task = 36
    val kResourceType_Ultrasonic = 37
    val kResourceType_Victor = 38
    val kResourceType_Button = 39
    val kResourceType_Command = 40
    val kResourceType_AxisCamera = 41
    val kResourceType_PCVideoServer = 42
    val kResourceType_SmartDashboard = 43
    val kResourceType_Talon = 44
    val kResourceType_HiTechnicColorSensor = 45
    val kResourceType_HiTechnicAccel = 46
    val kResourceType_HiTechnicCompass = 47
    val kResourceType_SRF08 = 48
    val kResourceType_AnalogOutput = 49
    val kResourceType_VictorSP = 50
    val kResourceType_TalonSRX = 51
    val kResourceType_CANTalonSRX = 52
    val kResourceType_ADXL362 = 53
    val kResourceType_ADXRS450 = 54
    val kResourceType_RevSPARK = 55
    val kResourceType_MindsensorsSD540 = 56
    val kResourceType_DigitalFilter = 57
  }

  /**
    * Instances from UsageReporting.h
    */
  object tInstances {
    val kLanguage_LabVIEW = 1
    val kLanguage_CPlusPlus = 2
    val kLanguage_Java = 3
    val kLanguage_Python = 4
    val kCANPlugin_BlackJagBridge = 1
    val kCANPlugin_2CAN = 2
    val kFramework_Iterative = 1
    val kFramework_Simple = 2
    val kFramework_CommandControl = 3
    val kRobotDrive_ArcadeStandard = 1
    val kRobotDrive_ArcadeButtonSpin = 2
    val kRobotDrive_ArcadeRatioCurve = 3
    val kRobotDrive_Tank = 4
    val kRobotDrive_MecanumPolar = 5
    val kRobotDrive_MecanumCartesian = 6
    val kDriverStationCIO_Analog = 1
    val kDriverStationCIO_DigitalIn = 2
    val kDriverStationCIO_DigitalOut = 3
    val kDriverStationEIO_Acceleration = 1
    val kDriverStationEIO_AnalogIn = 2
    val kDriverStationEIO_AnalogOut = 3
    val kDriverStationEIO_Button = 4
    val kDriverStationEIO_LED = 5
    val kDriverStationEIO_DigitalIn = 6
    val kDriverStationEIO_DigitalOut = 7
    val kDriverStationEIO_FixedDigitalOut = 8
    val kDriverStationEIO_PWM = 9
    val kDriverStationEIO_Encoder = 10
    val kDriverStationEIO_TouchSlider = 11
    val kADXL345_SPI = 1
    val kADXL345_I2C = 2
    val kCommand_Scheduler = 1
    val kSmartDashboard_Instance = 1
  }
}

