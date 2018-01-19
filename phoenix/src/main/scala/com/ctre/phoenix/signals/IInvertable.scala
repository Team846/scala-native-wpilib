package com.ctre.phoenix.signals

trait IInvertable {
  def setInverted(invert: Boolean): Unit

  def getInverted: Boolean
}