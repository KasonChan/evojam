name := "evojam"

version := "1.0"

lazy val `evojam` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.5"

scalacOptions ++= Seq("-feature")

libraryDependencies ++= Seq(filters,
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.5.0.akka23")

unmanagedResourceDirectories in Test <+= baseDirectory(_ / "target/web/public/test")