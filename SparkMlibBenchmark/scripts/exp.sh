filename=$1
parallelism=$2
memory=$3
executors=$4
$SPARK_HOME/bin/spark-submit --class edu.indiana.ise.spidal.pca.ExpPCA --master local[${parallelism}] --executor-memory ${memory} --total-executor-cores ${executors} --driver-memory 32g target/scala-2.11/sparkmlibbenchmark_2.11-1.0.jar ${filename}
