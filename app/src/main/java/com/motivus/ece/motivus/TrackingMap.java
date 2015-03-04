package com.motivus.ece.motivus;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dongx
 */
public class TrackingMap extends FragmentActivity implements LocationListener {
    private GoogleMap googleMap;
    private UiSettings uiSettings;
    private Marker selectedLocation;
    private LocationManager locationManager;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;
    private LatLng latLng;
    private MarkerOptions markerOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracking_map);

        //Loading map
        try {
            initializeMap();
            ArrayList<GPSlocation> gpsLocations = Database.getInstance(getApplicationContext()).getAllGPSs();
            putMarkers(gpsLocations);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeMap() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            alertTurnOnGPS();
        }

        if (googleMap == null) {
            googleMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            googleMap.setMyLocationEnabled(true);

            uiSettings = googleMap.getUiSettings();
            uiSettings.setAllGesturesEnabled(true);

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, MIN_TIME,
                    MIN_DISTANCE, this);

            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Unable to create the map", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void putMarkers(ArrayList<GPSlocation> addresses) {
        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
        }

        // Clears all the existing markers on the map
        googleMap.clear();

        // Adding Markers on Google Map for each matching address
        for (int i = 0; i < addresses.size(); i++) {
            GPSlocation address = (GPSlocation) addresses.get(i);

            // Creating an instance of GeoPoint, to display in Google Map
            latLng = new LatLng(address.latitude, address.longitude);

            markerOptions = new MarkerOptions();
            markerOptions.position(latLng);

            googleMap.addMarker(markerOptions);

            // Locate the first location
            if (i == 0) {
                CameraPosition cameraPosition = new CameraPosition.Builder().target(
                        latLng).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    }

    private void alertTurnOnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to enable GPS?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog,
                                                final int id) {
                                startActivity(new Intent(
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeMap();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null)
            return;
        LatLng latLng = new LatLng(location.getLatitude(),
                location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,
                10);
        googleMap.animateCamera(cameraUpdate);
        locationManager.removeUpdates(this);
        googleMap.getMaxZoomLevel();
    }

    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub

    }
}