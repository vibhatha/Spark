package edu.indiana.ise.spidal.util

import java.io.{File, FileWriter, IOException}

import scala.util.Random

/**
  * Created by vibhatha on 7/31/17.
  */
object LibSVMGenerator {

  def main(args: Array[String]): Unit = {

    if(args.length !=4){

      println("Arguments : sh gen_svm.sh <classes> <samples> <features> <output>")
      println("Example : sh gen_svm.sh 2 1000 20 /output/libsvm1")


    }else{

      /*
      * 0 1:3 2:0 3:0
        1 1:0 3:1 3:1
        2 1:1 2:3 3:1
      * */

      val classes = args(0).toInt
      val samples = args(1).toInt
      val features = args(2).toInt
      val output = args(3)
      generateSequenceAndSave1(classes, samples, features, output)



  }



  }

  def generateSequenceAndSave1(classes : Int,rows : Int, columns : Int, filename: String){
    println("Rows : "+ rows+", Columns : "+columns+" Filename : "+filename)
    var matrixString = ""
    try {
      val file = new File(filename)
      file.createNewFile();
      for(i <- 0 to rows-1){
        matrixString = ""
        val r = new scala.util.Random
        var index = r.nextInt(classes)
        var line = ""
        for(j <- 0 to columns-1){
          val num= Random.nextDouble()
          line+=(j+1).toString+":"+num.toString+" "
        }
        val matrixIndex = index.toString
        line = matrixIndex+" " + line
        matrixString+=line
        matrixString+="\n"
        val fw = (new FileWriter(file,true))
        fw.write(matrixString)
        fw.close()
      }

    }catch {
      case e : IOException => e.getMessage
        e.printStackTrace()
    }
  }

}
