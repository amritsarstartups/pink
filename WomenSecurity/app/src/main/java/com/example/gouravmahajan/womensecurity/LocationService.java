package com.example.gouravmahajan.womensecurity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

public class LocationService extends Service
{
    LocationManager lm;
    boolean isNet;
    boolean isGPS;
    String imei;
    public LocationService()
    {
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
                getLocation();


        return START_STICKY;
    }
    public void getLocation()
    {
        MyLocationListener ml=new MyLocationListener();
        TelephonyManager telephonyManager= (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        imei=telephonyManager.getDeviceId();
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            isNet = true;
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
            isGPS = true;
        if(isGPS)
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,ml);
        if(isNet)
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,ml);
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
                        try
                        {
                            Thread.sleep(5000);
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        Job j1 = new Job(lat, lng, cityName);
                        Thread t1 = new Thread(j1);
                        t1.start();

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
                Log.d("MYMESSAGE",imei+"");
                Log.d("MYMESSAGE",lat+"");
                Log.d("MYMESSAGE",lng+"");
                Log.d("MYMESSAGE",city+"");

                URL ur=new URL("http://192.168.43.176:8084/WomenSecurity/Location?imei="+imei+"&lat="+lat+"&lng="+lng+"&city="+city);
                HttpURLConnection conn= (HttpURLConnection) ur.openConnection();
                Log.d("MYMESSAGE",conn.getResponseCode()+"");
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String res=br.readLine();
                Log.d("MYMESSAGE",res+"");
                if(res!=null)
                {
                    StringTokenizer strk=new StringTokenizer(res,"_");
                    strk.nextToken();
                    String lat=strk.nextToken();
                    String lng=strk.nextToken();
                    NotificationManager nm= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    NotificationCompat.Builder nb=new NotificationCompat.Builder(getBaseContext());
                    nb.setContentTitle("Some One Need Help");
                    nb.setContentText("Help");
                    nb.setSmallIcon(R.mipmap.ic_launcher);
                    nb.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                    nb.setVibrate(new long[]{233,455,600});
                    Intent it=new Intent(Intent.ACTION_VIEW);
                    it.setData(Uri.parse("http://maps.google.com/maps?z=12&t=m&q=loc:"+lat+"+"+lng));
                    PendingIntent pit=PendingIntent.getActivity(getBaseContext(),0,it,0);
                    nb.setContentIntent(pit);
                    nm.notify(20,nb.build());
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
