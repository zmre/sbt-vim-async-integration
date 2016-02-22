// Code adapted from github.com/dscleaver/sbt-quickfix. Those portions
// Copyright (c) 2013 David Cleaver; All rights reserved.
package zmre.sbt

import sbt._
import Keys._

// scalastyle:off
object SbtVimAsyncIntegrationPlugin extends AutoPlugin {

  object autoImport {
    val vimIntegrationLogDirectory = SettingKey[File]("vim-integration-logdir", "The folder where temporary log files will be written")
    val vimIntegrationExecutable = SettingKey[String]("vim-integration-executable", "The path to the vim executable")
    val notifyVim = taskKey[Unit]("Notify vim on certain task completions")
    val startCompile = taskKey[Unit]("Add a log message indicating start of compilation")
    val finishCompile = taskKey[Unit]("Add a log message indicating end of compilation")
  }
  import autoImport._

  override def trigger = allRequirements


  override val projectSettings = Seq(
    finishCompile <<= Def.task {
      streams.value.log.debug("compilation finished.")
    }.triggeredBy(compile in (Compile, compile)),
    startCompile <<= Def.task {
      streams.value.log.info("Compiling...")
    }.runBefore(compile in (Compile, compile)),
    vimIntegrationLogDirectory in ThisBuild := baseDirectory.value / "target" / "vim",
    extraLoggers <<= (extraLoggers, vimIntegrationLogDirectory, vimIntegrationExecutable) apply { (currentFunction, logdir, vimExec) =>
      (key: ScopedKey[_]) => {
        val loggers = currentFunction(key) // current list of loggers
        val taskOption = key.scope.task.toOption

        if (taskOption.exists(l => l.label.toLowerCase.contains("compile") || l.label == "scalastyle")) {
          // prepend our logger to the list
          //println(s"Adding custom logger for $taskOption")
          CompileLogger(logdir, SbtVimAsyncIntegration.notifyToRefresh(vimExec)) +: loggers
        } else {
          loggers
        }
      }
    },
    testListeners += UnitTestLogger(vimIntegrationLogDirectory.value, (sources in Test).value, SbtVimAsyncIntegration.notifyToRefresh(vimIntegrationExecutable.value)),
    vimIntegrationExecutable in ThisBuild := (if (System.getProperty("os.name").startsWith("Win")) "gvim.bat" else "gvim")
  )

}
object SbtVimAsyncIntegration {
  def notifyToRefresh(vimExec: String): () => Unit = () => {
    println("Sending update notice to vim")
    vimCall(vimExec, ":SyntasticCheck readsbtlogs")
    ()
  }
  def vimCall(vimExec: String, command: Seq[String]): Int = Process(List(vimExec, "--remote-send") ++ command).!

  def vimCall(vimExec: String, command: String): Int =
    vimCall(vimExec, List(s"<c-\\><c-n>$command<cr>"))
}
