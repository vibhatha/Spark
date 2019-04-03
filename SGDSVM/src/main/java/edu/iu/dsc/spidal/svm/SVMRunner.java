package edu.iu.dsc.spidal.svm;

import edu.iu.dsc.spidal.svm.math.Initializer;
import edu.iu.dsc.spidal.svm.math.Matrix;
import edu.iu.dsc.spidal.svm.model.DataModel;
import edu.iu.dsc.spidal.svm.sgd.pegasos.PegasosSgdSvm;
import edu.iu.dsc.spidal.svm.util.DataUtils;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Spliterator;
import java.util.logging.Logger;

public class SVMRunner {

    private static final Logger LOG = Logger.getLogger(SVMRunner.class.getName());

    protected static JavaSparkContext sc;

    private static JavaRDD<Double> y;

    private static JavaRDD<double[]> X;

    private static JavaRDD<double[]> Xy;

    private static JavaRDD<double[][]> XyAll;

    protected static int features = 22;

    public static void main(String[] args) {
        m1();
    }

    public static void m2() {

    }

    public static void m1() {
        sc = new JavaSparkContext();

        JavaRDD<String> data = sc.textFile("/home/vibhatha/data/svm/ijcnn1/training.csv");
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
            return wFinal;
        });

        double [] wTrained = v.collect().get(0);
        LOG.info(String.format("W Final : %s ", Arrays.toString(wTrained)));
        v.repartition(2);
        double [] rW = v.reduce((v1, v2) -> {
            System.out.println(String.format("%f,%f", v1[0],v2[0]));
            double [] w = new double[v1.length];
            w = Matrix.add(v1, v2);
            return w;
        });
        LOG.info(String.format("WReduced  Final : %s ", Arrays.toString(rW)));
    }


    public static void printJavaRDD (JavaRDD<String> data) {
        data.foreach(s -> {
            System.out.println(s);
        });
    }

    public static void filterRDD(JavaRDD<String> data) {
        data.filter(v1 -> {
            String [] s = v1.split(",");
            System.out.println((String.format("Label : %s", s[0])));
            return true;
        });
    }

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
