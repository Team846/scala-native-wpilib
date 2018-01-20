package com.lynbrookrobotics.scalanativejni

import scala.scalanative.native._

@extern object DL {
  def dlopen(name: CString, flags: Int): Unit = extern
}
