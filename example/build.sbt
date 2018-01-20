enablePlugins(ScalaNativePlugin)

nativeMode := "debug"

nativeGC := "boehm"

val boehmFolder = file("/Users/shadaj/cross-compile/bdwgc")
val libunwindFolder = file("/Users/shadaj/cross-compile/libunwind-1.2.1")
val librtFolder = file("/Users/shadaj/cross-compile/re2")

import scala.scalanative.sbtplugin.ScalaNativePluginInternal._
import scala.scalanative.sbtplugin.Utilities._

scalacOptions ++= Seq("-target:jvm-1.8")

val crossCompileSettings = if (true) {
  Seq(
    // fork to link with gcc instead of clang
    nativeLinkLL in Compile := {
      val linked      = (nativeLinkNIR in Compile).value
      val logger      = streams.value.log
      val apppaths    = (nativeCompileLL in Compile).value
      val nativelib   = (nativeCompileLib in Compile).value
      val cwd         = (nativeWorkdir in Compile).value
      val target      = nativeTarget.value
      val gc          = nativeGC.value
      val linkingOpts = nativeLinkingOptions.value
      val clangpp     = file("/usr/local/bin/arm-frc-linux-gnueabi-gcc")//nativeClangPP.value
      val outpath     = (artifactPath in nativeLink in Compile).value

      val links = {
        val os   = target.split("-")(2)//Option(sys props "os.name").getOrElse("")
        val arch = target.split("-").head
        // we need re2 to link the re2 c wrapper (cre2.h)
        val librt = Seq.empty // we want to statically link librt
        val libunwind = Seq.empty //Seq("unwind", "unwind-" + arch) we want to statically link libunwind

        librt ++ libunwind ++ linked.links
          .map(_.name)// ++ garbageCollector(gc).links
      }

      val linkopts  = links.map("-l" + _) ++ linkingOpts
      val targetopt = Seq("-target", target)
      val flags     = Seq("-o", outpath.abs) ++ linkopts// ++ targetopt
      // statically link libunwind
      val opaths    = ((nativelib ** "*.o").get.map(_.abs) ++
        (file("custom-c") ** "*.o").get.map(_.abs)) :+
        (libunwindFolder / "lib" / "libunwind.a").abs :+
        (libunwindFolder / "lib" / "libunwind-arm.a").abs :+
        (librtFolder / "lib" / "libre2.a").abs :+
        (boehmFolder / "gc.a").abs

      val paths     = apppaths.map(_.abs) ++ opaths
      val compile   = clangpp.abs +: (flags ++ paths)

      logger.time("Linking native code") {
        logger.running(compile)
        Process(compile, cwd) ! logger
      }

      outpath
    },
    nativeLink in Test := (nativeLink in NativeTest).value,
    nativeTarget := "arm-frc-linux-gnueabi",
    nativeCompileOptions ++= Seq(
      "-funwind-tables", "-target", "armv7a-frc-linux-gnueabi",
      "-mfpu=neon", "-mfloat-abi=soft",
      "--sysroot=/usr/local/arm-frc-linux-gnueabi",
      s"-I${(libunwindFolder / "include").abs}", s"-I${(librtFolder / "include").abs}", s"-I${(boehmFolder / "include").abs}",
      "-I/usr/local/arm-frc-linux-gnueabi/include/c++/5.5.0", "-I/usr/local/arm-frc-linux-gnueabi/include/c++/5.5.0/arm-frc-linux-gnueabi",
      "-I/Users/shadaj/external-dev/allwpilib/wpilibj/src/arm-linux-jni",
      "-I/Users/shadaj/external-dev/allwpilib/wpilibj/src/arm-linux-jni/linux"
    ),
    nativeLinkingOptions ++= Seq(
      "-lm", "-lc", "-lstdc++", "-lpthread", // system stuff,
      // transitive dependencies
      "-lwpiHal", "-lwpiutil", "-l:libniriosession.so.17.0.0", "-l:libniriodevenum.so.17.0.0",
      "-l:libRoboRIO_FRC_ChipObject.so.18.0.0", "-l:libvisa.so", "-l:libFRC_NetworkCommunication.so.18.0.0",
      "-l:libNiFpga.so.17.0.0", "-l:libNiFpgaLv.so.17.0.0", "-l:libNiRioSrv.so.17.0.0",

      "-L/Users/shadaj/wpilib/common/current/lib/linux/athena/shared",
      "-L/Users/shadaj/wpilib/user/java/lib",
      "-L/Users/shadaj/wpilib/cpp/current/reflib/linux/athena/shared"
    )
  )
} else Seq.empty

crossCompileSettings

// after copying, run:
// rm -f FRCUserProgram; mv scala-native FRCUserProgram; . /etc/profile.d/natinst-path.sh; chown lvuser FRCUserProgram; setcap 'cap_sys_nice=pe' FRCUserProgram; chmod a+x FRCUserProgram; /usr/local/frc/bin/frcKillRobot.sh -t -r