package com.motivus.ece.motivus;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;
import android.location.LocationManager;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
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
    int mNotificationId;

    public static float LOCATION_REFRESH_DISTANCE = 0;
    public static long LOCATION_REFRESH_TIME = 5000;

    public static float APPOINTMENT_RANGE = 500; //in meter
    public static long APPOINTMENT_REMIND_TIME = 30; //in min

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

        Toast.makeText(getApplicationContext(), "GPS Tracking Service Created",
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

        Toast.makeText(getApplicationContext(), "GPS Tracking Service Destroyed",
                Toast.LENGTH_SHORT).show();
    }

    private int sendNotification(String content) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.motivus_notification)
                        .setContentTitle("motivus: appointment remind")
                        .setContentText(content);
        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(mNotificationId, mBuilder.build());
        return mNotificationId;
    }

    /**
     * Compare appointment location
     * @param latitude
     * @param longitude
     */
    private void checkAppointmentAccomplishment(double latitude, double longitude){
        ArrayList<Appointment> appointments = Database.getInstance(getApplication()).getAllAppointments();
        //Check the date
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        for(int i = 0; i <  appointments.size(); i++) {
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

            if(Math.abs(diffmin) <= APPOINTMENT_REMIND_TIME) {
                double diffDistance = HelperFunctions.checkDistance(latitude, longitude, appointments.get(i).latitude, appointments.get(i).longitude);
                //Check the location
                if (diffDistance <= APPOINTMENT_RANGE) {
                    if(appointments.get(i).done == 0) {
                        appointments.get(i).done = 1;
                        appointments.get(i).score = PointSystem.appointmentPoint(true, diffmin > 0);
                        if (Database.getInstance(getApplication()).existAppointment(appointments.get(i).id))
                            Database.getInstance(getApplication()).updateAppointment(appointments.get(i));
                    }
                    Toast.makeText(getApplication(),
                            "\"" + appointments.get(i).title + "\" appointment DONE!", Toast.LENGTH_SHORT)
                            .show();
                    sendNotification("Appointment: \"" + appointments.get(i).title + "\" is coming!");
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
