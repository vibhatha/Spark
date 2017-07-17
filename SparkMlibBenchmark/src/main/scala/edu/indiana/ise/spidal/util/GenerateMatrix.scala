package edu.indiana.ise.spidal.util

/**
  * Created by vibhatha on 7/17/17.
  */
import scala.util.Random
import java.io._

object GenerateMatrix {

  def main(args: Array[String]){

    if(args.length!=3){
      println("Wrong input arguments");
      println("<rows> <columns> <outputfile>")
    }else{
      val rows = args(0).toInt
      val columns = args(1).toInt
      val filename = args(2)
      generateSequenceAndSave(rows, columns, filename)
    }

  }

  def saveFile(filename : String, dataset : String){
      println("Saving file..."+filename)
      try {
        val file = new File(filename)
        file.createNewFile();
        val bw = new BufferedWriter(new FileWriter(file))
        bw.write(dataset)
        bw.close()
        println("File Saved")
      }catch {
        case e : IOException => e.getMessage
        e.printStackTrace()
      }

  }

  def generateSequenceAndSave(rows : Int, columns : Int, filename: String){
      println("Rows : "+ rows+", Columns : "+columns+" Filename : "+filename)
      var matrixString = ""

     for(i <- 0 to rows-1){
       var index = i
       var line = ""
        for(j <- 0 to columns-1){
            val num= Random.nextDouble()
            line+=num.toString+" "
        }
        val matrixIndex = index.toString
        line = matrixIndex+"," + line
        matrixString+=line
        matrixString+="\n"
      }
      println(matrixString)
      WriteText.saveFile(filename, matrixString)

  }

}
