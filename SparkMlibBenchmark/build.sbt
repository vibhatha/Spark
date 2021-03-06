name := "SparkMlibBenchmark"

version := "1.0"

scalaVersion := "2.11.4"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.0.0" ,
  "org.apache.spark" %% "spark-mllib" % "2.0.0",
  // https://mvnrepository.com/artifact/com.github.scopt/scopt_2.11
  "com.github.scopt" % "scopt_2.11" % "3.3.0"


)
