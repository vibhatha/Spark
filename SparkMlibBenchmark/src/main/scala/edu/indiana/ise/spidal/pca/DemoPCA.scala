package edu.indiana.ise.spidal.pca

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.feature.PCA
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.{LabeledPoint, LinearRegressionWithSGD}
/**
  * Created by vibhatha on 7/16/17.
  */
object DemoPCA {
  def main(args: Array[String]) {

    println("Spark PCA")
    val conf = new SparkConf().setMaster("local").setAppName("My App")
    val sc = new SparkContext(conf)
    val data = sc.textFile("file:/home/vibhatha/ds/data/pca/matrix2").map { line =>
      val parts = line
      LabeledPoint(1, Vectors.dense(parts.split(' ').map(_.toDouble)))
    }.cache()

    val splits = data.randomSplit(Array(0.8, 0.2), seed = 11L)
    val training = splits(0).cache()
    val test = splits(1)

    val pca = new PCA(training.first().features.size / 2).fit(data.map(_.features))
    val training_pca = training.map(p => p.copy(features = pca.transform(p.features)))
    val test_pca = test.map(p => p.copy(features = pca.transform(p.features)))
    val arr1 : Array[LabeledPoint] = training_pca.collect()
    var a = 0;
    for(a <- 0 to arr1.length-1){
      println(arr1(a));
    }


  }
}
