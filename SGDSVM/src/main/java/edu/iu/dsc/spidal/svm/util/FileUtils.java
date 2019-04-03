package edu.iu.dsc.spidal.svm.util;

import java.io.File;

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
}
