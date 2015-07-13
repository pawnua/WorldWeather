package com.pawnua.weathermap;

import org.json.JSONException;

import com.pawnua.weathermap.model.TemperatureDaily;
import com.pawnua.weathermap.model.TemperatureHistory;
import com.pawnua.weathermap.model.Weather;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


/**
 * Created by nickpeshkov on 03.11.2014.
 */
public class WeatherInfoActivity extends AppCompatActivity {

    private TextView cityText;
    private TextView condDescr;
    private TextView temp;
    private TextView press;
    private TextView windSpeed;
    private TextView windDeg;
    private TextView hum;
    private ImageView imgView;
    private String city;

    final String TAG = "NickPeshkovLogs";

    final String ico_prefix = "weather_ic_";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initTabHost();

        initGetData();

        getSupportActionBar().setTitle(city);

    }

    private void initGetData() {
        double lat = getIntent().getDoubleExtra("lat", 0);
        double lng = getIntent().getDoubleExtra("lng", 0);

        city = getIntent().getStringExtra("title");

        cityText = (TextView) findViewById(R.id.cityText);
        condDescr = (TextView) findViewById(R.id.condDescr);
        temp = (TextView) findViewById(R.id.temp);
        hum = (TextView) findViewById(R.id.hum);
        press = (TextView) findViewById(R.id.press);
        windSpeed = (TextView) findViewById(R.id.windSpeed);
        windDeg = (TextView) findViewById(R.id.windDeg);
        imgView = (ImageView) findViewById(R.id.condIcon);

        JSONWeatherTask task = new JSONWeatherTask();
        task.execute(new String[]{city});

        JSONWeatherForecastTask taskForecast = new JSONWeatherForecastTask();
        taskForecast.execute(new String[]{city});

        JSONWeatherHistoryTask taskHistory = new JSONWeatherHistoryTask();
        taskHistory.execute(new String[]{city});

//        task.execute(new Double[]{lat, lng});
    }

    private void initTabHost() {
        TabHost tabs = (TabHost) findViewById(android.R.id.tabhost);

        tabs.setup();

        TabHost.TabSpec spec = tabs.newTabSpec("tag1");

        spec.setContent(R.id.tab1);
        spec.setIndicator("Forecast");
        tabs.addTab(spec);

        spec = tabs.newTabSpec("tag2");
        spec.setContent(R.id.tab2);
        spec.setIndicator("History");
        tabs.addTab(spec);

/*        spec = tabs.newTabSpec("tag3");
        spec.setContent(R.id.tab3);
        spec.setIndicator("...");
        tabs.addTab(spec);
*/

        tabs.setCurrentTabByTag("tag1");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu; this adds items to the action bar if it is present.
//      getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private class JSONWeatherTask extends AsyncTask<String, Void, Weather> {

        @Override
        protected Weather doInBackground(String... params) {
            Weather weather = new Weather();
            String data = ( (new WeatherHttpClient()).getWeatherData(params[0]));

            if (data == null) return weather;

            try {
                weather = JSONWeatherParser.getWeather(data);
                // Let's retrieve the icon
                weather.iconData = ( (new WeatherHttpClient()).getImage(weather.currentCondition.getIcon()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return weather;
        }
        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);

            int drawableRes = getResources().getIdentifier(ico_prefix + weather.currentCondition.getIcon(), "drawable", getPackageName());
            if (drawableRes > 0) {
                imgView.setImageResource(drawableRes);
            }

            cityText.setText(weather.location.getCity() + ", " + weather.location.getCountry());
            condDescr.setText(weather.currentCondition.getCondition() + " (" + weather.currentCondition.getDescr() + ")");
            temp.setText("" + Math.round((weather.temperature.getTemp() - 273.15)) + "°C");
            hum.setText("" + weather.currentCondition.getHumidity() + "%");
            press.setText("" + weather.currentCondition.getPressure() + " hPa");
            windSpeed.setText("" + weather.wind.getSpeed() + " m/s");
            windDeg.setText("" + weather.wind.getDeg() + "°");

        }
    }

    private class JSONWeatherForecastTask extends AsyncTask<String, Void, Weather> {

        int drawableRes;

        LayoutInflater ltInflater = getLayoutInflater();
        LinearLayout linLayout = (LinearLayout) findViewById(R.id.tab1);
        TableLayout tabForecast = (TableLayout) findViewById(R.id.tableForecast);
        TableRow tableRow;

        View viewForecast;
        TextView forecastTemp;
        TextView forecastDay;
        ImageView forecastIcon;

        @Override
        protected Weather doInBackground(String... params) {
            Weather weather = new Weather();
            String data = ( (new WeatherHttpClient()).getWeatherForecastData(params[0]));

            if (data == null) return weather;

            try {
                weather = JSONWeatherParser.getWeatherForecast(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return weather;
        }

        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);

            tableRow = new TableRow(getApplicationContext());
            tableRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));


            for (int i = 0; i < weather.temperatureDailyList.size(); i++){
                addForecastView(weather, i);
                if (i == 4) break;
            }

            tabForecast.addView(tableRow, 0);

        }

        private void addForecastView(Weather weather, int i) {

            viewForecast = ltInflater.inflate(R.layout.item_wforecast, tableRow, false);
            forecastTemp = (TextView) viewForecast.findViewById(R.id.forecastTemp);
            forecastDay = (TextView) viewForecast.findViewById(R.id.forecastDay);
            forecastIcon = (ImageView) viewForecast.findViewById(R.id.forecastIcon);

            TemperatureDaily forecast = weather.temperatureDailyList.get(i);

            forecastDay.setText(new java.text.SimpleDateFormat("dd/MM").format(forecast.curDate));

            forecastTemp.setText("" + Math.round((forecast.getDayTemp() - 273.15)) + "°C");

            drawableRes = getResources().getIdentifier(ico_prefix + forecast.currentCondition.getIcon(), "drawable", getPackageName());
            if (drawableRes > 0) {
                forecastIcon.setImageResource(drawableRes);
            }

            tableRow.addView(viewForecast);
        }
    }

    private class JSONWeatherHistoryTask extends AsyncTask<String, Void, Weather> {

        int drawableRes;

        LayoutInflater ltInflater = getLayoutInflater();
        TableLayout tabHistory = (TableLayout) findViewById(R.id.tableHistory);
        TableRow tableRow;

        View viewHistory;
        TextView historyTemp;
        TextView historyDay;
        ImageView historyIcon;

        @Override
        protected Weather doInBackground(String... params) {
            Weather weather = new Weather();
            String data = ( (new WeatherHttpClient()).getWeatherHistoryData(params[0]));

            if (data == null) return weather;

            try {
                weather = JSONWeatherParser.getWeatherHistory(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return weather;
        }
        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);

            tableRow = new TableRow(getApplicationContext());
            tableRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));

            for (int i = 0; i < weather.temperatureHistoryList.size(); i++){
                addHistoryView(weather, i);
                if (i == 5) break;
            }

            tabHistory.addView(tableRow, 0);

        }

        private void addHistoryView(Weather weather, int i) {

            viewHistory = ltInflater.inflate(R.layout.item_whistory, tableRow, false);
            historyTemp = (TextView) viewHistory.findViewById(R.id.historyTemp);
            historyDay = (TextView) viewHistory.findViewById(R.id.historyDay);
            historyIcon = (ImageView) viewHistory.findViewById(R.id.historyIcon);

            TemperatureHistory history = weather.temperatureHistoryList.get(i);

            historyDay.setText(new java.text.SimpleDateFormat("dd/MM HH:mm").format(history.curDate));

            historyTemp.setText("" + Math.round((history.temperature.getTemp() - 273.15)) + "°C");

            drawableRes = getResources().getIdentifier(ico_prefix + history.currentCondition.getIcon(), "drawable", getPackageName());
            if (drawableRes > 0) {
                historyIcon.setImageResource(drawableRes);
            }

            tableRow.addView(viewHistory);
        }

    }

}
