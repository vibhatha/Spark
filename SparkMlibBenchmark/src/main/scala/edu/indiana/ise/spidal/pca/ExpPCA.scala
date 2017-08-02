package edu.indiana.ise.spidal.pca

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.feature.PCA
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.{LabeledPoint, LinearRegressionWithSGD}
/**
  * Created by vibhatha on 7/16/17.
  */
object ExpPCA {
  def main(args: Array[String]) {

    if(args.length!=1){
      ///home/vibhatha/ds/data/pca/matrix4
      println("Incorrect Argument format")
      println("sh exp.sh /home/data/matrix0")
    }else{
      val filename=args(0)
      println("Spark PCA")
      val conf = new SparkConf().setAppName("My App")
      val sc = new SparkContext(conf)
      val start_time = System.currentTimeMills()	      
	val data = sc.textFile("file:"+filename).map { line =>
        val parts = line.split(',')
        LabeledPoint(parts(0).toDouble, Vectors.dense(parts(1).split(' ').map(_.toDouble)))
      }.cache()

      val splits = data.randomSplit(Array(0.8, 0.2), seed = 11L)
      val training = splits(0).cache()
      val test = splits(1)
      //val start_time = System.currentTimeMillis();
      val pca = new PCA(training.first().features.size / 2).fit(data.map(_.features))
      val training_pca = training.map(p => p.copy(features = pca.transform(p.features)))
      val end_time = System.currentTimeMillis();
      val elapsed_time = (end_time - start_time)/1000.0
      val test_pca = test.map(p => p.copy(features = pca.transform(p.features)))
      val arr1 : Array[LabeledPoint] = training_pca.collect()
      var a = 0
      //for(a <- 0 to arr1.length-1){
      //  println(arr1(a));
      //}
      println("======================================")
      println("Time Elapsed : "+elapsed_time+" s")
      println("======================================")
    }

  }
}
