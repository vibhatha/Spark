filename=$1
iterations=$2
stepSize=$3
outputfile=$4
parallelism=$5
memory=$6
executors=$7
$SPARK_HOME/bin/spark-submit --class edu.indiana.ise.spidal.regression.ExpMLR --master local[${parallelism}] --executor-memory ${memory} --total-executor-cores ${executors} --driver-memory 32g target/scala-2.11/sparkmlibbenchmark_2.11-1.0.jar ${filename} ${iterations} ${stepSize} ${parallelism} ${outputfile}


