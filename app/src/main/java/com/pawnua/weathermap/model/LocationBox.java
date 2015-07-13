package com.pawnua.weathermap.model;

import java.io.Serializable;

/**
 * Created by Nick on 05.11.2014.
 */
public class LocationBox implements Serializable{

    private double longitudeTop;
    private double latitudeTop;
    private double longitudeBottom;
    private double latitudeBottom;
    private int mapZoom;

    public double getLongitudeTop() {
        return longitudeTop;
    }
    public double getLongitudeBottom() {
        return longitudeBottom;
    }

    public double getLatitudeTop() {
        return latitudeTop;
    }
    public double getLatitudeBottom() {
        return latitudeBottom;
    }

    public int getMapZoom() {
        return mapZoom;
    }

    public void setLongitudeTop(double longitudeTop) {
        this.longitudeTop = longitudeTop;
    }
    public void setLongitudeBottom(double longitudeBottom) {
        this.longitudeBottom = longitudeBottom;
    }

    public void setLatitudeTop(double latitudeTop) {
        this.latitudeTop = latitudeTop;
    }
    public void setLatitudeBottom(double latitudeBottom) {
        this.latitudeBottom = latitudeBottom;
    }

    public void setMapZoom(int mapZoom) {
        this.mapZoom = mapZoom;
    }

    public LocationBox(double longitudeTop, double latitudeTop, double longitudeBottom, double latitudeBottom, int mapZoom) {
        this.longitudeTop = longitudeTop;
        this.latitudeTop = latitudeTop;
        this.longitudeBottom = longitudeBottom;
        this.latitudeBottom = latitudeBottom;
        this.mapZoom = mapZoom;
    }

    @Override
    public String toString() {
        return "bbox=" + getLongitudeTop() + "," + getLatitudeTop() + "," + getLongitudeBottom() + "," + getLatitudeBottom() + "," + getMapZoom();
    }
}
