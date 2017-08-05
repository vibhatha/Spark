id=$1 #experiment id
mkdir /N/u/vlabeyko/experiments/mlr-spark/${id}
sh scripts/mlr.sh /N/u/vlabeyko/data/libsvm/mat_100K_300 100 /N/u/vlabeyko/experiments/mlr-spark/${id}/exp-100k-dem 1 128g 1 
sh scripts/mlr.sh /N/u/vlabeyko/data/libsvm/mat_100K_300 100 /N/u/vlabeyko/experiments/mlr-spark/${id}/exp-100k-dem 8 128g 1
sh scripts/mlr.sh /N/u/vlabeyko/data/libsvm/mat_100K_300 100 /N/u/vlabeyko/experiments/mlr-spark/${id}/exp-100k-dem 16 128g 1
sh scripts/mlr.sh /N/u/vlabeyko/data/libsvm/mat_100K_300 100 /N/u/vlabeyko/experiments/mlr-spark/${id}/exp-100k-dem 32 128g 1
sh scripts/mlr.sh /N/u/vlabeyko/data/libsvm/mat_100K_300 100 /N/u/vlabeyko/experiments/mlr-spark/${id}/exp-100k-dem 64 128g 1

sh scripts/mlr.sh /N/u/vlabeyko/data/libsvm/mat_500K_300 100 /N/u/vlabeyko/experiments/mlr-spark/${id}/exp-500k-dem 1 128g 1
sh scripts/mlr.sh /N/u/vlabeyko/data/libsvm/mat_500K_300 100 /N/u/vlabeyko/experiments/mlr-spark/${id}/exp-500k-dem 8 128g 1
sh scripts/mlr.sh /N/u/vlabeyko/data/libsvm/mat_500K_300 100 /N/u/vlabeyko/experiments/mlr-spark/${id}/exp-500k-dem 16 128g 1
sh scripts/mlr.sh /N/u/vlabeyko/data/libsvm/mat_500K_300 100 /N/u/vlabeyko/experiments/mlr-spark/${id}/exp-500k-dem 32 128g 1
sh scripts/mlr.sh /N/u/vlabeyko/data/libsvm/mat_500K_300 100 /N/u/vlabeyko/experiments/mlr-spark/${id}/exp-500k-dem 64 128g 1

sh scripts/mlr.sh /N/u/vlabeyko/data/libsvm/mat_1M_300 100 /N/u/vlabeyko/experiments/mlr-spark/${id}/exp-1M-dem 1 128g 1
sh scripts/mlr.sh /N/u/vlabeyko/data/libsvm/mat_1M_300 100 /N/u/vlabeyko/experiments/mlr-spark/${id}/exp-1M-dem 8 128g 1
sh scripts/mlr.sh /N/u/vlabeyko/data/libsvm/mat_1M_300 100 /N/u/vlabeyko/experiments/mlr-spark/${id}/exp-1M-dem 16 128g 1
sh scripts/mlr.sh /N/u/vlabeyko/data/libsvm/mat_1M_300 100 /N/u/vlabeyko/experiments/mlr-spark/${id}/exp-1M-dem 32 128g 1
sh scripts/mlr.sh /N/u/vlabeyko/data/libsvm/mat_1M_300 100 /N/u/vlabeyko/experiments/mlr-spark/${id}/exp-1M-dem 64 128g 1

sh scripts/mlr.sh /N/u/vlabeyko/data/libsvm/mat_10M_300 100 /N/u/vlabeyko/experiments/mlr-spark/${id}/exp-10M-dem 1 128g 1
sh scripts/mlr.sh /N/u/vlabeyko/data/libsvm/mat_10M_300 100 /N/u/vlabeyko/experiments/mlr-spark/${id}/exp-10M-dem 8 128g 1
sh scripts/mlr.sh /N/u/vlabeyko/data/libsvm/mat_10M_300 100 /N/u/vlabeyko/experiments/mlr-spark/${id}/exp-10M-dem 16 128g 1
sh scripts/mlr.sh /N/u/vlabeyko/data/libsvm/mat_10M_300 100 /N/u/vlabeyko/experiments/mlr-spark/${id}/exp-10M-dem 32 128g 1
sh scripts/mlr.sh /N/u/vlabeyko/data/libsvm/mat_10M_300 100 /N/u/vlabeyko/experiments/mlr-spark/${id}/exp-10M-dem 64 128g 1

sh scripts/mlr.sh /N/u/vlabeyko/data/libsvm/mat_5M_300 100 /N/u/vlabeyko/experiments/mlr-spark/${id}/exp-5M-dem 1 128g 1
sh scripts/mlr.sh /N/u/vlabeyko/data/libsvm/mat_5M_300 100 /N/u/vlabeyko/experiments/mlr-spark/${id}/exp-5M-dem 8 128g 1
sh scripts/mlr.sh /N/u/vlabeyko/data/libsvm/mat_5M_300 100 /N/u/vlabeyko/experiments/mlr-spark/${id}/exp-5M-dem 16 128g 1
sh scripts/mlr.sh /N/u/vlabeyko/data/libsvm/mat_5M_300 100 /N/u/vlabeyko/experiments/mlr-spark/${id}/exp-5M-dem 32 128g 1
sh scripts/mlr.sh /N/u/vlabeyko/data/libsvm/mat_5M_300 100 /N/u/vlabeyko/experiments/mlr-spark/${id}/exp-5M-dem 64 128g 1



