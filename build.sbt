name := """adventure"""
organization := "com.maki"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)
scalaVersion := "2.13.3"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += caffeine
libraryDependencies ++= Seq(
  "com.github.julien-truffaut" %% "monocle-core"  % "3.0.0-M3",
  "com.github.julien-truffaut" %% "monocle-macro" % "3.0.0-M3" // Not required for Scala 3
)

libraryDependencies ++= Seq(
  "com.pauldijou" %% "jwt-play" % "4.0.0",
  "com.pauldijou" %% "jwt-core" % "4.0.0",
  "com.auth0"      % "jwks-rsa" % "0.10.0"
)

libraryDependencies += "org.postgresql" % "postgresql" % "9.4-1200-jdbc41"
libraryDependencies ++= Seq(evolutions, jdbc)

libraryDependencies ++= Seq(
  "org.playframework.anorm" %% "anorm" % "2.6.10"
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.maki.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.maki.binders._"
