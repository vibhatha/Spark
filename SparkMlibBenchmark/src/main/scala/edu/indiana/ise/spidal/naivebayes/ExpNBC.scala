package edu.indiana.ise.spidal.naivebayes

/**
  * Created by vibhatha on 8/2/17.
  */
import edu.indiana.ise.spidal.util.WriteText
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.mllib.util.MLUtils


object ExpNBC {

  def main(args: Array[String]){

    if(args.length!=5){

      println("Invalid Argument Format")
      println("<data-set-path> <lambda> <modelType> <threads> <outputPath>")
      println("sample_libsvm.txt 1 bernoulli 1 sample_output.txt")
      println("sample_libsvm.txt 1 multinomial 1 sample_output.txt")

    }else{

      val filename = args(0)
      val lambda = args(1).toInt
      val modelType = args(2) //"multinomial" or "bernoulli"
      val threads = args(3).toInt
      val output = args(4)

      println("Spark Naive Bayes Classification")
      val conf = new SparkConf().setAppName("ExpNBC - Naive Bayes Classification")
      val sc = new SparkContext(conf)
      val start_time = System.currentTimeMillis()
      val data = MLUtils.loadLibSVMFile(sc, "file:"+filename)// libsvm data format

      // Split data into training (99%) and test (1%).
      val Array(training, test) = data.randomSplit(Array(0.99, 0.01))

      val model = NaiveBayes.train(training, lambda = 1.0, modelType = modelType)

      val predictionAndLabel = test.map(p => (model.predict(p.features), p.label))
      val accuracy = 1.0 * predictionAndLabel.filter(x => x._1 == x._2).count() / test.count()
      val end_time = System.currentTimeMillis();
      val elapsed_time = (end_time - start_time)/1000.0
      println("======================================")
      println("Time Elapsed : "+elapsed_time+" s")
      println("======================================")
      val outputData = "Matrix : "+filename+"\n"
      val itr = "Lambda : "+lambda.toString+"\n"
      val modeltype = "Model Type : "+modelType+"\n"
      val thd = "Threads : "+threads.toString+"\n"
      val eta = "Execution Time : "+elapsed_time.toString+"\n"
      val final_record = outputData+itr+thd+modeltype+eta
      WriteText.saveFile(output, final_record);
    }

  }

}
