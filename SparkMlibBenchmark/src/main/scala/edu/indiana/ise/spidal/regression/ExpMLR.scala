package edu.indiana.ise.spidal.regression

/**
  * Created by vibhatha on 8/5/17.
  */

import edu.indiana.ise.spidal.util.WriteText
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.regression.LinearRegressionModel
import org.apache.spark.mllib.regression.LinearRegressionWithSGD

object ExpMLR {

  def main(args: Array[String]){

    if(args.length!=5){
      println("Wrong argument format")
      println("<filename> <iterations> <stepSize> <threads> <output>")
    }else{
      val (filename, iterations, stepSize, threads, output) = parser(args)
      run(filename, iterations, stepSize, threads, output)

    }

  }

  def parser(params : Array[String]): (String, Int, Double, Int, String) = (params(0), params(1).toInt, params(2).toDouble, params(3).toInt, params(4))



  def run(filename : String, iterations : Int, stepSize : Double, threads : Int, output : String){

    val conf = new SparkConf().setAppName("ExpSVM - Support Vector Machines Benchmarking")
    val sc = new SparkContext(conf)
    //data/mllib/sample_libsvm_data.txt
    val start_time = System.currentTimeMillis();
    // Load and parse the data
    val data = sc.textFile("file:"+filename)
    val parsedData = data.map { line =>
      val parts = line.split(',')
      LabeledPoint(parts(0).toDouble, Vectors.dense(parts(1).split(' ').map(_.toDouble)))
    }.cache()

    // Building the model
    val numIterations = iterations
    val step = stepSize//0.00000001
    val model = LinearRegressionWithSGD.train(parsedData, numIterations, step)
    val end_time = System.currentTimeMillis()

    // Evaluate model on training examples and compute training error
    val valuesAndPreds = parsedData.map { point =>
      val prediction = model.predict(point.features)
      (point.label, prediction)
    }
    val MSE = valuesAndPreds.map{ case(v, p) => math.pow((v - p), 2) }.mean()
    println("training Mean Squared Error = " + MSE)


    val elapsed_time = (end_time - start_time)/1000.0
    println("======================================")
    println("Time Elapsed : "+elapsed_time+" s")
    println("======================================")
    val outputData = "Matrix : "+filename+"\n"
    val itr = "Iterations : "+iterations.toString+"\n"
    val thd = "Threads : "+threads.toString+"\n"
    val eta = "Execution Time : "+elapsed_time.toString+"\n"
    val final_record = outputData+itr+thd+eta
    WriteText.saveFile(output, final_record);
    // Save and load model
    //model.save(sc, "target/tmp/scalaLinearRegressionWithSGDModel")
    //val sameModel = LinearRegressionModel.load(sc, "target/tmp/scalaLinearRegressionWithSGDModel")

  }

}
