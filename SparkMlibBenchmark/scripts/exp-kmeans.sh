filename=$1
clusters=$2
iterations=$3
outputfile=$4
$SPARK_HOME/bin/spark-submit --class edu.indiana.ise.spidal.kmeans.ExpKMeans --master local[1] target/scala-2.11/sparkmlibbenchmark_2.11-1.0.jar /home/vibhatha/ds/data/kmeans/${filename} ${clusters} ${iterations} /home/vibhatha/ds/kmeans/output/${outputfile}
