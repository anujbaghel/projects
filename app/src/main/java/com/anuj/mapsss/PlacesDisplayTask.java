package com.anuj.mapsss;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by ravi on 15-04-2016.
 */
public class PlacesDisplayTask extends AsyncTask<Object,Integer,List<HashMap<String,String>>> {
    JSONObject jsonObject;
    GoogleMap googleMap;

    @Override
    protected List<HashMap<String,String>> doInBackground(Object... objects) {
            List<HashMap<String,String>> googleplaceslist =null;
            Places places=new Places();
            try {
                googleMap= (GoogleMap) objects[0];
                jsonObject=new JSONObject((String)objects[1]);
                googleplaceslist=places.parse(jsonObject);
            } catch (JSONException e) {
                Log.d("Exception", e.toString());
            }
        return googleplaceslist;
    }
    @Override
    protected void onPostExecute(List<HashMap<String, String>> list) {
        googleMap.clear();
        for (int i = 0; i < list.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = list.get(i);
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(placeName + " : " + vicinity);
            googleMap.addMarker(markerOptions);
        }
    }
}
