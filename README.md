# scala-native-wpilib
[![Build Status](https://travis-ci.org/Team846/scala-native-wpilib.svg?branch=master)](https://travis-ci.org/Team846/scala-native-wpilib)

A reimplementation of the FIRST Robotics WPILib libraries in Scala for Scala Native support.

Right now, this implements a core set of APIs from WPILibJ, CTRE Phoenix, and NTCore in Scala by linking into the existing JNI libraries for each. In the future, we hope to complete support and make the libraries 100% match the original API.
