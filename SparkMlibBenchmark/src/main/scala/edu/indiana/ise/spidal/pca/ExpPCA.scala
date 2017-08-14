package edu.indiana.ise.spidal.pca

import edu.indiana.ise.spidal.util.WriteText
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.feature.PCA
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.{LabeledPoint, LinearRegressionWithSGD}
/**
  * Created by vibhatha on 7/16/17.
  */
object ExpPCA {
  def main(args: Array[String]) {

    if(args.length!=3){
      ///home/vibhatha/ds/data/pca/matrix4
      println("Incorrect Argument format")
      println("sh exp.sh /home/data/matrix0 /home/output/matrix1")
    }else{
      val filename=args(0)
      val threads = args(1).toInt
      val output = args(2)
      println("Spark PCA")
      val conf = new SparkConf().setAppName("My App")
      val sc = new SparkContext(conf)
      val start_data_loading = System.currentTimeMillis()
      val load_data = sc.textFile("file:"+filename).count()
      val end_data_loading = System.currentTimeMillis()
      val data_loading_time = end_data_loading - start_data_loading

      val data = sc.textFile("file:"+filename).map { line =>
        val parts = line.split(',')
        LabeledPoint(parts(0).toDouble, Vectors.dense(parts(1).split(' ').map(_.toDouble)))
      }.cache()



      val splits = data.randomSplit(Array(0.99, 0.01), seed = 11L)
      val training = splits(0).cache()
      val test = splits(1)
      val start_time = System.currentTimeMillis();
      val principalComponents=training.first().features.size / 2
      val pca = new PCA(principalComponents).fit(data.map(_.features))
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
      println("Data Loading Time : "+data_loading_time)
      println("Training Time : "+elapsed_time+" s")
      println("======================================")

      val outputData = "Matrix : "+filename+"\n"
      val itr = "Principal Components : "+principalComponents.toString+"\n"
      val thd = "Threads : "+threads.toString+"\n"
      val eta = "Execution Time : "+elapsed_time.toString+"\n"
      val final_record = outputData+itr+thd+eta
      WriteText.saveFile(output, final_record);

    }

  }
}
