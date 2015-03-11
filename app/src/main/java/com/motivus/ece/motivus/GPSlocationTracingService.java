package com.motivus.ece.motivus;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;
import android.location.LocationManager;
import android.os.PowerManager;
import android.provider.SyncStateContract;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GPSlocationTracingService extends Service {

    private LocationManager mLocationManager;
    private PowerManager.WakeLock wakeLock;

    double mLastLatitude = 0;
    double mLastLongitude = 0;

    private final float LOCATION_REFRESH_DISTANCE = 0;
    private final long LOCATION_REFRESH_TIME = 1000;
    private final float LOCATION_THRESHOLD_DISTANCE = 200;
    private final float LOCATION_LOG_THRESHOLD_DISTANCE = 500;

    public GPSlocationTracingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        PowerManager pm = (PowerManager) getSystemService(this.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DoNotSleep");

        Toast.makeText(getApplicationContext(), "Service Created",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    @Deprecated
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);

        mLocationManager = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);

        mLocationManager.removeUpdates(mLocationListener);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, mLocationListener);

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        wakeLock.release();
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
            Date date = new Date();
            GPSlocation gpsLocation = new GPSlocation();
            gpsLocation.time = dateFormat.format(date);
            gpsLocation.latitude = latitude;
            gpsLocation.longitude = longitude;
            double logDiffDistance = HelperFunctions.checkDistance(latitude, longitude, mLastLatitude, mLastLongitude);
            if (logDiffDistance >= LOCATION_LOG_THRESHOLD_DISTANCE) {
                Database.getInstance(getApplication()).addGPS(gpsLocation);
                mLastLatitude = latitude;
                mLastLongitude = longitude;
            }

            //Compare appointment location
            ArrayList<Appointment> appointments = Database.getInstance(getApplication()).getAllAppointments();
            for(int i = 0; i <  appointments.size(); i++) {
                double diffDistance = HelperFunctions.checkDistance(latitude, longitude, appointments.get(i).latitude, appointments.get(i).longitude);
                if (diffDistance <= LOCATION_THRESHOLD_DISTANCE) {
                    appointments.get(i).check = true;
                    Toast.makeText(getApplication(),
                            "\"" + appointments.get(i).title + "\" appointment DONE!" , Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
}
