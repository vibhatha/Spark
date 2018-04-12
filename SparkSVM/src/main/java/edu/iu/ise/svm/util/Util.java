package edu.iu.ise.svm.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by vibhatha on 4/11/18.
 */
public class Util {

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

    public static void appendLogs(String filename, String log){
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {

            String data = log;

            File file = new File(filename);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            // true = append file
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);

            bw.write(data+"\n");

            System.out.println("Log Written : "+filename);

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (bw != null)
                    bw.close();

                if (fw != null)
                    fw.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }
        }

    }
}
