$SPARK_HOME/bin/spark-submit --class edu.iu.ise.svm.ExpSVM --master local[2] target/SparkSVM-1.0.0.jar -train ~/data/libsvm/ijcnn1/ijcnn1_train -test ~/data/libsvm/ijcnn1/ijcnn1_test -iterations 1000 -stepSize 0.05 -regParam 0.01

