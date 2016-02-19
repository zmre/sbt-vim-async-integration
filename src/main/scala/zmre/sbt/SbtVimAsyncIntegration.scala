// Code adapted from github.com/dscleaver/sbt-quickfix. Those portions
// Copyright (c) 2013 David Cleaver; All rights reserved.
package zmre.sbt

import sbt._
import Keys._

// scalastyle:off
object SbtVimAsyncIntegrationPlugin extends AutoPlugin {

  object autoImport {
    // val quickFixDirectory = target in config("vim")
    val vimIntegrationLogDirectory = SettingKey[File]("vim-integration-logdir", "The folder where temporary log files will be written")
    val vimIntegrationExecutable = SettingKey[String]("vim-integration-executable", "The path to the vim executable")
    val notifyVim = taskKey[Unit]("Notify vim on certain task completions")
  }
  import autoImport._

  override def trigger = allRequirements


  override val projectSettings = Seq(
    notifyVim <<= Def.task {
        SbtVimAsyncIntegration.notifyToRefresh(vimIntegrationExecutable.value)()
    }.triggeredBy(compile in Compile).triggeredBy(test in Test),
    vimIntegrationLogDirectory in ThisBuild := baseDirectory.value / "target" / "vim",
    extraLoggers <<= (extraLoggers, vimIntegrationLogDirectory, vimIntegrationExecutable) apply { (currentFunction, logdir, vimExec) =>
      (key: ScopedKey[_]) => {
        val loggers = currentFunction(key) // current list of loggers
        val taskOption = key.scope.task.toOption

        if (taskOption.exists(l => l.label.startsWith("compile") || l.label == "scalastyle")) {
          // prepend our logger to the list
          CompileLogger(logdir, SbtVimAsyncIntegration.notifyToRefresh(vimExec)) +: loggers
        } else {
          loggers
        }
      }
    },
    testListeners += UnitTestLogger(vimIntegrationLogDirectory.value, (sources in Test).value, SbtVimAsyncIntegration.notifyToRefresh(vimIntegrationExecutable.value)),
    // testListeners <+= (sources in Test) map { (testSources) =>
      // UnitTestLogger(vimIntegrationLogDirectory, testSources)
    // },
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
