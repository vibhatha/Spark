package edu.iu.dsc.spidal.svm;

import edu.iu.dsc.spidal.svm.SVMRunner;
import edu.iu.dsc.spidal.svm.comms.SVMReduce;
import edu.iu.dsc.spidal.svm.comms.SVMTrainMap;
import edu.iu.dsc.spidal.svm.data.DistributedDataLoader;
import edu.iu.dsc.spidal.svm.data.StreamingDistributedDataLoader;
import edu.iu.dsc.spidal.svm.math.Initializer;
import edu.iu.dsc.spidal.svm.math.Matrix;
import edu.iu.dsc.spidal.svm.model.DataModel;
import edu.iu.dsc.spidal.svm.sgd.pegasos.PegasosSgdSvm;
import edu.iu.dsc.spidal.svm.util.DataUtils;
import edu.iu.dsc.spidal.svm.util.FileUtils;
import org.apache.commons.cli.*;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.StreamingContext;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SVMStreamingRunner {
    private static final Logger LOG = Logger.getLogger(SVMRunner.class.getName());

    protected static JavaSparkContext sc;

    protected static StreamingContext ssc;

    protected static JavaStreamingContext jssc;

    private static JavaRDD<Double> y;

    private static JavaRDD<double[]> X;

    private static JavaRDD<double[]> Xy;

    private static JavaRDD<double[][]> XyAll;

    private static String dataSource = "/home/vibhatha/data/svm/ijcnn1/training.csv";

    private static int features = 22;

    private static int samples = 35000;

    private static int parallelism = 4;

    private static double alpha = 0.001;

    private static int iterations = 10;

    private static JavaRDD<double[]> wRdd;

    private static double[] wFinal;

    private static Options options;

    private static String statsDest = "stats/";

    private static String expName ="";

    private static double dataLoadingTime = 0;

    private static double trainingTime = 0;

    private final static double N2S = 1000000000;


    public static void main(String[] args) {
        initialize();
        CommandLine cmd = parse(args);
        String trainingDataSet = cmd.getOptionValue("train");
        String testingDataSet = cmd.getOptionValue("test");
        iterations = Integer.parseInt(cmd.getOptionValue("iterations"));
        parallelism = Integer.parseInt(cmd.getOptionValue("parallelism"));
        alpha = Double.parseDouble(cmd.getOptionValue("stepSize"));
        double regParam = Double.parseDouble(cmd.getOptionValue("regParam"));
        expName = cmd.getOptionValue("name");
        FileUtils.mkdir(statsDest);
        statsDest += cmd.getOptionValue("stats");
        LOG.info("Stats Save Path : " + statsDest);
        //sc = new JavaSparkContext();
        SparkConf conf = new SparkConf().setAppName("SGD-SVM-SPARK");
        //ssc = new StreamingContext(conf, new Duration(1));
        jssc = new JavaStreamingContext(conf, new Duration(1));

        long t1 = 0;
        t1 = System.nanoTime();
        StreamingDistributedDataLoader distributedDataLoader = new StreamingDistributedDataLoader(trainingDataSet, features, samples,
                parallelism, jssc);
        distributedDataLoader.prepare();
        XyAll = distributedDataLoader.getData();
        dataLoadingTime = (double)(System.nanoTime() - t1) / N2S;
        t1 = System.nanoTime();
        SVMTrainMap svmTrainMap = new SVMTrainMap(XyAll, features, samples, alpha, iterations);
        svmTrainMap.map();
        wRdd = svmTrainMap.getwRdd();
        SVMReduce svmReduce = new SVMReduce(wRdd, parallelism);
        svmReduce.reduce();
        wFinal = svmReduce.getW();
        trainingTime = (double)(System.nanoTime() - t1) / N2S;
        LOG.info(String.format("WReduced  Final : %s ", Arrays.toString(wFinal)));
        try {
            FileUtils.logSave(statsDest, features, samples, parallelism, dataLoadingTime, trainingTime);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOG.info(String.format("Data Loading Time : %f, Training Time : %f", dataLoadingTime, trainingTime));
    }


    public static void initialize() {
        options = new Options();
        options.addOption("h", "help", false, "show help.");
        options.addOption("train", "training data set path", true, "Set training data set . ex: -train train_data");
        options.addOption("test", "testing data set path", true, "Set testing data set . ex: -test test_data");
        options.addOption("iterations", "iteration number", true, "Set number of iterations . ex: -iterations 100");
        options.addOption("parallelism", "parallelism", true, "Set the parallelism expected. ex: 2 ");
        options.addOption("stepSize", "step size", true, "Set step size . ex: -stepSize 0.01");
        options.addOption("regParam", "regularization parameter", true, "Set testing data set. ex: -regParam 0.02");
        options.addOption("split", "Data splitting ratio", true, "Training and Testing data splitting. ex: -split 0.8 (80% of training and 20% of testing)");
        options.addOption("stats", "Statistics Save functionality", true, "Statistics file path addition. (File format we use is csv) ex: stats/stats-1.csv");
        options.addOption("name", "Experiment Naming functionality", true, "Name the experiment with dataset id  ex: webspam-first-experiment");
        options.getOption("test").setOptionalArg(true);
        options.getOption("split").setOptionalArg(true);
    }

    public static CommandLine parse(String [] args) {
        CommandLineParser parser = new BasicParser();

        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);

            if (cmd.hasOption("h"))
                help();

            if (cmd.hasOption("train")) {
                LOG.log(Level.INFO, "Training data set -train=" + cmd.getOptionValue("train"));
                // Whatever you want to do with the setting goes here
            } else {
                LOG.log(Level.SEVERE, "Missing -train option");
                help();
            }

            if (cmd.hasOption("test")) {
                LOG.log(Level.INFO, "Testing data set -test=" + cmd.getOptionValue("test"));
                // Whatever you want to do with the setting goes here
            }

            if (cmd.hasOption("iterations")) {
                LOG.log(Level.INFO, "Iterations -iterations=" + cmd.getOptionValue("iterations"));
                // Whatever you want to do with the setting goes here
            } else {
                LOG.log(Level.SEVERE, "Missing -iterations option");
                help();
            }

            if (cmd.hasOption("parallelism")) {
                LOG.log(Level.INFO, "Parallelism -parallelism=" + cmd.getOptionValue("parallelism"));
                // Whatever you want to do with the setting goes here
            } else {
                LOG.log(Level.SEVERE, "Missing -parallelism option");
                help();
            }


            if (cmd.hasOption("stepSize")) {
                LOG.log(Level.INFO, "Step Size -stepSize=" + cmd.getOptionValue("stepSize"));
                // Whatever you want to do with the setting goes here
            } else {
                LOG.log(Level.SEVERE, "Missing -stepSize option");
                help();
            }

            if (cmd.hasOption("regParam")) {
                LOG.log(Level.INFO, "Regularization Parameter -regParam=" + cmd.getOptionValue("regParam"));
                // Whatever you want to do with the setting goes here
            } else {
                LOG.log(Level.SEVERE, "Missing -regParam option");
                help();
            }

            if (cmd.hasOption("stats")) {
                LOG.log(Level.INFO, "Statistics Saving Path Parameter -stats=" + cmd.getOptionValue("stats"));
                // Whatever you want to do with the setting goes here
            } else {
                LOG.log(Level.SEVERE, "Missing -stats option");
                help();
            }

            if (cmd.hasOption("name")) {
                LOG.log(Level.INFO, "Name Parameter -name=" + cmd.getOptionValue("name"));
                // Whatever you want to do with the setting goes here
            } else {
                LOG.log(Level.SEVERE, "Missing -name option");
                help();
            }


            if (cmd.hasOption("split")) {
                LOG.log(Level.INFO, "Split Parameter -split=" + cmd.getOptionValue("split"));
                // Whatever you want to do with the setting goes here
            }

        } catch (ParseException e) {
            LOG.log(Level.SEVERE, "Failed to parse comand line properties", e);
            help();
        }

        return cmd;
    }


    private static void help() {
        // This prints out some help
        HelpFormatter formater = new HelpFormatter();
        formater.printHelp("SGD-SVM", options);
        System.exit(0);
    }

    @Deprecated
    public static void m2() {

    }

    @Deprecated
    public static void m1() {


        JavaRDD<String> data = sc.textFile(dataSource);
        LOG.info(String.format("Num of Partitions : %d", data.getNumPartitions()));
        prepareData(data);
        LOG.info(String.format("******* %d", XyAll.collect().get(0).length));
        //XyAll.repartition(4);
        JavaRDD<double[]> v = XyAll.map(v1 -> {
            System.out.println("Data ::::: " + v1.length);
            double [] w = Initializer.initialWeights(v1[0].length);
            DataModel dataModel = DataUtils.getDataModel1(v1);
            double [][] x = dataModel.getxAll();
            double [] y = dataModel.getyAll();
            PegasosSgdSvm pegasosSgdSvm = new PegasosSgdSvm(w, x, y, 0.001, 10, features);
            pegasosSgdSvm.iterativeSgd(w, x, y);
            double [] wFinal = pegasosSgdSvm.getW();
            LOG.info(String.format("W Final : %s ", Arrays.toString(wFinal)));
            return wFinal;
        });

        v.repartition(2);
        double [] rW = v.reduce((v1, v2) -> {
            System.out.println(String.format("%f,%f", v1[0],v2[0]));
            double [] w = new double[v1.length];
            w = Matrix.add(v1, v2);
            return w;
        });
        LOG.info(String.format("WReduced  Final : %s ", Arrays.toString(rW)));
    }

    @Deprecated
    public static void m3() {
        JavaRDD<double[]> v = XyAll.map(v1 -> {
            System.out.println("Data ::::: " + v1.length);
            double [] w = Initializer.initialWeights(v1[0].length);
            DataModel dataModel = DataUtils.getDataModel1(v1);
            double [][] x = dataModel.getxAll();
            double [] y = dataModel.getyAll();
            PegasosSgdSvm pegasosSgdSvm = new PegasosSgdSvm(w, x, y, 0.001, 10, features);
            pegasosSgdSvm.iterativeSgd(w, x, y);
            double [] wFinal = pegasosSgdSvm.getW();
            LOG.info(String.format("W Final : %s ", Arrays.toString(wFinal)));
            return wFinal;
        });



        v.repartition(parallelism);
        double [] rW = v.reduce((v1, v2) -> {
            System.out.println(String.format("%f,%f", v1[0],v2[0]));
            double [] w = new double[v1.length];
            w = Matrix.add(v1, v2);
            return w;
        });
        LOG.info(String.format("WReduced  Final : %s ", Arrays.toString(rW)));
    }

    @Deprecated
    public static void printJavaRDD (JavaRDD<String> data) {
        data.foreach(s -> {
            System.out.println(s);
        });
    }

    @Deprecated
    public static void filterRDD(JavaRDD<String> data) {
        data.filter(v1 -> {
            String [] s = v1.split(",");
            System.out.println((String.format("Label : %s", s[0])));
            return true;
        });
    }

    @Deprecated
    public static void prepareData(JavaRDD<String> strRDD) {
        List<String> list = strRDD.collect();
        int dataPoints = list.size();
        List<Double> ylistDouble = new ArrayList<>(dataPoints);
        List<double[]> xlistDouble = new ArrayList<>(dataPoints);
        List<double[]> xylistDouble = new ArrayList<>(dataPoints);
        List<double[][]> xylistDoubleAll = new ArrayList<>(1);
        LOG.info(String.format("List Size : %d, %s", dataPoints, list.get(0)));
        double [][] allXy = new double[dataPoints][features+1];
        for (int i = 0; i < dataPoints; i++) {
            String s = list.get(i);
            String [] vals = s.split(",");
            double [] xvals = new double[vals.length-1];
            double [] xyvals = new double[vals.length];
            ylistDouble.add(Double.parseDouble(vals[0]));
            for (int j = 0; j < xvals.length; j++) {
                xvals[j] = Double.parseDouble(vals[j+1]);
            }
            xlistDouble.add(xvals);
            for (int j = 0; j < vals.length; j++) {
                xyvals[j] = Double.parseDouble(vals[j]);
            }
            allXy[i] = xyvals;

        }
        int allDataPoints = allXy.length;
        double [][] d1 = new double[allDataPoints/2][features];
        double [][] d2 = new double[allDataPoints/2][features];
        System.arraycopy(allXy, 0, d1, 0, allDataPoints/2);
        System.arraycopy(allXy, allDataPoints/2, d2, 0, allDataPoints/2);
        xylistDoubleAll.add(d1);
        xylistDoubleAll.add(d2);
        y = sc.parallelize(ylistDouble);
        X = sc.parallelize(xlistDouble);
        Xy = sc.parallelize(xylistDouble);
        XyAll = sc.parallelize(xylistDoubleAll);
    }
}
