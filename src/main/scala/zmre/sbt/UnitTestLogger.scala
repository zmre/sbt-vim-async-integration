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
    //println("Clearing test log")
    IO.delete(output)
    IO.touch(List(output))
  }

  def doInit() = clearLog()
  def doComplete(finalResult: TestResult.Value) = {
      notify()
  }

  def startGroup(name: String): Unit = {}

  def testEvent(events: TestEvent): Unit = {
    // TestEvent http://www.scala-sbt.org/0.13/api/index.html#sbt.TestEvent
    // has .result: Option[Value] and .detail[Seq[Event]]
    // Event, I think, is
    // http://grepcode.com/file/repo1.maven.org/maven2/org.scala-sbt/test-interface/1.0/sbt/testing/Event.java?av=f
    // event.throwable is OptionalThrowable, shown here:
    // http://grepcode.com/file/repo1.maven.org/maven2/org.scala-sbt/test-interface/1.0/sbt/testing/OptionalThrowable.java
    // status is an enum with these options: Success, Error, Failure, Skipped,
    // Ignored, Pending, Canceled
    // TODO: add a warning for Pending and Ignored tests?
    for {
      event <- events.detail if event.status == Failure || event.status == Error
      error <- Try(event.throwable.get)
      (file, line) <- findInStackTrace(error.getStackTrace)
    } { append(output, "error", file, line, error.getMessage); notify() }
  }

  def endGroup(name: String, t: Throwable): Unit = {}

  def endGroup(name: String, v: Value): Unit = {}

  def findInStackTrace(trace: Array[StackTraceElement]): Option[(File, Int)] =
    { for {
      elem <- trace
      file <- findSource(elem.getFileName)
    } yield (file, elem.getLineNumber) }.headOption

  def findSource(name: String): Option[File] = srcFiles find { file => file.getName endsWith name }
}

object UnitTestLogger {
  def apply(dir: File, srcFiles: Seq[File], notify: () => Unit): TestReportListener =
    new UnitTestLogger(dir, srcFiles, notify)
}

