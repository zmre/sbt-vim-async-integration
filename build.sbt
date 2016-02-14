sbtPlugin := true

name := "sbt-vim-async-integration"

organization := "zmre"

version := "1.0-LOCAL"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8", // yes, this is 2 args
  "-feature",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Xfuture",
  "-language:existentials",
  "-language:higherKinds"
)

//libraryDependencies ++= Seq(
  //"org.scalatest" %% "scalatest" % "2.2.6"
//)
