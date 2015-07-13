package com.pawnua.weathermap.model;

import java.util.Date;

public class TemperatureDaily {

    public WeatherCurrentCondition currentCondition = new WeatherCurrentCondition();
    private float dayTemp;
    private float nightTemp;
    private float eveTemp;
    private float mornTemp;
    private float minTemp;
    private float maxTemp;

    public Date curDate;

    public float getDayTemp() {
        return dayTemp;
    }
    public void setDayTemp(float dayTemp) {
        this.dayTemp = dayTemp;
    }

    public float getNightTemp() {
        return nightTemp;
    }
    public void setNightTemp(float nightTemp) {
        this.nightTemp = nightTemp;
    }

    public float getEveTemp() {
        return eveTemp;
    }
    public void setEveTemp(float eveTemp) {
        this.eveTemp = eveTemp;
    }

    public float getMornTemp() {
        return mornTemp;
    }
    public void setMornTemp(float mornTemp) {
        this.mornTemp = mornTemp;
    }

    public float getMinTemp() {
        return minTemp;
    }
    public void setMinTemp(float minTemp) {
        this.minTemp = minTemp;
    }

    public float getMaxTemp() {
        return maxTemp;
    }
    public void setMaxTemp(float maxTemp) {
        this.maxTemp = maxTemp;
    }

}
