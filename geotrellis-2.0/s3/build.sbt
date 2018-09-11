import Dependencies._

name := "geotrellis-s3"

libraryDependencies ++= Seq(
  sparkCore % Provided,
  awsSdkS3 % Provided,
  spire,
  scaffeine,
  scalatest % Test,
  hadoopAws % Provided exclude("com.amazonaws", "aws-java-sdk-s3")
)

fork in Test := false
parallelExecution in Test := false

mimaPreviousArtifacts := Set(
  "org.locationtech.geotrellis" %% "geotrellis-s3" % Version.previousVersion
)

initialCommands in console :=
  """
  import geotrellis.raster._
  import geotrellis.vector._
  import geotrellis.proj4._
  import geotrellis.spark._
  import geotrellis.spark.util._
  import geotrellis.spark.tiling._
  """
