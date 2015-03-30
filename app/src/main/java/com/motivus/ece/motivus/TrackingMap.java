package com.motivus.ece.motivus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;

/**
 * Created by dongx
 */
public class TrackingMap extends FragmentActivity implements LocationListener {
    private GoogleMap mGoogleMap;
    private UiSettings uiSettings;
    private LocationManager mLocationManager;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;
    private LatLng latLng;
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracking_map);

        //Loading map
        try {
            initializeMap();
            //Add all the visited locations
            ArrayList<GPSlocation> gpsLocations = Database.getInstance(getApplication()).getAllGPSs();
            ArrayList<LatLng> gpsAddresses =  new ArrayList<LatLng>();
            for(int i = 0; i < gpsLocations.size(); i++) {
                gpsAddresses.add(i, new LatLng(
                        gpsLocations.get(i).latitude, gpsLocations.get(i).longitude));
            }
            putMarkers(gpsAddresses, BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_RED));
            putHeatMap(gpsAddresses);
            //Add all the appointment locations
            ArrayList<Appointment> appointments = Database.getInstance(getApplication()).getAllAppointments();
            gpsAddresses =  new ArrayList<LatLng>();
            for(int i = 0; i < appointments.size(); i++) {
                gpsAddresses.add(i, new LatLng(
                        appointments.get(i).latitude, appointments.get(i).longitude));
            }
            putMarkers(gpsAddresses, BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeMap() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            alertTurnOnGPS();
        }

        if (mGoogleMap == null) {
            mGoogleMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            mGoogleMap.setMyLocationEnabled(true);

            uiSettings = mGoogleMap.getUiSettings();
            uiSettings.setAllGesturesEnabled(true);

            mLocationManager.removeUpdates(this);
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, MIN_TIME,
                    MIN_DISTANCE, this);

            if (mGoogleMap == null) {
                Toast.makeText(getApplication(),
                        "Unable to create the map", Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                CameraPosition cameraPosition = new CameraPosition.Builder().target(
                        new LatLng(43.653226, -79.3831842)).zoom(10).build();
                mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }

            // Clears all the existing markers on the map
            mGoogleMap.clear();
        }
    }

    private void putMarkers(ArrayList<LatLng> addresses, BitmapDescriptor descriptor) {
        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
        }

        // Adding Markers on Google Map for each matching address
        for (int i = 0; i < addresses.size(); i++) {
            LatLng address = addresses.get(i);

            // Creating an instance of GeoPoint, to display in Google Map
            latLng = new LatLng(address.latitude, address.longitude);

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(descriptor);
            mGoogleMap.addMarker(markerOptions);

            // Locate the first location
            if (i == 0) {
                CameraPosition cameraPosition = new CameraPosition.Builder().target(
                        latLng).zoom(12).build();
                mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    }

    private void putHeatMap(ArrayList<LatLng> addresses) {
        // Create a heat map tile provider, passing it the latlngs of the police stations.
        mProvider = new HeatmapTileProvider.Builder()
                .data(addresses)
                .build();
        mProvider.setRadius(50);
        // Add a tile overlay to the map, using the heat map tile provider.
        mOverlay = mGoogleMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
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
    protected void onPause() {
        super.onPause();
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null)
            return;
        LatLng latLng = new LatLng(location.getLatitude(),
                location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,
                10);
        mGoogleMap.animateCamera(cameraUpdate);
        mGoogleMap.getMaxZoomLevel();
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