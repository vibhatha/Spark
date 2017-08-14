filename=$1
$SPARK_HOME/bin/spark-submit --class edu.indiana.ise.spidal.pca.ExpPCA --master local[1] target/scala-2.11/sparkmlibbenchmark_2.11-1.0.jar /home/vibhatha/ds/data/pca/${filename}
