Id=$1
#sh scripts/exp.sh mat_100K_300F 1 ${Id}
sh scripts/exp.sh mat_5M_300 1 ${Id}
sh scripts/exp.sh mat_5M_300 8 ${Id}
sh scripts/exp.sh mat_5M_300 16 ${Id}
sh scripts/exp.sh mat_5M_300 32 ${Id}
sh scripts/exp.sh mat_5M_300 64 ${Id}
