filename=$1
parallelism=$2
Id=$3
$SPARK_HOME/bin/spark-submit --class edu.indiana.ise.spidal.pca.ExpPCA --master local[${parallelism}] --conf spark.driver.maxResultSize=16g --executor-memory 128g --total-executor-cores 1 --driver-memory 32g target/scala-2.11/sparkmlibbenchmark_2.11-1.0.jar /N/u/vlabeyko/data/pca-spark/dataset/${filename} ${parallelism} /N/u/vlabeyko/experiments/pca-spark/${filename}-${parallelism}-${Id}

