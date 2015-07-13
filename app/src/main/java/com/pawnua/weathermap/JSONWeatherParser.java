package com.pawnua.weathermap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.pawnua.weathermap.model.Location;
import com.pawnua.weathermap.model.TemperatureDaily;
import com.pawnua.weathermap.model.TemperatureHistory;
import com.pawnua.weathermap.model.Weather;

import java.util.ArrayList;
import java.util.Date;

/*
* Copyright (C) 2013 Surviving with Android (http://www.survivingwithandroid.com)
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
public class JSONWeatherParser {
    public static Weather getWeather(String data) throws JSONException {
        Weather weather = new Weather();
// We create out JSONObject from the data
        JSONObject jObj = new JSONObject(data);

// We start extracting the info
        Location loc = new Location();
        JSONObject coordObj = getObject("coord", jObj);
        loc.setLatitude(getFloat("lat", coordObj));
        loc.setLongitude(getFloat("lon", coordObj));
        JSONObject sysObj = getObject("sys", jObj);
        loc.setCountry(getString("country", sysObj));
        loc.setSunrise(getInt("sunrise", sysObj));
        loc.setSunset(getInt("sunset", sysObj));
        loc.setCity(getString("name", jObj));
        weather.location = loc;
// We get weather info (This is an array)
        JSONArray jArr = jObj.getJSONArray("weather");
// We use only the first value
        if (jArr.length() > 0) {
            JSONObject JSONWeather = jArr.getJSONObject(0);
            weather.currentCondition.setWeatherId(getInt("id", JSONWeather));
            weather.currentCondition.setDescr(getString("description", JSONWeather));
            weather.currentCondition.setCondition(getString("main", JSONWeather));
            weather.currentCondition.setIcon(getString("icon", JSONWeather));
        }
        JSONObject mainObj = getObject("main", jObj);
        weather.currentCondition.setHumidity(getInt("humidity", mainObj));
        weather.currentCondition.setPressure(getInt("pressure", mainObj));
        weather.temperature.setMaxTemp(getFloat("temp_max", mainObj));
        weather.temperature.setMinTemp(getFloat("temp_min", mainObj));
        weather.temperature.setTemp(getFloat("temp", mainObj));
// Wind
        JSONObject wObj = getObject("wind", jObj);
        if (wObj.has("speed")) {
            weather.wind.setSpeed(getFloat("speed", wObj));
        }
        if (wObj.has("deg")) {
            weather.wind.setDeg(getFloat("deg", wObj));
        }
// Clouds
        JSONObject cObj = getObject("clouds", jObj);
        weather.clouds.setPerc(getInt("all", cObj));
// We download the icon to show
        return weather;
    }

    public static Weather getWeatherForecast(String data) throws JSONException {
        Weather weather = new Weather();
// We create out JSONObject from the data
        JSONObject jObj = new JSONObject(data);

// We start extracting the info
        Location loc = new Location();

        JSONObject cityObj = getObject("city", jObj);

        loc.setCity(getString("name", cityObj));
        loc.setId(getString("id", cityObj));

        JSONObject coordObj = getObject("coord", cityObj);
        loc.setLatitude(getFloat("lat", coordObj));
        loc.setLongitude(getFloat("lon", coordObj));

        weather.location = loc;


// We get weather info (This is an array)
        JSONArray jArr = jObj.getJSONArray("list");
        JSONObject JSONList = new JSONObject();

        for (int i = 0; i < jArr.length() ; i++) {

            JSONList = jArr.getJSONObject(i);


            JSONObject tempObj = getObject("temp", JSONList);

            TemperatureDaily wTemp = new TemperatureDaily();

            wTemp.setDayTemp(getFloat("day", tempObj));
            wTemp.setNightTemp(getFloat("night", tempObj));
            wTemp.setEveTemp(getFloat("eve", tempObj));
            wTemp.setMornTemp(getFloat("morn", tempObj));
            wTemp.setMinTemp(getFloat("min", tempObj));
            wTemp.setMaxTemp(getFloat("max", tempObj));

            int dt = getInt("dt", JSONList);


//            Date curdate = new java.util.Date(dt*1000);
            Date curdate = new Date((long) dt*1000);
            String curDateString = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(curdate);
            wTemp.curDate = curdate;


// We get weather info (This is an array)
            JSONArray jArrWeather = JSONList.getJSONArray("weather");
// We use only the first value
            if (jArrWeather.length() > 0) {
                JSONObject JSONWeather = jArrWeather.getJSONObject(0);
                wTemp.currentCondition.setWeatherId(getInt("id", JSONWeather));
                wTemp.currentCondition.setDescr(getString("description", JSONWeather));
                wTemp.currentCondition.setCondition(getString("main", JSONWeather));
                wTemp.currentCondition.setIcon(getString("icon", JSONWeather));
            }

            wTemp.currentCondition.setHumidity(getInt("humidity", JSONList));
            wTemp.currentCondition.setPressure(getInt("pressure", JSONList));

            weather.temperatureDailyList.add(wTemp);

        }



        return weather;
    }

    public static Weather getWeatherHistory(String data) throws JSONException {
        Weather weather = new Weather();
// We create out JSONObject from the data
        JSONObject jObj = new JSONObject(data);

// We get weather info (This is an array)
        JSONArray jArr = jObj.getJSONArray("list");
        JSONObject JSONList = new JSONObject();

        for (int i = 0; i < jArr.length() ; i++) {

            JSONList = jArr.getJSONObject(i);


            JSONObject mainObj = getObject("main", JSONList);

            TemperatureHistory wTemp = new TemperatureHistory();

            wTemp.temperature.setTemp(getFloat("temp", mainObj));
            wTemp.temperature.setMinTemp(getFloat("temp_min", mainObj));
            wTemp.temperature.setMaxTemp(getFloat("temp_max", mainObj));

            wTemp.currentCondition.setHumidity(getInt("humidity", mainObj));
            wTemp.currentCondition.setPressure(getInt("pressure", mainObj));

            int dt = getInt("dt", JSONList);

//            Date curdate = new java.util.Date(dt*1000);
            Date curdate = new Date((long) dt*1000);
            String curDateString = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(curdate);
            wTemp.curDate = curdate;


// We get weather info (This is an array)
            JSONArray jArrWeather = JSONList.getJSONArray("weather");
// We use only the first value
            if (jArrWeather.length() > 0) {
                JSONObject JSONWeather = jArrWeather.getJSONObject(0);
                wTemp.currentCondition.setWeatherId(getInt("id", JSONWeather));
                wTemp.currentCondition.setDescr(getString("description", JSONWeather));
                wTemp.currentCondition.setCondition(getString("main", JSONWeather));
                wTemp.currentCondition.setIcon(getString("icon", JSONWeather));
            }

            weather.temperatureHistoryList.add(wTemp);
        }

        return weather;
    }

    public static ArrayList<Weather> getCities(String data) throws JSONException {

        ArrayList<Weather> weatherList = new ArrayList<Weather>();

        // We create out JSONObject from the data
        JSONObject jObj = new JSONObject(data);

/*        // Check amount
        int cnt = getInt("cnt", jObj);
        if (cnt == 0){
            return weatherList;
        }
*/

        // We start extracting the info
        JSONArray jArr = jObj.getJSONArray("list");

        JSONObject JSONList = new JSONObject();

        for (int i = 0; i < jArr.length() ; i++) {

            Weather weather = new Weather();

            JSONList = jArr.getJSONObject(i);

            Location loc = new Location();
            JSONObject coordObj = getObject("coord", JSONList);
            loc.setLatitude(getFloat("lat", coordObj));
            loc.setLongitude(getFloat("lon", coordObj));

            loc.setCity(getString("name", JSONList));
            loc.setId(getString("id", JSONList));
//            loc.setId(getInt("id", JSONList));

            weather.location = loc;

// We get weather info (This is an array)
            JSONArray jArrWeather = JSONList.getJSONArray("weather");
// We use only the first value
            if (jArrWeather.length() > 0) {
                JSONObject JSONWeather = jArrWeather.getJSONObject(0);
                weather.currentCondition.setWeatherId(getInt("id", JSONWeather));
                weather.currentCondition.setDescr(getString("description", JSONWeather));
                weather.currentCondition.setCondition(getString("main", JSONWeather));
                weather.currentCondition.setIcon(getString("icon", JSONWeather));
            }
            JSONObject mainObj = getObject("main", JSONList);
            weather.currentCondition.setHumidity(getInt("humidity", mainObj));
            weather.currentCondition.setPressure(getInt("pressure", mainObj));
            weather.temperature.setMaxTemp(getFloat("temp_max", mainObj));
            weather.temperature.setMinTemp(getFloat("temp_min", mainObj));
            weather.temperature.setTemp(getFloat("temp", mainObj));
// Wind
            JSONObject wObj = getObject("wind", JSONList);
            weather.wind.setSpeed(getFloat("speed", wObj));
            weather.wind.setDeg(getFloat("deg", wObj));
// Clouds
            JSONObject cObj = getObject("clouds", JSONList);
            weather.clouds.setPerc(getInt("all", cObj));


            weatherList.add(weather);
        }

        return weatherList;
    }

    private static JSONObject getObject(String tagName, JSONObject jObj) throws JSONException {
        JSONObject subObj = jObj.getJSONObject(tagName);
        return subObj;
    }
    private static String getString(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getString(tagName);
    }
    private static float getFloat(String tagName, JSONObject jObj) throws JSONException {
        return (float) jObj.getDouble(tagName);
    }
    private static int getInt(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getInt(tagName);
    }
}
