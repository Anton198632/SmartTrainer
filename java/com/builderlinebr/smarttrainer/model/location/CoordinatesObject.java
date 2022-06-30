package com.builderlinebr.smarttrainer.model.location;

import java.util.List;

public class CoordinatesObject {

    private double[] preCoordinates;
    List<double[]> coordinateList;

    public double[] getPreCoordinates() {
        return preCoordinates;
    }

    public List<double[]> getCoordinateList() {
        return coordinateList;
    }

    public CoordinatesObject(double[] preCoordinates, List<double[]> coordinateList) {

        this.preCoordinates = preCoordinates;
        this.coordinateList = coordinateList;
    }
}
