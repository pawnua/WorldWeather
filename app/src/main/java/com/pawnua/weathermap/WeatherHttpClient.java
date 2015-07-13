package com.pawnua.weathermap;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import com.pawnua.weathermap.model.LocationBox;

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
public class WeatherHttpClient {
    private static String BASE_URL = "http://api.openweathermap.org/data/2.5/weather";
    private static String BASE_FORECAST_URL = "http://api.openweathermap.org/data/2.5/forecast/daily";
    private static String BASE_HISTORY_URL = "http://api.openweathermap.org/data/2.5/history";
    private static String BOX_CITY_URL = "http://api.openweathermap.org/data/2.5/box/city";
    private static String BASE_URL_FIND = "http://api.openweathermap.org/data/2.5/find";
    private static String IMG_URL = "http://openweathermap.org/img/w/";

    private static String APPID = "&APPID=24c6937be5e15060f04d010931e1b808";

    public String getOpenWeatherData(URL url) {
        HttpURLConnection con = null ;
        InputStream is = null;
        try {
            con = (HttpURLConnection) (url).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();
// Let's read the response
            StringBuffer buffer = new StringBuffer();
            is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ( (line = br.readLine()) != null )
                buffer.append(line + "\r\n");
            is.close();
            con.disconnect();
            return buffer.toString();
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
        finally {
            try { is.close(); } catch(Throwable t) {}
            try { con.disconnect(); } catch(Throwable t) {}
        }
        return null;
    }



    public String getWeatherData(String location) {

        try {
            URL url = new URL(BASE_URL + "?q=" + location + APPID);
            return getOpenWeatherData(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;

    }

    public String getWeatherData(Double lat, Double lng) {

        try {
            URL url = new URL(BASE_URL + "?lat=" + lat + "&lon=" + lng + APPID);
            return getOpenWeatherData(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;

    }

    public String getCitiesInBox(LocationBox locationBox) {

        try {
            URL url = new URL(BOX_CITY_URL + "?" + locationBox.toString() + APPID);
            return getOpenWeatherData(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;

    }

    public String getWeatherForecastData(String location) {

        // http://api.openweathermap.org/data/2.5/forecast/daily?q=Kiev
        try {
            URL url = new URL(BASE_FORECAST_URL + "?q=" + location + APPID);
            return getOpenWeatherData(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;

    }

    public String getWeatherForecastData(int id) {

        // http://api.openweathermap.org/data/2.5/forecast/daily?id=703448
        try {
            URL url = new URL(BASE_URL + "?id=" + id + APPID);
            return getOpenWeatherData(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;

    }

    public String getWeatherHistoryData(String location) {

        // http://api.openweathermap.org/data/2.5/history/city?q=Kiev
        try {
            URL url = new URL(BASE_HISTORY_URL + "/city?q=" + location + "&cnt=5&type=day" + APPID);
            return getOpenWeatherData(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;

    }

    public byte[] getImage(String code) {
        HttpURLConnection con = null ;
        InputStream is = null;
        try {
            con = (HttpURLConnection) ( new URL(IMG_URL + code + ".png")).openConnection();
//            con = (HttpURLConnection) ( new URL(IMG_URL + code)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
//            con.setDoOutput(true);
            con.connect();
// Let's read the response
            is = con.getInputStream();
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ( is.read(buffer) != -1)
                baos.write(buffer);
            return baos.toByteArray();
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
        finally {
            try { is.close(); } catch(Throwable t) {}
            try { con.disconnect(); } catch(Throwable t) {}
        }
        return null;
    }
}