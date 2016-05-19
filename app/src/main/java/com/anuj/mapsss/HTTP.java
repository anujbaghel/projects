package com.anuj.mapsss;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ravi on 15-04-2016.
 */
public class HTTP {
    public  String read(String httpUrl) throws IOException {
        String hhtpdata="";
        InputStream inputStream=null;
        HttpURLConnection httpURLConnection=null;
        try {
            URL url=new URL(httpUrl);
            httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.connect();
            inputStream=httpURLConnection.getInputStream();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer= new StringBuffer();
            String line="";
            while ((line=bufferedReader.readLine())!=null){
                stringBuffer.append(line);
            }
            hhtpdata=stringBuffer.toString();
            bufferedReader.close();
        }catch (Exception e){
            Log.d("Exception", e.toString());

        }finally {
            inputStream.close();
            httpURLConnection.disconnect();
        }
        return hhtpdata;
    }
}
