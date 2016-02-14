// Code adapted from github.com/dscleaver/sbt-quickfix. Those portions
// Copyright (c) 2013 David Cleaver; All rights reserved.
package zmre.sbt

import sbt._
import sbt.TestResult.Value
import sbt.testing.Status._
import sbt.testing.Event
import scala.util.Try
//import org.scalatest.exceptions._

class UnitTestLogger(dir: File, srcFiles: => Seq[File], notify: () => Unit) extends TestsListener {
  import CompileLogger._

  val output = dir / "testissues.log"

  def clearLog() = {
    IO.delete(output)
    IO.touch(List(output))
  }

  def doInit() = clearLog()
  def doComplete(finalResult: TestResult.Value) = ()

  def startGroup(name: String): Unit = {}

  def testEvent(event: TestEvent): Unit = {
    writeFailure(event)
    if (event.detail.exists(e => e.status == Failure)) {
      notify()
    }
  }

  def endGroup(name: String, t: Throwable): Unit = {}

  def endGroup(name: String, v: Value): Unit = {}

  def writeFailure(event: TestEvent): Unit =
    for {
      detail <- event.detail
      if writeable(detail)
      (file, line) <- findInStackTrace(detail.throwable.get.getStackTrace)
    } append(output, "error", file, line, detail.throwable.get.getMessage)

  def writeable(detail: Event): Boolean =
    detail.status == Failure && detail.throwable.isDefined

  def findInStackTrace(trace: Array[StackTraceElement]): Option[(File, Int)] =
    { for {
      elem <- trace
      file <- findSource(elem.getFileName)
    } yield (file, elem.getLineNumber) }.headOption

  def findSource(name: String): Option[File] =
    srcFiles find { file => file.getName endsWith name }
}

object UnitTestLogger {
  def apply(dir: File, srcFiles: Seq[File], notify: () => Unit): TestReportListener =
    new UnitTestLogger(dir, srcFiles, notify)
}

