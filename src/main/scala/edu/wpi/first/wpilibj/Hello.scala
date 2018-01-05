package edu.wpi.first.wpilibj

object Hello extends IterativeRobot {
  lazy val pwm = new Servo(0)

  override def teleopPeriodic(): Unit = {
    pwm.setPosition(math.abs(math.sin(Timer.getFPGATimestamp)))
  }

  override def main(args: Array[String]): Unit = {
    super.main(args)
  }
}
