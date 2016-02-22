// Code adapted from github.com/dscleaver/sbt-quickfix. Those portions
// Copyright (c) 2013 David Cleaver; All rights reserved.
package zmre.sbt

import sbt._

object CompileLogger {
  def apply(dir: File, notify: () => Unit): CompileLogger = new CompileLogger(dir / "compileissues.log", notify)
  def append(output: File, prefix: String, message: String): Unit =
    IO.append(output, "[%s] %s\n".format(prefix, message))

  def append(output: File, prefix: String, file: File, line: Int, message: String): Unit =
    append(output, prefix, "%s:%d: %s".format(file, line, message))
}

class CompileLogger(val output: File, notify: () => Unit) extends BasicLogger {
  import CompileLogger._
  var numErrors = 0
  var numWarnings = 0

  def clearLog() = {
    //println("Clearing compile logs")
    numErrors = 0
    numWarnings = 0
    IO.delete(output)
    IO.touch(List(output))
    ()
  }
  def log(level: Level.Value, message: => String): Unit = level match {
    case Level.Info => handleInfoMessage(message)
    case Level.Error => handleErrorMessage(message)
    case Level.Warn => handleWarnMessage(message)
    case _ => handleDebugMessage(message)
  }

  def handleDebugMessage(message: String) = {
    if (message.toLowerCase.contains("compilation failed")) {
      //notify()
    } else if (message.toLowerCase.contains("compilation finished")) {
      notify()
    } else if (message.toLowerCase.startsWith("All initially invalidated sources")) {
      clearLog()
    }
  }

  def handleInfoMessage(message: String) = {
    if((message startsWith "Compiling") || (message startsWith "scalastyle using config")) {
      clearLog()
    } else ()
  }

  def handleErrorMessage(message: String) = {
    numErrors = numErrors + 1
    if (message contains "scalastyle) errors exist") notify()
    append(output, "error", message)
  }

  def handleWarnMessage(message: String) = {
    numWarnings = numWarnings + 1
    append(output, "warn", message)
  }

  def control(event: ControlEvent.Value, message: => String): Unit = {
    ()
  }

  def logAll(events: Seq[LogEvent]): Unit = {
    ()
  }

  def success(message: => String): Unit = {
    // Got success message -- should probably clear out the log, but scalastyle gives a bogus success, so we
    // have to count errors and warnings before deciding
    if (numErrors == 0 && numWarnings == 0) {
      clearLog()
    }
    notify()
  }

  def trace(t: => Throwable): Unit = ()

  override def successEnabled = true

}

