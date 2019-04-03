package edu.iu.dsc.spidal.svm.comms;

import edu.iu.dsc.spidal.svm.math.Matrix;
import org.apache.spark.api.java.JavaRDD;

import java.io.Serializable;
import java.util.logging.Logger;

public class SVMReduce implements Serializable {

    private static final Logger LOG = Logger.getLogger(SVMReduce.class.getName());

    private JavaRDD<double[]> wRdd;

    private double[] wFinal;

    private int parallelism;

    public SVMReduce(JavaRDD<double[]> wRdd, int parallelism) {
        this.wRdd = wRdd;
        this.parallelism = parallelism;
    }

    public void reduce() {
        this.wRdd.repartition(this.parallelism);
        this.wFinal = this.wRdd.reduce((v1, v2) -> {
            double [] w ;
            w = Matrix.add(v1, v2);
            return w;
        });
    }

    public double[] getW() {
        for (int i = 0; i < this.wFinal.length; i++) {
            this.wFinal[i] /= (double)parallelism;
        }
        return this.wFinal;
    }
}
