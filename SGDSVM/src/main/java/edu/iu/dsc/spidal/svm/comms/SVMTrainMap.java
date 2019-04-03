package edu.iu.dsc.spidal.svm.comms;

import edu.iu.dsc.spidal.svm.math.Initializer;
import edu.iu.dsc.spidal.svm.model.DataModel;
import edu.iu.dsc.spidal.svm.sgd.pegasos.PegasosSgdSvm;
import edu.iu.dsc.spidal.svm.util.DataUtils;
import org.apache.spark.api.java.JavaRDD;

import java.io.Serializable;
import java.util.Arrays;
import java.util.logging.Logger;

public class SVMTrainMap implements Serializable {

    private static final Logger LOG = Logger.getLogger(SVMTrainMap.class.getName());

    private JavaRDD<double[][]> XyAll;

    private int features;

    private int samples;

    private double alpha;

    private int iterations;

    JavaRDD<double[]> wRdd;

    public SVMTrainMap(JavaRDD<double[][]> xyAll, int features, int samples, double alpha, int iterations) {
        this.XyAll = xyAll;
        this.features = features;
        this.samples = samples;
        this.alpha = alpha;
        this.iterations = iterations;
    }

    public void map() {
        this.wRdd = XyAll.map(v1 -> {
            System.out.println("Data ::::: " + v1.length);
            double [] w = Initializer.initialWeights(v1[0].length);
            DataModel dataModel = DataUtils.getDataModel1(v1);
            double [][] x = dataModel.getxAll();
            double [] y = dataModel.getyAll();
            PegasosSgdSvm pegasosSgdSvm = new PegasosSgdSvm(w, x, y, this.alpha, this.iterations, this.features);
            pegasosSgdSvm.iterativeSgd(w, x, y);
            double [] wFinal = pegasosSgdSvm.getW();
            LOG.info(String.format("W Final : %s ", Arrays.toString(wFinal)));
            return wFinal;
        });
    }

    public JavaRDD<double[]> getwRdd() {
        return wRdd;
    }
}
