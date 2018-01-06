package com.lynbrookrobotics.scalanativejni

import scala.language.experimental.macros
import scala.reflect.macros.whitebox

case class jnilib(library: String) extends scala.annotation.StaticAnnotation

object JNIMacrosImpl {
  def jniImpl[T: c.WeakTypeTag](c: whitebox.Context): c.Expr[T] = {
    import c.universe._

    val method = c.internal.enclosingOwner.asMethod
    val paramss: List[Symbol] = method.paramLists.flatten

    val enclosingObject = method.owner
    val linkLibrary = c.Expr[String](enclosingObject.annotations.find { annot =>
      annot.tree.tpe.typeSymbol.fullName == "com.lynbrookrobotics.scalanativejni.jnilib"
    }.get.tree.children.last)

    val objectPath = enclosingObject.fullName
    val methodName = method.name.toString

    val jniName = s"Java_${objectPath.replace('.', '_')}_$methodName"

    val jniParams = paramss.map { param =>
      q"val ${param.name.asInstanceOf[TermName]}: ${param.typeSignature}"
    }

    val paramExprs = paramss.map { p =>
      q"${p.asTerm.name}"
    }

    val linkerObject =
      q"""
         @_root_.scala.scalanative.native.extern @_root_.scala.scalanative.native.link(${linkLibrary.tree}) object linker {
           @_root_.scala.scalanative.native.name(${Literal(Constant(jniName))})
           def native(env: _root_.com.lynbrookrobotics.scalanativejni.Env,
                      cls: _root_.com.lynbrookrobotics.scalanativejni.Cls,
                      ..$jniParams): ${method.returnType.typeSymbol} =
             _root_.scala.scalanative.native.extern
         }
       """

    c.Expr[T](
      q"""
         $linkerObject
         linker.native(_root_.com.lynbrookrobotics.scalanativejni.env, _root_.com.lynbrookrobotics.scalanativejni.cls, ..$paramExprs)
       """)
  }
}
