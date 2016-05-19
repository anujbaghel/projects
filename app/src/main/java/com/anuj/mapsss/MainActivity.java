package com.anuj.mapsss;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,OnMapReadyCallback,GoogleApiClient.OnConnectionFailedListener {
    private Location mlocation;
    private GoogleApiClient apiClient;
    private LocationRequest locationRequest;
    private boolean requestupdates = false;
    private Button btnShowLocation;
    private EditText sea;
    double latitude;
    double longitude;
    double loc1, loc2;
    GoogleMap gMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        if (checkPlayservices()) {
            buildGoogleApiClient();
        }

        sea=(EditText)findViewById(R.id.editText);
        btnShowLocation = (Button) findViewById(R.id.button);
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                displayLocation();
                String go=sea.getText().toString();
                final String u="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+loc1+","+loc2+"&radius=1000&type="+go+"&key=AIzaSyA4Lz-SvcgZxlCtkDyEQc1QF2J1bEhZCLM";

                new search().execute(u);
            }
        });
    }

    private void displayLocation() {
        detail d = new detail();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mlocation = LocationServices.FusedLocationApi
                    .getLastLocation(apiClient);
        }
        mlocation = LocationServices.FusedLocationApi
                .getLastLocation(apiClient);


        if (mlocation != null) {
            latitude = mlocation.getLatitude();
            loc1 = latitude;
            longitude = mlocation.getLongitude();
            loc2 = longitude;
            d.setLocation1(latitude);
            d.setLocation2(longitude);
            locationaddress locationaddress = new locationaddress();
            locationaddress.getaddressfromlocation(latitude, longitude, getApplicationContext(), new GeocoderHandler());
        } else {
        }
        gMap.clear();
        onMapReady(gMap);
    }

    protected synchronized void buildGoogleApiClient() {
        apiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    private boolean checkPlayservices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            Toast.makeText(MainActivity.this, "Not connected", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (apiClient != null) {
            apiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayservices();
    }

    @Override
    public void onConnected(Bundle bundle) {

        displayLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

        apiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        CircleOptions co = new CircleOptions();
        LatLng loc = new LatLng(loc1, loc2);
        co.center(loc);
        co.strokeWidth(2.0f);
        co.radius(1000);
        co.strokeColor(Color.RED);
        co.fillColor(0x44ff0000);
        gMap.addMarker(new MarkerOptions().position(loc).title("YOU ARE HERE").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
        gMap.animateCamera(CameraUpdateFactory.zoomIn());
        gMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        gMap.addCircle(co);
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationaddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationaddress = bundle.getString("address");
                    break;
                default:
                    locationaddress = null;
            }
        }
    }

    public class search extends AsyncTask<String, String, List<details>> {
        @Override
        protected List<details> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                List<details> placelist = new ArrayList<>();
                String finaljson = buffer.toString();
                JSONObject parentobject = new JSONObject(finaljson);
                JSONArray jsonArray = parentobject.getJSONArray("results");
                int len = jsonArray.length();
                for (int i = 0; i < len; i++) {
                    details d = new details();
                    JSONObject finalobject = jsonArray.getJSONObject(i);
                    d.setName(finalobject.getString("name"));
                    d.setIcon(finalobject.getString("icon"));
                    d.setLatitude(finalobject.getJSONObject("geometry").getJSONObject("location").getString("lat"));
                    d.setLongitude(finalobject.getJSONObject("geometry").getJSONObject("location").getString("lng"));
                    d.setVicinity(finalobject.getString("vicinity"));
                    int rating = 0;
                    if (finalobject.has("rating")) {
                        d.setRating(finalobject.getInt("rating"));
                    }
                    placelist.add(d);
                    // a.append(d.getName() +" "+ d.getVicinity() + " " + d.getLatitude() + ","+d.getLongitude()+"\n"+ d.getIcon() + "\n" + d.getRating() +"\n\n" );
                }


                return placelist;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<details> result) {
            super.onPostExecute(result);
            for(int i=0;i<result.size();i++){
                double lati= Double.parseDouble(result.get(i).getLatitude());
                double longi= Double.parseDouble(result.get(i).getLongitude());
                LatLng lLng=new LatLng(lati,longi);
                gMap.addMarker(new MarkerOptions().position(lLng).title(result.get(i).getName()));
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lLng, 15));
                gMap.animateCamera(CameraUpdateFactory.zoomIn());
                gMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
            }
            //TODO list

        }

    }
}
