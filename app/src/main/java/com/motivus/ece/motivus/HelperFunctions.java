package com.motivus.ece.motivus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by dongx on 2015-03-06.
 */
public class HelperFunctions {

    public static void alertTurnOnGPS(final Context mContext) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Do you want to enable GPS?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                mContext.startActivity(new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog,
                                        final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public static boolean isConnectingToInternet(Context _context) {
        ConnectivityManager connectivity = (ConnectivityManager) _context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    /**
     * Calculates the distance between two locations in KM
     */
    public static double checkDistance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 6371;

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c * 1000;

        return dist; // in meter
    }

    public static Bitmap getGoogleMapThumbnail(double latitude, double longitude){

        String URL = "http://maps.google.com/maps/api/staticmap?center=" +latitude + "," + longitude + "&zoom=15&size=600x600&sensor=false";

        Bitmap bmp = null;
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet request = new HttpGet(URL);

        InputStream in = null;
        try {
            in = httpclient.execute(request).getEntity().getContent();
            bmp = BitmapFactory.decodeStream(in);
            in.close();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return bmp;
    }

    public static Appointment[] demoAppointments() {
        Appointment[] appointments = new Appointment[15];

        Appointment appointment0 = new Appointment();
        appointment0.id = 0;
        appointment0.title = "Home";
        appointment0.detail = "test";
        appointment0.date = "2015-03-15";
        appointment0.time = "08:30";
        appointment0.latitude = 43.659389479900085;
        appointment0.longitude = -79.37318980693817;
        appointment0.done = 1;
        appointment0.pic = null;
        appointments[0] = appointment0;

        Appointment appointment1 = new Appointment();
        appointment1.id = 1;
        appointment1.title = "Ryerson University";
        appointment1.detail = "test";
        appointment1.date = "2015-03-14";
        appointment1.time = "08:30";
        appointment1.latitude = 43.65769058142255;
        appointment1.longitude = -79.37882781028748;
        appointment1.done = 1;
        appointment1.pic = null;
        appointments[1] = appointment1;

        Appointment appointment2 = new Appointment();
        appointment2.id = 2;
        appointment2.title = "Toronto General Hospital";
        appointment2.detail = "test";
        appointment2.date = "2015-03-13";
        appointment2.time = "08:30";
        appointment2.latitude = 43.657193807051044;
        appointment2.longitude = -79.3880546092987;
        appointment2.done = 0;
        appointment2.pic = null;
        appointments[2] = appointment2;

        Appointment appointment3 = new Appointment();
        appointment3.id = 3;
        appointment3.title = "Centre for Addiction and Mental Health";
        appointment3.detail = "test";
        appointment3.date = "2015-03-05";
        appointment3.time = "08:30";
        appointment3.latitude = 43.658574480347106;
        appointment3.longitude = -79.39919650554657;
        appointment3.done = 1;
        appointment3.pic = null;
        appointments[3] = appointment3;

        Appointment appointment4 = new Appointment();
        appointment4.id = 4;
        appointment4.title = "Mount Sinai Hospital";
        appointment4.detail = "test";
        appointment4.date = "2015-03-04";
        appointment4.time = "08:30";
        appointment4.latitude = 43.65751981569591;
        appointment4.longitude = -79.39038276672363;
        appointment4.done = 1;
        appointment4.pic = null;
        appointments[4] = appointment4;

        Appointment appointment5 = new Appointment();
        appointment5.id = 5;
        appointment5.title = "St. Michael's Hospital";
        appointment5.detail = "test";
        appointment5.date = "2015-03-03";
        appointment5.time = "08:30";
        appointment5.latitude = 43.653283505911965;
        appointment5.longitude = -79.37755107879639;
        appointment5.done = 1;
        appointment5.pic = null;
        appointments[5] = appointment5;

        Appointment appointment6 = new Appointment();
        appointment6.id = 6;
        appointment6.title = "Toronto Western Hospital";
        appointment6.detail = "test";
        appointment6.date = "2015-03-02";
        appointment6.time = "08:30";
        appointment6.latitude = 43.65350085935817;
        appointment6.longitude = -79.40527439117432;
        appointment6.done = 0;
        appointment6.pic = null;
        appointments[6] = appointment6;

        Appointment appointment7 = new Appointment();
        appointment7.id = 7;
        appointment7.title = "Centre for Addiction and Mental Health";
        appointment7.detail = "test";
        appointment7.date = "2015-02-25";
        appointment7.time = "08:30";
        appointment7.latitude = 43.658574480347106;
        appointment7.longitude = -79.39919650554657;
        appointment7.done = 1;
        appointment7.pic = null;
        appointments[7] = appointment7;

        Appointment appointment8 = new Appointment();
        appointment8.id = 8;
        appointment8.title = "Mount Sinai Hospital";
        appointment8.detail = "test";
        appointment8.date = "2015-02-24";
        appointment8.time = "08:30";
        appointment8.latitude = 43.65751981569591;
        appointment8.longitude = -79.39038276672363;
        appointment8.done = 1;
        appointment8.pic = null;
        appointments[8] = appointment8;

        Appointment appointment9 = new Appointment();
        appointment9.id = 9;
        appointment9.title = "St. Michael's Hospital";
        appointment9.detail = "test";
        appointment9.date = "2015-02-24";
        appointment9.time = "08:30";
        appointment9.latitude = 43.653283505911965;
        appointment9.longitude = -79.37755107879639;
        appointment9.done = 1;
        appointment9.pic = null;
        appointments[9] = appointment9;

        Appointment appointment10 = new Appointment();
        appointment10.id = 10;
        appointment10.title = "Toronto Western Hospital";
        appointment10.detail = "test";
        appointment10.date = "2015-02-23";
        appointment10.time = "08:30";
        appointment10.latitude = 43.65350085935817;
        appointment10.longitude = -79.40527439117432;
        appointment10.done = 0;
        appointment10.pic = null;
        appointments[10] = appointment10;

        Appointment appointment11 = new Appointment();
        appointment11.id = 11;
        appointment11.title = "Home";
        appointment11.detail = "test";
        appointment11.date = "2015-02-23";
        appointment11.time = "08:30";
        appointment11.latitude = 43.659389479900085;
        appointment11.longitude = -79.37318980693817;
        appointment11.done = 0;
        appointment11.pic = null;
        appointments[11] = appointment11;

        Appointment appointment12 = new Appointment();
        appointment12.id = 12;
        appointment12.title = "Toronto Western Hospital";
        appointment12.detail = "test";
        appointment12.date = "2015-02-17";
        appointment12.time = "08:30";
        appointment12.latitude = 43.65350085935817;
        appointment12.longitude = -79.40527439117432;
        appointment12.done = 1;
        appointment12.pic = null;
        appointments[12] = appointment12;

        Appointment appointment13 = new Appointment();
        appointment13.id = 13;
        appointment13.title = "Home";
        appointment13.detail = "test";
        appointment13.date = "2015-02-16";
        appointment13.time = "08:30";
        appointment13.latitude = 43.659389479900085;
        appointment13.longitude = -79.37318980693817;
        appointment13.done = 0;
        appointment13.pic = null;
        appointments[13] = appointment13;

        Appointment appointment14 = new Appointment();
        appointment14.id = 14;
        appointment14.title = "Home";
        appointment14.detail = "test";
        appointment14.date = "2015-02-10";
        appointment14.time = "08:30";
        appointment14.latitude = 43.659389479900085;
        appointment14.longitude = -79.37318980693817;
        appointment14.done = 1;
        appointment14.pic = null;
        appointments[14] = appointment14;

        return appointments;
    }
}
