package com.pawnua.weathermap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.pawnua.weathermap.model.LocationBox;
import com.pawnua.weathermap.model.Weather;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends GooglePlusActivity implements OnInfoWindowClickListener, OnMarkerClickListener, OnCameraChangeListener {

    //Defining Variables
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    GoogleMap map;
    // Marker marker;
    final String TAG = "NickPeshkovLogs";

    Menu mainmenu;
    Menu navViewMenu;

    SQLiteDatabaseHandler dbCity;

    private static final double KIEVLat = 50.4501;
    private static final double KIEVLng = 30.5234;

    private static final LatLng KIEV = new LatLng(KIEVLat, KIEVLng);
    private static final float KIEVzoom = 5;
    final String ico_prefix = "weather_ic_";

    public static final String APP_PREFERENCES = "mysettings";
    SharedPreferences mSettings;

    private HashMap allMarkersID = new HashMap<String, Marker>();
    private HashMap allMarkers = new HashMap<Marker, Weather>();

    private TextView profile_emailView;
    private TextView profile_usernameView;
    private CircleImageView profile_imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main);

        setSupportActionBar(toolbar);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {

                    case R.id.action_sign_in:
                        signIn();
                        return true;

                    case R.id.action_sign_out:
                        signOut();
                        return true;

                    case R.id.action_openStatistics:
                        openStatistics();
                        return true;

                    case R.id.change_map_type:
                        ChangeMapType();
                        return true;

                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

        navViewMenu = navigationView.getMenu();


        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        profile_emailView = (TextView) findViewById(R.id.email);
        profile_usernameView = (TextView) findViewById(R.id.username);
        profile_imageView = (CircleImageView) findViewById(R.id.profile_image);


        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
        dbCity = new SQLiteDatabaseHandler(this);

    }


    @Override
    protected void onResume() {
        super.onResume();

        addGoogleMap();

    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        this.mainmenu = menu;
        getMenuInflater().inflate(R.menu.main, menu);

        // update Menu elements depands on Google connection
        onConnectionStatusChanged();

        return true;
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        ShowCities();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle() == getResources().getString(R.string.change_map_type)) {
            ChangeMapType();
        }
        else if (item.getTitle() == getResources().getString(R.string.action_openStatistics)) {
            openStatistics();
        }
        else if (item.getItemId() == R.id.action_sign_in) {
            signIn();
        }
        else if (item.getItemId() == R.id.action_sign_out) {
            signOut();
        }

        return super.onOptionsItemSelected(item);
    }

    private void openStatistics() {
        Intent intent = new Intent(this, CityStatisticsActivity.class);
        startActivity(intent);

    }

    private void addGoogleMap() {

       Log.d(TAG, "addGoogleMap()");

        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
       if (map == null) {
           Log.d(TAG, "map == null");

           Toast.makeText(getApplicationContext(),
                   "Error supporting Google Maps", Toast.LENGTH_LONG)
                   .show();
           finish();
           return;
       }

       map.setMyLocationEnabled(true);

       map.setOnInfoWindowClickListener(this);
       map.setOnMarkerClickListener(this);
       map.setOnCameraChangeListener(this);

       map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
           @Override
           public View getInfoWindow(Marker marker) {
               // Getting view from the layout file info_window_layout
               View v = getLayoutInflater().inflate(R.layout.info_window_layout, null);

               // Getting reference to the ImageView to set Temperature
               ImageView iwIcon = (ImageView) v.findViewById(R.id.iwIcon);

               // Getting reference to the TextView to set title
               TextView iwTitle = (TextView) v.findViewById(R.id.iwTitle);

               // Getting reference to the TextView to set Temperature
               TextView iwTemperature = (TextView) v.findViewById(R.id.iwTemperature);

               iwTitle.setText(marker.getTitle());
               iwTemperature.setText(marker.getSnippet());

               Weather curW = (Weather) allMarkers.get(marker);

               int drawableRes = getResources().getIdentifier(ico_prefix + curW.currentCondition.getIcon(), "drawable", getPackageName());
               if (drawableRes > 0) {
                   iwIcon.setImageResource(drawableRes);
               }

               return v;
           }

           @Override
           public View getInfoContents(Marker marker) {
               return null;
           }
       });


       UiSettings mapUI = map.getUiSettings();
       mapUI.setAllGesturesEnabled(true);

       // Load saved data
       mSettings = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);

       double savedLat = mSettings.getFloat("savedLat", (float) KIEVLat);
       double savedLng = mSettings.getFloat("savedLng", (float) KIEVLng);
       float savedZoom = mSettings.getFloat("savedZoom", KIEVzoom);


        CameraPosition cameraPosition = new CameraPosition.Builder()
               .target(new LatLng(savedLat, savedLng))
               .zoom(savedZoom)
               .build();

       CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
       map.animateCamera(cameraUpdate);

    }

    @Override
    protected void onPause() {
        super.onPause();

        // Save data
        CameraPosition cameraPosition =	map.getCameraPosition();

        SharedPreferences.Editor editor = mSettings.edit();
        editor.putFloat("savedZoom", cameraPosition.zoom);
        editor.putFloat("savedLat", (float) cameraPosition.target.latitude);
        editor.putFloat("savedLng", (float) cameraPosition.target.longitude);
        editor.apply();
    }

    private void addMarkers(ArrayList<Weather> weatherList) {
        Log.d(TAG, "addMarkersList()");

        if (map != null) {

            for (int i = 0; i < weatherList.size() ; i++) {

                Weather curW = weatherList.get(i);
                Log.d(TAG, "City = " + curW.location.getCity() + ":"+ curW.location.getId());

                if (allMarkersID.containsKey(curW.location.getId())) continue;

                Marker marker = map.addMarker(new MarkerOptions()
                        .position(new LatLng(curW.location.getLatitude(), curW.location.getLongitude()))
                        .title(curW.location.getCity())
                        .anchor(0.5f, 0.5f)
                        .snippet("" + Math.round((curW.temperature.getTemp())) + "°C"));


                int drawableRes = getResources().getIdentifier("weather_ic_" + curW.currentCondition.getIcon(), "drawable", getPackageName());
                if (drawableRes > 0) {
                  marker.setIcon(BitmapDescriptorFactory.fromResource(drawableRes));
                }


                allMarkersID.put(curW.location.getId(), marker);
                allMarkers.put(marker, curW);

                /*if (curW.iconData != null && curW.iconData.length > 0) {
                    Bitmap img = BitmapFactory.decodeByteArray(curW.iconData, 0, curW.iconData.length);
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(img));
                }*/

            }

            if (allMarkersID.size() > 20 ) {
                // need clever cleaning old markers
//            map.clear();
                // marker.remove();
            }


        }
    }

    public void ChangeMapType() {
        int curMapType = map.getMapType();
        if (curMapType == GoogleMap.MAP_TYPE_NORMAL) {
            map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            Log.d(TAG, "MAP_TYPE_SATELLITE");
        }
        else if (curMapType == GoogleMap.MAP_TYPE_SATELLITE){
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    Log.d(TAG, "MAP_TYPE_TERRAIN");
        }
        else if (curMapType == GoogleMap.MAP_TYPE_TERRAIN){
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    Log.d(TAG, "MAP_TYPE_HYBRID");
        }
        else {
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    Log.d(TAG, "MAP_TYPE_NORMAL");
}
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.d(TAG, "onInfoWindowClick()");

//        marker.hideInfoWindow();
        Intent intent = new Intent(this, WeatherInfoActivity.class);
        LatLng position = marker.getPosition();
        intent.putExtra("lat", position.latitude);
        intent.putExtra("lng", position.longitude);
        intent.putExtra("title", marker.getTitle());

        startActivity(intent);

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        Log.d(TAG, "onMarkerClick()");

        // add info to DB
        dbCity.increaseCityViewAmount(marker.getTitle(), 1);

        return false;
    }

    public void ShowCities() {
        if (map == null) {
            Toast.makeText(getApplicationContext(),
                    "Wait and try again", Toast.LENGTH_LONG)
                    .show();
            return;
        }

        VisibleRegion visibleRegion = map.getProjection().getVisibleRegion();
        CameraPosition cameraPosition =	map.getCameraPosition();

        LocationBox lBox = new LocationBox(visibleRegion.latLngBounds.northeast.longitude, visibleRegion.latLngBounds.northeast.latitude, visibleRegion.latLngBounds.southwest.longitude, visibleRegion.latLngBounds.southwest.latitude, (int) cameraPosition.zoom + 1);
        Log.d(TAG, "lBox (LatLngBounds) = " + lBox.toString());

        JSONGetCitiesTask task = new JSONGetCitiesTask();
//        task.execute(new String[]{city});
        task.execute(new LocationBox[] {lBox});
    }

    private class JSONGetCitiesTask extends AsyncTask<LocationBox, Void, ArrayList<Weather>> {

        @Override
        protected ArrayList<Weather> doInBackground(LocationBox... params) {
            ArrayList<Weather> weatherList = new ArrayList<Weather>();
            String data = ( (new WeatherHttpClient()).getCitiesInBox(params[0]));

            if (data == null) return weatherList;

            try {
                weatherList = JSONWeatherParser.getCities(data);
/*                for (int i = 0; i < weatherList.size() ; i++) {

                    // Let's retrieve the icon
                    weatherList.get(i).iconData = ( (new WeatherHttpClient()).getImage(weatherList.get(i).currentCondition.getIcon()));
                }
*/

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return weatherList;
        }

        @Override
        protected void onPostExecute(ArrayList<Weather> weathers) {
            super.onPostExecute(weathers);

            addMarkers(weathers);
        }
    }

    @Override
    public void onConnectionStatusChanged() {
        Log.d(TAG, "onConnectionStatusChanged()");
        if (mainmenu != null) {
            if (isSignIn == true) {
                MenuItem menu_sign_in = mainmenu.findItem(R.id.action_sign_in);
                menu_sign_in.setVisible(false);
                MenuItem menu_sign_out = mainmenu.findItem(R.id.action_sign_out);
                menu_sign_out.setVisible(true);

            }
            else{
                MenuItem menu_sign_in = mainmenu.findItem(R.id.action_sign_in);
                menu_sign_in.setVisible(true);
                MenuItem menu_sign_out = mainmenu.findItem(R.id.action_sign_out);
                menu_sign_out.setVisible(false);

            }

        }

//        navViewMenu
        if (navViewMenu != null) {
            if (isSignIn == true) {
                MenuItem menu_sign_in = navViewMenu.findItem(R.id.action_sign_in);
                menu_sign_in.setVisible(false);
                MenuItem menu_sign_out = navViewMenu.findItem(R.id.action_sign_out);
                menu_sign_out.setVisible(true);

                if (mPlusClient!=null) {
                    profile_emailView.setText(mPlusClient.getAccountName());
                    if (mPlusClient.getCurrentPerson()!=null) {
                        profile_usernameView.setText(mPlusClient.getCurrentPerson().getDisplayName());
                        if (mDrawable!=null) {
                            profile_imageView.setImageDrawable(mDrawable);
                        }
                        else {
                            profile_imageView.setImageResource(R.drawable.profile_blank);
                        }
                    }
                    else{
                        profile_imageView.setImageResource(R.drawable.profile_blank);
                        profile_usernameView.setText("");
                    }

                }
            }
            else{
                MenuItem menu_sign_in = navViewMenu.findItem(R.id.action_sign_in);
                menu_sign_in.setVisible(true);
                MenuItem menu_sign_out = navViewMenu.findItem(R.id.action_sign_out);
                menu_sign_out.setVisible(false);

                profile_emailView.setText("");
                profile_usernameView.setText(R.string.no_user);
                profile_imageView.setImageResource(R.drawable.profile_blank);

            }

        }

    }

    @Override
    public void onActivityResultExtended(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onCreateExtended(Bundle savedInstanceState) {

    }

    @Override
    public void getUserInformation() {

    }

}