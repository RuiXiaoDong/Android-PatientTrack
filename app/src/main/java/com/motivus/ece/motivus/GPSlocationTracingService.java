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
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class GPSlocationTracingService extends Service {

    private LocationManager mLocationManager;
    private PowerManager.WakeLock wakeLock;

    double mLastLatitude = 0;
    double mLastLongitude = 0;

    private final float LOCATION_REFRESH_DISTANCE = 0;
    private final long LOCATION_REFRESH_TIME = 5000;

    private float LOCATION_THRESHOLD_DISTANCE = 1000; //in meter
    private float LOCATION_THRESHOLD_TIME = 30; //in min

    private boolean mRunning;
    public GPSlocationTracingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if (!mRunning) {
            mRunning = true;
            // do something
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        PowerManager pm = (PowerManager) getSystemService(this.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DoNotSleep");
        wakeLock.acquire();

        Toast.makeText(getApplicationContext(), "Service Created",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    @Deprecated
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        mLocationManager = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.removeUpdates(mLocationListener);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, mLocationListener);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationManager.removeUpdates(mLocationListener);
        wakeLock.release();

        Toast.makeText(getApplicationContext(), "Service Destroyed",
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Compare appointment location
     * @param latitude
     * @param longitude
     */
    private void checkAppointmentAccomplishment(double latitude, double longitude){
        ArrayList<Appointment> appointments = Database.getInstance(getApplication()).getAllAppointments();
        for(int i = 0; i <  appointments.size(); i++) {
            //Check the date
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            Date currentDate = calendar.getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String appointmentDate = "" + appointments.get(i).date + ' ' + appointments.get(i).time;
            Date compareDate;

            try {
                compareDate = formatter.parse(appointmentDate);
            } catch (ParseException e) {
                e.printStackTrace();
                continue;
            }
            long diff = currentDate.getTime() - compareDate.getTime();
            int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
            int diffhours = (int) (diff / (60 * 60 * 1000));
            int diffmin = (int) (diff / (60 * 1000));
            int diffsec = (int) (diff / (1000));

            if(diffmin <= LOCATION_THRESHOLD_TIME) {
                double diffDistance = HelperFunctions.checkDistance(latitude, longitude, appointments.get(i).latitude, appointments.get(i).longitude);
                //Check the location
                if (diffDistance <= LOCATION_THRESHOLD_DISTANCE) {
                    appointments.get(i).done = 1;
                    //if(Database.getInstance(getApplication()).existAppointment(appointments.get(i).title))
                    //    Database.getInstance(getApplication()).updateAppointment(appointments.get(i));
                    Toast.makeText(getApplication(),
                            "\"" + appointments.get(i).title + "\" appointment DONE!", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
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
            if (logDiffDistance >= LOCATION_REFRESH_DISTANCE) {
                Database.getInstance(getApplication()).addGPS(gpsLocation);
                mLastLatitude = latitude;
                mLastLongitude = longitude;
            }

            checkAppointmentAccomplishment(latitude, longitude);
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
