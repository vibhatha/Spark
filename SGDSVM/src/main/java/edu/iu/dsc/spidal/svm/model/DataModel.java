package edu.iu.dsc.spidal.svm.model;

public class DataModel {

    private double [] x;

    private double y;

    private double [][] xAll;

    private double [] yAll;


    public DataModel(double[] x, double y) {
        this.x = x;
        this.y = y;
    }

    public DataModel(double[][] x, double[] y) {
        xAll = x;
        yAll = y;
    }

    public double[] getX() {
        return x;
    }

    public void setX(double[] x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double[][] getxAll() {
        return xAll;
    }

    public void setxAll(double[][] xAll) {
        this.xAll = xAll;
    }

    public double[] getyAll() {
        return yAll;
    }

    public void setyAll(double[] yAll) {
        this.yAll = yAll;
    }
}
