package com.pawnua.weathermap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.pawnua.weathermap.model.CityAmount;

import java.util.ArrayList;

/**
 * Created by Nick on 13.11.2014.
 */
public class CityStatisticsActivity extends AppCompatActivity {

    SQLiteDatabaseHandler dbCity;
    ArrayList<CityAmount> city_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle(R.string.cityStatistics);

        dbCity = new SQLiteDatabaseHandler(this);
        city_list = dbCity.Get_AllCities();

        CityStatisticsAdapter adapter = new CityStatisticsAdapter(city_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.statisticRecyclerView);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);

    }
}
