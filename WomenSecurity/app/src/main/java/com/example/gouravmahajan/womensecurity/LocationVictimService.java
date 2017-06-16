package com.example.gouravmahajan.womensecurity;

import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class LocationVictimService extends Service
{
    LocationManager lm;
    boolean isNet;
    boolean isGPS;
    String imei;

    public LocationVictimService()
    {
    }
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d("MYVICTIM","ONSTARTCOMMAND");
                getLocation();


        return START_STICKY;
    }
    public void getLocation()
    {

        TelephonyManager telephonyManager= (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        imei=telephonyManager.getDeviceId();
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        MyLocationListener ml=new MyLocationListener();
        if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            isNet = true;
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
            isGPS = true;
        if(isGPS)
        {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,ml);
        }
        if(isNet)
        {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,ml);
        }


    }


    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

   class MyLocationListener implements LocationListener
   {

       @Override
       public void onLocationChanged(final Location location)
       {
           new Thread(new Runnable()
           {
               @Override
               public void run()
               {
                   if (Global.flag == false)
                   {
                       double lat = location.getLatitude();
                       double lng = location.getLongitude();
                       Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
                       List<Address> addresses = null;
                       try
                       {
                           addresses = geocoder.getFromLocation(lat, lng, 1);
                       } catch (IOException e)
                       {
                           e.printStackTrace();
                       }
                       String cityName = addresses.get(0).getAddressLine(2);
                       cityName = cityName.substring(0, cityName.indexOf(","));

                       Job j1 = new Job(lat, lng, cityName);
                       Thread t1 = new Thread(j1);
                       t1.start();
                       Global.flag=true;
                   }
               }
           }).start();


       }

       @Override
       public void onStatusChanged(String provider, int status, Bundle extras)
       {

       }

       @Override
       public void onProviderEnabled(String provider)
       {

       }

       @Override
       public void onProviderDisabled(String provider)
       {

       }
   }


    class Job implements Runnable
    {

        double lat;
        double lng;
        String city;
        Job(double lat,double lng,String city)
        {
            this.lat=lat;
            this.lng=lng;
            this.city=city;
        }
        @Override
        public void run()
        {
            try
            {
                city=city.replace(" ","%5F");
                Log.d("MYVICTIM",imei+"");
                Log.d("MYVICTIM",lat+"");
                Log.d("MYVICTIM",lng+"");
                Log.d("MYVICTIM",city+"");

                URL ur=new URL("http://192.168.43.176:8084/WomenSecurity/Location_Victim?imei="+imei+"&lat="+lat+"&lng="+lng+"&city="+city);
                HttpURLConnection conn= (HttpURLConnection) ur.openConnection();
                Log.d("MYMESSAGE",conn.getResponseCode()+"");
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
