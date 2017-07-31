classes=$1
rows=$2
columns=$3
filename=$4
parallelism=$6
$SPARK_HOME/bin/spark-submit --class edu.indiana.ise.spidal.util.LibSVMGenerator --master local[${parallelism}] target/scala-2.11/sparkmlibbenchmark_2.11-1.0.jar ${classes} ${rows} ${columns} ${filename}
