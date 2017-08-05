package edu.indiana.ise.spidal.svm

/**
  * Created by vibhatha on 7/31/17.
  */

import com.sun.javafx.tk.Toolkit.WritableImageAccessor
import edu.indiana.ise.spidal.util.WriteText
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.classification.{SVMModel, SVMWithSGD}
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.feature.PCA
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.util.MLUtils

object ExpSVM {

  def main(args: Array[String]){


    if(args.length!=4){
      ///home/vibhatha/ds/data/pca/matrix4
      println("Incorrect Argument format")
      println("sh exp.sh <data_file_path> <num_iterations> <output_path> <threads>")
      println("sh exp.sh /home/data/matrix0 100 /home/output/data1 1")
    }else{
      val filename=args(0)
      val iterations = args(1).toInt
      val output = args(2)
      val threads = args(3).toInt

      println("Spark SVM")
      val conf = new SparkConf().setAppName("ExpSVM - Support Vector Machines Benchmarking")
      val sc = new SparkContext(conf)
      //data/mllib/sample_libsvm_data.txt
      val start_time = System.currentTimeMillis();
      val data = MLUtils.loadLibSVMFile(sc, "file:"+filename)

      // Split data into training (60%) and test (40%).
      val splits = data.randomSplit(Array(0.99, 0.01), seed = 11L)
      val training = splits(0).cache()
      val test = splits(1)

      // Run training algorithm to build the model
      val numIterations = iterations
      val model = SVMWithSGD.train(training, numIterations)

      // Clear the default threshold.
      model.clearThreshold()

      // Compute raw scores on the test set.
      val scoreAndLabels = test.map { point =>
        val score = model.predict(point.features)
        (score, point.label)
      }

      // Get evaluation metrics.
      val metrics = new BinaryClassificationMetrics(scoreAndLabels)
      val auROC = metrics.areaUnderROC()

      println("Area under ROC = " + auROC)

      // Save and load model
      //model.save(sc, "file:"+output+"_"+System.currentTimeMillis().toString)
      //val sameModel = SVMModel.load(sc, "file:"+output+"_"+System.currentTimeMillis().toString)
      val end_time = System.currentTimeMillis();
      val elapsed_time = (end_time - start_time)/1000.0
      println("======================================")
      println("Time Elapsed : "+elapsed_time+" s")
      println("======================================")
      val outputData = "Matrix : "+filename+"\n";
      val itr = "Iterations : "+iterations.toString+"\n"
      val thd = "Threads : "+threads.toString+"\n"
      val eta = "Execution Time : "+elapsed_time.toString+"\n"
      val final_record = outputData+itr+thd+eta
      WriteText.saveFile(output, final_record);
    }



  }

}
