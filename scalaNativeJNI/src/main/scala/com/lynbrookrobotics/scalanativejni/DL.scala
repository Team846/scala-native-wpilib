package com.lynbrookrobotics.scalanativejni

import scala.scalanative.native._

@extern object DL {
  def dlopen(name: CString, flags: Int): Ptr[Byte] = extern
  def dlsym(handle: Ptr[Byte], symbol: CString): Ptr[Byte] = extern
}
