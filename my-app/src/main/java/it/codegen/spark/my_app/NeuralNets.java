package it.codegen.spark.my_app;
import scala.Tuple2;

import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.regression.LinearRegressionModel;
import org.apache.spark.mllib.regression.LinearRegressionWithSGD;

import java.util.List;

import org.apache.spark.SparkConf;


public class NeuralNets {
	
	public static void main(String[] args) throws Exception {
	    SparkConf conf = new SparkConf().setMaster("local").setAppName("Linear Regression Example");
	    JavaSparkContext sc = new JavaSparkContext(conf);
	    
	    // Load and parse the data
	    String path = "G:\\ML\\Data\\lpsa.data.txt";
	    JavaRDD<String> data = sc.textFile(path);
	    
	   
	    
	    long count = data.count();
	    System.out.println("Size :"+data.count());
	    JavaRDD<LabeledPoint> parsedData = data.map(
	      new Function<String, LabeledPoint>() {
	        public LabeledPoint call(String line) {
	          String[] parts = line.split(",");
	          String[] features = parts[1].split(" ");
	         
	          double[] v = new double[features.length];
	          for (int i = 0; i < features.length - 1; i++)
	            v[i] = Double.parseDouble(features[i]);
	          return new LabeledPoint(Double.parseDouble(parts[0]), Vectors.dense(v));
	        }
	      }
	    );
	    parsedData.cache();

	    // Building the model
	    int numIterations = 100;
	    final LinearRegressionModel model = 
	      LinearRegressionWithSGD.train(JavaRDD.toRDD(parsedData), numIterations);

	    // Evaluate model on training examples and compute training error
	    JavaRDD<Tuple2<Double, Double>> valuesAndPreds = parsedData.map(
	      new Function<LabeledPoint, Tuple2<Double, Double>>() {
	        public Tuple2<Double, Double> call(LabeledPoint point) {
	          double prediction = model.predict(point.features());
	          return new Tuple2<Double, Double>(prediction, point.label());
	        }
	      }
	    );
	    double MSE = new JavaDoubleRDD(valuesAndPreds.map(
	      new Function<Tuple2<Double, Double>, Object>() {
	        public Object call(Tuple2<Double, Double> pair) {
	          return Math.pow(pair._1() - pair._2(), 2.0);
	        }
	      }
	    ).rdd()).mean();
	    System.out.println("training Mean Squared Error = " + MSE);
	  }

}
