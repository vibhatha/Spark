package edu.indiana.ise.spidal.kmeans

/**
  * Created by vibhatha on 7/18/17.
  */
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.clustering.{KMeans, KMeansModel}
import org.apache.spark.mllib.linalg.Vectors

object ExpKMeans {
  def main(args: Array[String]){

    if(args.length!=4){
      ///home/vibhatha/ds/data/pca/matrix4
      println(args.length)
      println("Incorrect Argument format")
      println("sh exp-kmeans.sh <path-to-data> <num-clusters> <num-iterations> <output-info>")
      println("example : sh exp-kmeans.sh matrix0 3 10 matrix0")
    }else {

      val dataset = args(0)
      val num_clusters = args(1).toInt
      val iterations = args(2).toInt
      val output = args(3)
      println("Dataset : "+dataset +", Clusters : "+num_clusters+", Iterations : "+iterations+ ", Output : "+output)

      val conf = new SparkConf().setMaster("local").setAppName("Spark Mlib Benchmarking")
      val sc = new SparkContext(conf)
      val data = sc.textFile("file:"+dataset)
      val parsedData = data.map(s => Vectors.dense(s.split(' ').map(_.toDouble))).cache()

      // Cluster the data into two classes using KMeans
      val numClusters = num_clusters
      val numIterations = iterations
      val start_time = System.currentTimeMillis();
      val clusters = KMeans.train(parsedData, numClusters, numIterations)
      val end_time = System.currentTimeMillis();
      val elapsed_time = (end_time - start_time)/1000.0
      // Evaluate clustering by computing Within Set Sum of Squared Errors
      val WSSSE = clusters.computeCost(parsedData)
      println("Within Set Sum of Squared Errors = " + WSSSE)

      println("======================================")
      println("Time Elapsed : "+elapsed_time+" s")
      println("======================================")
      // Save and load model
      //clusters.save(sc, "target/org/apache/spark/KMeansExample/KMeansModel")
      //val sameModel = KMeansModel.load(sc, "target/org/apache/spark/KMeansExample/KMeansModel")

    }

  }
}
