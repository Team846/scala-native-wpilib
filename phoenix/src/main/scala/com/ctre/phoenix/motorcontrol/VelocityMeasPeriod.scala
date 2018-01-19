package com.ctre.phoenix.motorcontrol

final class VelocityMeasPeriod(val value: Int)
object VelocityMeasPeriod {
  val Period_1Ms: VelocityMeasPeriod = new VelocityMeasPeriod(1)

  val Period_2Ms: VelocityMeasPeriod = new VelocityMeasPeriod(2)

  val Period_5Ms: VelocityMeasPeriod = new VelocityMeasPeriod(5)

  val Period_10Ms: VelocityMeasPeriod = new VelocityMeasPeriod(10)

  val Period_20Ms: VelocityMeasPeriod = new VelocityMeasPeriod(20)

  val Period_25Ms: VelocityMeasPeriod = new VelocityMeasPeriod(25)

  val Period_50Ms: VelocityMeasPeriod = new VelocityMeasPeriod(50)

  val Period_100Ms: VelocityMeasPeriod = new VelocityMeasPeriod(100)
}
