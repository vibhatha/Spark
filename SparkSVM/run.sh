#!/usr/bin/env bash
mvn package
$SPARK_HOME/bin/spark-submit --class edu.iu.ise.svm.ExpSVM --master local[2] target/SparkSVM-1.0.0.jar -train ~/data/libsvm/ijcnn1/ijcnn1_train -test ~/data/libsvm/ijcnn1/ijcnn1_test
#$SPARK_HOME/bin/spark-submit --class "SimpleApp" --master local[4] target/SparkSVM-1.0.0.jar

