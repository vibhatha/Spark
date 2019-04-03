# SGD Based SVM [Part of Twister2 Research](https://twister2.gitbook.io/twister2/)

## Status

Ready for Alpha Testing

#Sample Run

```bash
$SPARK_HOME/bin/spark-submit --class edu.iu.dsc.spidal.svm.SVMRunner --master local[2] target/SGDSVM-1.0-SNAPSHOT.jar --train /home/vibhatha/data/svm/ijcnn1/training.csv --test /home/vibhatha/data/svm/ijcnn1/testing.csv --iterations 10 --stepSize 0.001 --regParam 1.0 --stats stats.txt -name test-svm --parallelism 8

```