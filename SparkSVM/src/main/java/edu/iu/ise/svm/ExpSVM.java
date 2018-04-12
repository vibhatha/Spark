package edu.iu.ise.svm; /**
 * Created by vibhatha on 7/11/17.
 */

import edu.iu.ise.svm.util.Util;
import org.apache.commons.io.FileUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.classification.SVMModel;
import org.apache.spark.mllib.classification.SVMWithSGD;
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.util.MLUtils;
import scala.Tuple2;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import java.io.IOException;
import java.util.*;


public class ExpSVM {

    private static final Logger log = Logger.getLogger(ExpSVM.class.getName());
    private String[] args = null;
    private static Options options = new Options();
    private static String LOG_PATH = "logs";
    private static String LOG_FILE = "log.txt";
    private static String LOG_DEST = "";

    public static void main(String [] args) throws IOException {
        long start_time = System.currentTimeMillis();
        System.out.println("Hello Spark");
        SparkConf conf = new SparkConf().setAppName("Simple Application");
        SparkContext sc = new SparkContext(conf);

        init(args);
        CommandLine cmd = parse(args);
        String trainingDataSet = cmd.getOptionValue("train");
        String testingDataSet = cmd.getOptionValue("test");
        int numIterations = Integer.parseInt(cmd.getOptionValue("iterations"));
        double stepSize = Double.parseDouble(cmd.getOptionValue("stepSize"));
        double regParam = Double.parseDouble(cmd.getOptionValue("regParam"));

        if((cmd.getOptionValue("log"))!=null){
            LOG_DEST = cmd.getOptionValue("log");
        }else{
            Util.mkdir(LOG_PATH);
            LOG_DEST = LOG_PATH+"/"+LOG_FILE;
        }

        Util.appendLogs(LOG_DEST,"===============================================================================");
        Util.appendLogs(LOG_DEST,"Experiment Started :"+ new Date().toString());
        Util.appendLogs(LOG_DEST,"Training File: " + cmd.getOptionValue("train") );
        Util.appendLogs(LOG_DEST,"Iterations: " + cmd.getOptionValue("iterations") );
        Util.appendLogs(LOG_DEST,"Step Size: " + cmd.getOptionValue("stepSize") );
        Util.appendLogs(LOG_DEST,"Regularization Parameter: " + cmd.getOptionValue("regParam") );

        if((cmd.getOptionValue("split"))!=null){
            Util.appendLogs(LOG_DEST,"Splitting Ratio: " + cmd.getOptionValue("split") );
            double splitRatio = Double.parseDouble(cmd.getOptionValue("split"));
            System.out.println("Split Ratio: " + splitRatio);
            ArrayList<JavaRDD<LabeledPoint>> dataList = dataSplit(trainingDataSet, sc, splitRatio);
            JavaRDD<LabeledPoint> training = dataList.get(0);
            JavaRDD<LabeledPoint> testing = dataList.get(1);
            task(sc, training, testing, numIterations, stepSize, regParam);
        }else{
            Util.appendLogs(LOG_DEST,"Testing File: " + cmd.getOptionValue("test") );
            task(sc, trainingDataSet, testingDataSet, numIterations, stepSize, regParam);
        }

        Util.appendLogs(LOG_DEST,"===============================================================================");
    }

    public static void task(SparkContext sc, String trainingDataSet, String testingDataSet) throws IOException {
        String datasource = "ijcnn1";
        String path = "file:"+trainingDataSet; //"file:/home/vibhatha/data/sparksvm/ijcnn1/ijcnn1_train_spark.txt";
        String test_path = "file:"+testingDataSet;
        JavaRDD<LabeledPoint> data = MLUtils.loadLibSVMFile(sc, path).toJavaRDD();
        JavaRDD<LabeledPoint> testdata = MLUtils.loadLibSVMFile(sc, test_path).toJavaRDD();

        ArrayList<LabeledPoint> newrdd = new ArrayList<>();

        LabeledPoint pos = new LabeledPoint(1.0, Vectors.dense(1.0, 0.0, 3.0));
        Double label = pos.label();
        Vector features = pos.features();
        System.out.println(label);
        System.out.println(features);

        JavaRDD<LabeledPoint> parsedData = data.map(line -> {
            Double label2 = line.label();
            Vector feature = line.features();
            if(label2==-1.0){
                label2=0.0;
            }
            return new LabeledPoint(label2, feature);
        });

        JavaRDD<LabeledPoint> parsedTestData = testdata.map(line -> {
            Double label2 = line.label();
            Vector feature = line.features();
            if(label2==-1.0){
                label2=0.0;
            }
            return new LabeledPoint(label2, feature);
        });



        // Split initial RDD into two... [60% training data, 40% testing data].
        JavaRDD<LabeledPoint> training = parsedData;
        training.cache();
        JavaRDD<LabeledPoint> test = parsedTestData;

        //printRDD(training);
        //printRDD(test);

        train(sc,training, test);
    }

    public static void task(SparkContext sc, String trainingDataSet, String testingDataSet, int numIterations, double stepSize, double regParam) throws IOException {
        String datasource = "ijcnn1";
        String path = "file:"+trainingDataSet; //"file:/home/vibhatha/data/sparksvm/ijcnn1/ijcnn1_train_spark.txt";
        String test_path = "file:"+testingDataSet;
        JavaRDD<LabeledPoint> data = MLUtils.loadLibSVMFile(sc, path).toJavaRDD();
        JavaRDD<LabeledPoint> testdata = MLUtils.loadLibSVMFile(sc, test_path).toJavaRDD();

        ArrayList<LabeledPoint> newrdd = new ArrayList<>();

        LabeledPoint pos = new LabeledPoint(1.0, Vectors.dense(1.0, 0.0, 3.0));
        Double label = pos.label();
        Vector features = pos.features();
        System.out.println(label);
        System.out.println(features);

        JavaRDD<LabeledPoint> parsedData = data.map(line -> {
            Double label2 = line.label();
            Vector feature = line.features();
            if(label2==-1.0){
                label2=0.0;
            }
            return new LabeledPoint(label2, feature);
        });

        JavaRDD<LabeledPoint> parsedTestData = testdata.map(line -> {
            Double label2 = line.label();
            Vector feature = line.features();
            if(label2==-1.0){
                label2=0.0;
            }
            return new LabeledPoint(label2, feature);
        });



        // Split initial RDD into two... [60% training data, 40% testing data].
        JavaRDD<LabeledPoint> training = parsedData;
        training.cache();
        JavaRDD<LabeledPoint> test = parsedTestData;

        //printRDD(training);
        //printRDD(test);

        train(sc,training, test, numIterations, stepSize, regParam);
    }


    public static void task(SparkContext sc, JavaRDD<LabeledPoint> trainingDataSet, JavaRDD<LabeledPoint> testingDataSet, int numIterations, double stepSize, double regParam) throws IOException {
        String datasource = "ijcnn1";
        String path = "file:"+trainingDataSet; //"file:/home/vibhatha/data/sparksvm/ijcnn1/ijcnn1_train_spark.txt";
        String test_path = "file:"+testingDataSet;
        JavaRDD<LabeledPoint> data = trainingDataSet;
        JavaRDD<LabeledPoint> testdata = testingDataSet;

        ArrayList<LabeledPoint> newrdd = new ArrayList<>();

        LabeledPoint pos = new LabeledPoint(1.0, Vectors.dense(1.0, 0.0, 3.0));
        Double label = pos.label();
        Vector features = pos.features();
        System.out.println(label);
        System.out.println(features);

        JavaRDD<LabeledPoint> parsedData = data.map(line -> {
            Double label2 = line.label();
            Vector feature = line.features();
            if(label2==-1.0){
                label2=0.0;
            }
            return new LabeledPoint(label2, feature);
        });

        JavaRDD<LabeledPoint> parsedTestData = testdata.map(line -> {
            Double label2 = line.label();
            Vector feature = line.features();
            if(label2==-1.0){
                label2=0.0;
            }
            return new LabeledPoint(label2, feature);
        });



        // Split initial RDD into two... [60% training data, 40% testing data].
        JavaRDD<LabeledPoint> training = parsedData;
        training.cache();
        JavaRDD<LabeledPoint> test = parsedTestData;

        //printRDD(training);
        //printRDD(test);

        train(sc,training, test, numIterations, stepSize, regParam);
    }



    public static void train(SparkContext sc,JavaRDD<LabeledPoint> training, JavaRDD<LabeledPoint> test ) throws IOException {
        // Run training algorithm to build the model.
        int numIterations = 100;
        long start_time = System.currentTimeMillis();
        final SVMModel model = SVMWithSGD.train(training.rdd(), numIterations, 0.01, 0.01);
        //model.clearThreshold();

        long end_time = System.currentTimeMillis();
        long elapsed_time = end_time - start_time;


        String svmModelPath= "model/svm/exp1";
// Save and load model
        File file = new File(svmModelPath);
        if(file.exists()){
            FileUtils.deleteDirectory(file);
        }

        model.save(sc, svmModelPath);
        SVMModel sameModel = SVMModel.load(sc, svmModelPath);

        JavaRDD<Vector> testFeatures = test.map(line -> {
            Vector feature = line.features();
            return feature;
        });

        JavaRDD<Double> testLabels = test.map(line -> {
            Double label = line.label();
            return label;
        });

        JavaRDD<Double> predictions = sameModel.predict(testFeatures);

       // double prediction = sameModel.predict(testFeatures.first());
        List<Double> predictionVals = predictions.collect();
        List<Double> expectedVals = testLabels.collect();
        double accuracy = predictionAccuracy(predictionVals, expectedVals);
        String record = "Accuracy : "+accuracy+", Training Time : "+elapsed_time/1000.0;
        System.out.println(record);
        Util.appendLogs(LOG_DEST,record);

    }

    public static void train(SparkContext sc,JavaRDD<LabeledPoint> training, JavaRDD<LabeledPoint> test, int numIterations, double stepSize, double regParam) throws IOException {
        // Run training algorithm to build the model.

        long start_time = System.currentTimeMillis();
        final SVMModel model = SVMWithSGD.train(training.rdd(), numIterations, stepSize, regParam);

        long end_time = System.currentTimeMillis();
        long elapsed_time = end_time - start_time;

        String svmModelPath= "model/svm/exp1";
// Save and load model
        File file = new File(svmModelPath);
        if(file.exists()){
            FileUtils.deleteDirectory(file);
        }

        model.save(sc, svmModelPath);
        SVMModel sameModel = SVMModel.load(sc, svmModelPath);

        JavaRDD<Vector> testFeatures = test.map(line -> {
            Vector feature = line.features();
            return feature;
        });

        JavaRDD<Double> testLabels = test.map(line -> {
            Double label = line.label();
            return label;
        });

        JavaRDD<Double> predictions = sameModel.predict(testFeatures);

        // double prediction = sameModel.predict(testFeatures.first());

        List<Double> predictionVals = predictions.collect();
        List<Double> expectedVals = testLabels.collect();
        double accuracy = predictionAccuracy(predictionVals, expectedVals);
        String record = "Accuracy : "+accuracy+", Training Time : "+elapsed_time/1000.0;
        System.out.println(record);
        Util.appendLogs(LOG_DEST,record);

        /*System.out.println("Test Labels");
        System.out.println("===================================");
        testLabels.foreach(s->{
            System.out.println(s);
        });

        System.out.println("Prediction Labels");
        System.out.println("===================================");
        predictions.foreach(s->{
            System.out.println(s);
        });*/

    }



    public static double  predictionAccuracy(List<Double> predictions, List<Double> tests){
        double acc = 0.0;
        int count = 0;
        int matches = 0;
        for (Double d: predictions){
            //System.out.println(d+","+tests.get(count));
            if(d.intValue() == tests.get(count).intValue()){
                matches++;
            }
            count++;
        }
        acc = (double)matches / (double)(predictions.size())*100.0;
        return acc;
    }

    public static void printRDD(JavaRDD<LabeledPoint> parsedData){
        parsedData.foreach(x->{
            //
            Double label1 = x.label();
            Vector feature = x.features();

            LabeledPoint newLabelPoint = new LabeledPoint(label1, feature);
            System.out.println(newLabelPoint.label());

        });
    }


    public static void init(String[] args) {

        options.addOption("h", "help", false, "show help.");
        options.addOption("train", "training data set path", true, "Set training data set . ex: -train train_data");
        options.addOption("test", "testing data set path", true, "Set testing data set . ex: -test test_data");
        options.addOption("iterations", "iteration number", true, "Set number of iterations . ex: -iterations 100");
        options.addOption("stepSize", "step size", true, "Set step size . ex: -stepSize 0.01");
        options.addOption("regParam", "regularization parameter", true, "Set testing data set. ex: -regParam 0.02");
        options.addOption("split", "Data splitting ratio", true, "Training and Testing data splitting. ex: -split 0.8 (80% of training and 20% of testing)");
        options.addOption("log", "Logging functionality", true, "Log file path addition. ex: logs/log1.txt");
        options.getOption("test").setOptionalArg(true);
        options.getOption("split").setOptionalArg(true);
        options.getOption("log").setOptionalArg(true);

    }

    public static CommandLine parse(String [] args) {
        CommandLineParser parser = new BasicParser();

        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);

            if (cmd.hasOption("h"))
                help();

            if (cmd.hasOption("train")) {
                log.log(Level.INFO, "Training data set -train=" + cmd.getOptionValue("train"));
                // Whatever you want to do with the setting goes here
            } else {
                log.log(Level.SEVERE, "Missing -train option");
                help();
            }

            if (cmd.hasOption("test")) {
                log.log(Level.INFO, "Testing data set -test=" + cmd.getOptionValue("test"));
                // Whatever you want to do with the setting goes here
            }

            if (cmd.hasOption("iterations")) {
                log.log(Level.INFO, "Iterations -iterations=" + cmd.getOptionValue("iterations"));
                // Whatever you want to do with the setting goes here
            } else {
                log.log(Level.SEVERE, "Missing -iterations option");
                help();
            }

            if (cmd.hasOption("stepSize")) {
                log.log(Level.INFO, "Step Size -stepSize=" + cmd.getOptionValue("stepSize"));
                // Whatever you want to do with the setting goes here
            } else {
                log.log(Level.SEVERE, "Missing -stepSize option");
                help();
            }

            if (cmd.hasOption("regParam")) {
                log.log(Level.INFO, "Regularization Parameter -regParam=" + cmd.getOptionValue("regParam"));
                // Whatever you want to do with the setting goes here
            } else {
                log.log(Level.SEVERE, "Missing -regParam option");
                help();
            }

            if (cmd.hasOption("split")) {
                log.log(Level.INFO, "Split Parameter -split=" + cmd.getOptionValue("split"));
                // Whatever you want to do with the setting goes here
            }
            if (cmd.hasOption("log")) {
                log.log(Level.INFO, "Log Parameter -log=" + cmd.getOptionValue("log"));
                // Whatever you want to do with the setting goes here
            }

        } catch (ParseException e) {
            log.log(Level.SEVERE, "Failed to parse comand line properties", e);
            help();
        }

        return cmd;
    }

    private static void help() {
        // This prints out some help
        HelpFormatter formater = new HelpFormatter();

        formater.printHelp("ExpSVM", options);
        System.exit(0);
    }

    public static ArrayList<JavaRDD<LabeledPoint>> dataSplit(String path, SparkContext sc, double ratio){
        JavaRDD<LabeledPoint> data = MLUtils.loadLibSVMFile(sc, path).toJavaRDD();
        ArrayList<JavaRDD<LabeledPoint>> list = new ArrayList<>();
        JavaRDD<LabeledPoint> training = data.sample(false, ratio, 11L);
        training.cache();
        JavaRDD<LabeledPoint> test = data.subtract(training);
        list.add(training);
        list.add(test);
        return list;
    }



}
