filename=$1
lambda=$2
modelType=$3
outputfile=$4
parallelism=$5
memory=$5
executors=$6
$SPARK_HOME/bin/spark-submit --class edu.indiana.ise.spidal.naivebayes.ExpNBC --master local[${parallelism}] --executor-memory ${memory} --total-executor-cores ${executors} --driver-memory 32g target/scala-2.11/sparkmlibbenchmark_2.11-1.0.jar ${filename} ${lambda} ${modelType} ${parallelism} ${outputfile}


