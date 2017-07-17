rows=$1
columns=$2
filename=$3
$SPARK_HOME/bin/spark-submit --class edu.indiana.ise.spidal.util.GenerateMatrix --master local[1] target/scala-2.11/sparkmlibbenchmark_2.11-1.0.jar ${rows} ${columns} ${filename}
