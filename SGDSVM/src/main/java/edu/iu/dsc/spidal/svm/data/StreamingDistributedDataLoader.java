package edu.iu.dsc.spidal.svm.data;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.streaming.StreamingContext;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.collection.JavaConverters;
import scala.collection.Seq;
import scala.concurrent.JavaConversions;
import scala.reflect.ClassTag;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class StreamingDistributedDataLoader {

    private static final Logger LOG = Logger.getLogger(StreamingDistributedDataLoader.class.getName());

    private JavaRDD<double[][]> XyAll;

    private String dataSource = "";

    private int features;

    private int samples;

    private int parallelism;

    private StreamingContext ssc;

    private JavaStreamingContext jssc;

    public StreamingDistributedDataLoader(String dataSource, int features, int samples, int parallelism, StreamingContext ssc) {
        this.dataSource = dataSource;
        this.features = features;
        this.samples = samples;
        this.parallelism = parallelism;
        this.ssc = ssc;
    }

    public StreamingDistributedDataLoader(String dataSource, int features, int samples, int parallelism, JavaStreamingContext jssc) {
        this.dataSource = dataSource;
        this.features = features;
        this.samples = samples;
        this.parallelism = parallelism;
        this.jssc = jssc;
    }

    public void prepare() {

        //JavaRDD<String> strRDD = ssc.sparkContext().textFile(this.dataSource, 2).toJavaRDD();
        JavaRDD<String> strRDD = jssc.sparkContext().textFile(this.dataSource).cache();
        LOG.info(String.format("Data Source : %s, Num of Partitions : %d", this.dataSource, strRDD.getNumPartitions()));
        List<String> list = strRDD.collect();
        int dataPoints = list.size();
        List<double[][]> xylistDoubleAll = new ArrayList<>(this.parallelism);
        double[][] allXy = new double[dataPoints][features + 1];
        for (int i = 0; i < dataPoints; i++) {
            String s = list.get(i);
            String[] vals = s.split(",");
            double[] xyvals = new double[vals.length];
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
            double[][] d = new double[dataPerPatition][this.features];
            System.arraycopy(allXy, start, d, 0, dataPerPatition);
            xylistDoubleAll.add(d);
        }

        //Seq<double[][]> seq = JavaConverters.asScalaIteratorConverter(xylistDoubleAll.iterator()).asScala().toSeq();
        this.XyAll = jssc.sparkContext().parallelize(xylistDoubleAll);

    }

    public JavaRDD<double[][]> getData() {
        return this.XyAll;
    }

}
