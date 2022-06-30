package com.builderlinebr.smarttrainer.calculation;

public class CalcMap {

    double eQuatorialEarthRadius = 6378.1370D; // радиус землм
    double d2r = (Math.PI / 180D);

    public int getDistanceInM(double lat1, double long1, double lat2, double long2)
    {
        return (int)(1000D * getDistanceInKm(lat1, long1, lat2, long2));
    }

    public double getDistanceInKm(double lat1, double long1, double lat2, double long2){


        double dlong = (long2 - long1) * d2r;
        double dlat = (lat2 - lat1) * d2r;
        double a = Math.pow(Math.sin(dlat / 2D), 2D) + Math.cos(lat1 * d2r) * Math.cos(lat2 * d2r) * Math.pow(Math.sin(dlong / 2D), 2D);
        double c = 2D * Math.atan2(Math.sqrt(a), Math.sqrt(1D - a));
        double result = eQuatorialEarthRadius * c;


        return result;
    }



    public double calcAzimuth(double lat1, double long1, double lat2, double long2) {

        double a = Math.atan2(long2 - long1, lat2 - lat1) * 180 / Math.PI;
        if (a < 0) a += 360;
        return a;
    }

}
