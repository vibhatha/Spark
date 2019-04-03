package edu.iu.dsc.spidal.svm.util;

import edu.iu.dsc.spidal.svm.model.DataModel;

public final class DataUtils {

    private DataUtils() { }

    public static DataModel getDataModel(double [] xy) {
        DataModel dataModel = null;
        double y = xy[0];
        double [] x = new double[xy.length-1];
        for (int i = 0; i < xy.length-1; i++) {
            x[i+1] = xy[i];
        }
        dataModel = new DataModel(x, y);
        return dataModel;
    }

    public static DataModel getDataModel1(double [][] xy) {
        DataModel dataModel = null;
        double [][] x = new double [xy.length][xy[0].length];
        double [] y = new double[xy.length];
        for (int i = 0; i < xy.length; i++) {
            y[i] = xy[i][0];
            for (int j = 1; j < xy[0].length; j++) {
                x[i][j-1] = xy[i][j];
            }
        }
        dataModel = new DataModel(x, y);
        return dataModel;
    }
}
