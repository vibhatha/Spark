package edu.indiana.ise.spidal.util

/**
  * Created by vibhatha on 7/16/17.
  */

import java.io._

object WriteText {

  def saveFile(fileName : String, dataset : String){
    println("Saving file..."+fileName)
    try {
      val file = new File(fileName)
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

}
