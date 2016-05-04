package it.codegen.spark.my_app;

import scala.Tuple2;
import scala.reflect.ClassTag;

import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.classification.*;
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics;

import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.util.MLUtils;
import org.apache.spark.rdd.RDD;
import org.apache.spark.util.Vector;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;

public class SVMClassifier {
  public static void main(String[] args) {
    SparkConf conf = new SparkConf().setAppName("SVM Classifier Example");
    conf.setMaster("local");
    SparkContext sc = new SparkContext(conf);
    
    String path = "G:/Spark/spark/data/mllib/sample_libsvm_data.txt";
    JavaRDD<LabeledPoint> data = MLUtils.loadLibSVMFile(sc, path).toJavaRDD();

    // Split initial RDD into two... [60% training data, 40% testing data].
    JavaRDD<LabeledPoint> training = data.sample(false, 0.6, 11L);
    training.cache();
    JavaRDD<LabeledPoint> test = data.subtract(training);

    // Run training algorithm to build the model.
    int numIterations = 100;
    final SVMModel model = SVMWithSGD.train(training.rdd(), numIterations);
   

    // Clear the default threshold.
    model.clearThreshold();

    // Compute raw scores on the test set.
    JavaRDD<Tuple2<Object, Object>> scoreAndLabels = test.map(
      new Function<LabeledPoint, Tuple2<Object, Object>>() {
        public Tuple2<Object, Object> call(LabeledPoint p) {
          Double score = model.predict(p.features());
          
         
          
          System.out.println("Prediction "+score);
          return new Tuple2<Object, Object>(score, p.label());
        }
      }
    );
     
    

    // Get evaluation metrics.
    BinaryClassificationMetrics metrics =
      new BinaryClassificationMetrics(JavaRDD.toRDD(scoreAndLabels));
    double auROC = metrics.areaUnderROC();

    System.out.println("Area under ROC = " + auROC);

    // Save and load model
    model.save(sc, "myModelPath6");
   
    SVMModel sameModel = SVMModel.load(sc, "myModelPath6");
    System.out.println(" Prediction "+metrics.scoreAndLabels());
    
    long size = metrics.scoreAndLabels().count();
    metrics.scoreAndLabels().saveAsTextFile("G:\\Spark\\outputs\\data1.txt");
    
    
    
    
  }
}
