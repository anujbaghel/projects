package com.anuj.mapsss;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;

import java.io.IOException;

/**
 * Created by ravi on 15-04-2016.
 */
public class placesread extends AsyncTask<Object,Integer,String>{
        String placesdata=null;
    GoogleMap googleMap;
    @Override
    protected String doInBackground(Object[] objects) {
        try {
            googleMap= (GoogleMap) objects[0];
            String placesUrl= (String) objects[1];
            HTTP http=new HTTP();
            placesdata=http.read(placesUrl);
        } catch (IOException e) {
            Log.d("Google Place Read Task", e.toString());
        }
        return placesdata;
    }
    @Override
    protected void onPostExecute(String result) {
        PlacesDisplayTask placesDisplayTask = new PlacesDisplayTask();
        Object[] toPass = new Object[2];
        toPass[0] = googleMap;
        toPass[1] = result;
        placesDisplayTask.execute(toPass);
    }
}
