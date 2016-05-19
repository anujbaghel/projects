package com.anuj.mapsss;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by ravi on 13-04-2016.
 */
public class locationaddress {
    private final String TAG = "locationaddress";
        public void getaddressfromlocation(final double latitude,final double longitude,final Context context,final Handler handler) {

            Thread thread=new Thread(){
            public void run(){
                Geocoder geocoder=new Geocoder(context, Locale.getDefault());
                String result = null;
                try{
                    List<Address> addresses= geocoder.getFromLocation(latitude,longitude,1);
                    if(addresses != null && addresses.size()>0){
                        Address address= addresses.get(0);
                        StringBuilder sb=new StringBuilder();
                        for (int i=0;i<address.getMaxAddressLineIndex();i++){
                            sb.append(address.getAddressLine(i)).append("\n");
                        }
                        sb.append(address.getLocality()).append("\n");
                        sb.append(address.getPostalCode()).append("\n");
                        sb.append(address.getCountryCode()).append("\n");
                        result = sb.toString();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "unabble to connect to Geocoder",e );
                }
                finally {
                    Message message=new Message();
                    message.setTarget(handler);
                    if(result!=null){
                        message.what=1;
                        Bundle bundle= new Bundle();
                        bundle.putString("address",result);
                        message.setData(bundle);
                    }else {
                        message.what=1;
                        Bundle bundle=new Bundle();
                        bundle.putString("address","unabble to get address");
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
            };
            thread.start();
        }
}
