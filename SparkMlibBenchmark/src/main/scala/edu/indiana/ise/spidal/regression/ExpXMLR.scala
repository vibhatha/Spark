package edu.indiana.ise.spidal.regression

import scala.language.reflectiveCalls
import scopt.OptionParser
import edu.indiana.ise.spidal.util.{AbstractParams}
import org.apache.spark.ml.regression.LinearRegression
import org.apache.spark.ml.linalg.Vector
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  * Created by vibhatha on 8/3/17.
  */
object ExpXMLR {



    def main(args: Array[String]) {
    run()
    }

    def run() {
      val spark = SparkSession
        .builder
        .appName(s"LinearRegressionExample with ")
        .getOrCreate()

      println(s"LinearRegressionExample with parameters:\n")

      // Load training and test data and cache it.
      val (training: DataFrame, test: DataFrame) = loadDatasets("file:/home/vibhatha/ds/spark/spark-2.0.0-preview-bin-hadoop2.7/data/mllib/sample_linear_regression_data.txt",
        "libsvm", "", "regression", 0.2)


      val lir = new LinearRegression()
        .setFeaturesCol("features")
        .setLabelCol("label")
        .setRegParam(0.01)
        .setElasticNetParam(1)
        .setMaxIter(100)
        .setTol(0.01)

      // Train the model
      val startTime = System.nanoTime()
      val lirModel = lir.fit(training)
      val elapsedTime = (System.nanoTime() - startTime) / 1e9
      println(s"Training time: $elapsedTime seconds")

      // Print the weights and intercept for linear regression.
      println(s"Weights: ${lirModel.coefficients} Intercept: ${lirModel.intercept}")

      println("Training data results:")
      //SparkDataUtil.evaluateRegressionModel(lirModel, training, "label")
      //println("Test data results:")
      //SparkDataUtil.evaluateRegressionModel(lirModel, test, "label")

      spark.stop()




    }

  private def loadData(
                            spark: SparkSession,
                            path: String,
                            format: String,
                            expectedNumFeatures: Option[Int] = None): DataFrame = {
    import spark.implicits._

    format match {
      case "dense" => MLUtils.loadLabeledPoints(spark.sparkContext, path).toDF()
      case "libsvm" => expectedNumFeatures match {
        case Some(numFeatures) => spark.read.option("numFeatures", numFeatures.toString)
          .format("libsvm").load(path)
        case None => spark.read.format("libsvm").load(path)
      }
      case _ => throw new IllegalArgumentException(s"Bad data format: $format")
    }
  }


   def loadDatasets(
                                input: String,
                                dataFormat: String,
                                testInput: String,
                                algo: String,
                                fracTest: Double): (DataFrame, DataFrame) = {
    val spark = SparkSession
      .builder
      .getOrCreate()

    // Load training data
    val origExamples: DataFrame = loadData(spark, input, dataFormat)

    // Load or create test set
    val dataframes: Array[DataFrame] = if (testInput != "") {
      // Load testInput.
      val numFeatures = origExamples.first().getAs[Vector](1).size
      val origTestExamples: DataFrame =
        loadData(spark, testInput, dataFormat, Some(numFeatures))
      Array(origExamples, origTestExamples)
    } else {
      // Split input into training, test.
      origExamples.randomSplit(Array(1.0 - fracTest, fracTest), seed = 12345)
    }

    val training = dataframes(0).cache()
    val test = dataframes(1).cache()

    val numTraining = training.count()
    val numTest = test.count()
    val numFeatures = training.select("features").first().getAs[Vector](0).size
    println("Loaded data:")
    println(s"  numTraining = $numTraining, numTest = $numTest")
    println(s"  numFeatures = $numFeatures")

    (training, test)
  }



}
