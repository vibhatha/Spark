/**
 * Created by vibhatha on 7/11/17.
 */
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.classification.SVMModel;
import org.apache.spark.mllib.classification.SVMWithSGD;
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.util.MLUtils;


public class ExpSVM {

    public static void main(String [] args){
        long start_time = System.currentTimeMillis();
        System.out.println("Hello Spark");
        SparkConf conf = new SparkConf().setAppName("Simple Application");
        SparkContext sc = new SparkContext(conf);
        String path = "file:/home/vibhatha/ds/data/ijcnn1_train_spark.txt";
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
                        return new Tuple2<Object, Object>(score, p.label());
                    }
                }
        );

// Get evaluation metrics.
        BinaryClassificationMetrics metrics =
                new BinaryClassificationMetrics(JavaRDD.toRDD(scoreAndLabels));
        double auROC = metrics.areaUnderROC();
        System.out.println("Area under ROC = " + auROC);
        long end_time = System.currentTimeMillis();
        long elapsed_time = end_time - start_time;
        System.out.println("Time Elapsed : "+elapsed_time);

// Save and load model
        //model.save(sc, "target/tmp/javaSVMWithSGDModel1");
        //SVMModel sameModel = SVMModel.load(sc, "target/tmp/javaSVMWithSGDModel1");

    }

}
