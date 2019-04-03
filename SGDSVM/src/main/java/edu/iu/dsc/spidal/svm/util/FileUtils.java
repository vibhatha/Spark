package edu.iu.dsc.spidal.svm.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public final class FileUtils {

    private FileUtils() {

    }

    public static boolean mkdir(String folderpath){
        boolean status = true;

        File file = new File(folderpath);

        if(file.exists()){
            status = false;
        }else{
            file.mkdir();
            System.out.println("Mkdir: "+folderpath);
        }

        return status;
    }

    public static void logSave(String filePath, int features, int samples, int parallelism, double dataLoadingTime, double trainingTime) throws IOException {
        String s = "";
        s = String.format("%d,%d,%d,%f,%f", parallelism, samples, features, dataLoadingTime, trainingTime);
        File file = new File(filePath);
        FileWriter fileWriter = new FileWriter(file, true);
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(s);
            bufferedWriter.newLine();
        } finally {
            bufferedWriter.close();
        }
    }
}
