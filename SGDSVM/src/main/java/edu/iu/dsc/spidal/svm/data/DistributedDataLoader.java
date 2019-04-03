package edu.iu.dsc.spidal.svm.data;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DistributedDataLoader {

    private static final Logger LOG = Logger.getLogger(DistributedDataLoader.class.getName());

    private  JavaRDD<double[][]> XyAll;

    private String dataSource = "";

    private int features;

    private int samples;

    private int parallelism;

    private JavaSparkContext sc;

    public DistributedDataLoader(String dataSource, int features, int samples, int parallelism, JavaSparkContext sc) {
        this.dataSource = dataSource;
        this.features = features;
        this.samples = samples;
        this.parallelism = parallelism;
        this.sc = sc;
    }

    public void prepare() {
        JavaRDD<String> strRDD = sc.textFile(this.dataSource);
        LOG.info(String.format("Data Source : %s, Num of Partitions : %d", this.dataSource, strRDD.getNumPartitions()));
        List<String> list = strRDD.collect();
        int dataPoints = list.size();
        List<double[][]> xylistDoubleAll = new ArrayList<>(this.parallelism);
        double [][] allXy = new double[dataPoints][features+1];
        for (int i = 0; i < dataPoints; i++) {
            String s = list.get(i);
            String [] vals = s.split(",");
            double [] xyvals = new double[vals.length];
            for (int j = 0; j < vals.length; j++) {
                xyvals[j] = Double.parseDouble(vals[j]);
            }
            allXy[i] = xyvals;
        }
        // TODO : remainder non-zero state must be handled
        int dataPerPatition = dataPoints / parallelism;
        int start = 0;
        int end = 0;
        for (int i = 0; i < this.parallelism; i++) {
            start = i * dataPerPatition;
            end = start + dataPerPatition;
            double [][] d = new double[dataPerPatition][this.features];
            System.arraycopy(allXy, start, d, 0, dataPerPatition);
            xylistDoubleAll.add(d);
        }
        this.XyAll = sc.parallelize(xylistDoubleAll);
    }

    public  JavaRDD<double[][]> getData() {
        return this.XyAll;
    }


}
