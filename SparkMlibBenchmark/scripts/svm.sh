filename=$1
iterations=$2
outputfile=$3
parallelism=$4
memory=$5
executors=$6
$SPARK_HOME/bin/spark-submit --class edu.indiana.ise.spidal.mlr.ExpMLR --master local[${parallelism}] --executor-memory ${memory} --total-executor-cores ${executors} --driver-memory 32g target/scala-2.11/sparkmlibbenchmark_2.11-1.0.jar ${filename} ${iterations} ${outputfile} ${parallelism}


