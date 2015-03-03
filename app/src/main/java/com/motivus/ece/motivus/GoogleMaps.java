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
import java.util.List;

/**
 * Created by dongx
 */
public class GoogleMaps extends FragmentActivity implements
        GoogleMap.OnMapClickListener, LocationListener {
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
        setContentView(R.layout.google_map);

        Button findLocation = (Button) findViewById(R.id.button_find);
        findLocation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //Getting user input location
                AutoCompleteTextView etLocation = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
                String location = etLocation.getText().toString();

                if (location != null && !location.equals("")) {
                    System.out.println (location);
                    new GeocoderTask().execute(location);
                }
            }
        });

        Button submitLocation = (Button) findViewById(R.id.button_submit);
        submitLocation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                if(latLng == null)
                    return;

                data.putExtra("latitude", latLng.latitude);
                data.putExtra("longitude", latLng.longitude);

                if (getParent() == null) {
                    setResult(Activity.RESULT_OK, data);
                } else {
                    getParent().setResult(Activity.RESULT_OK, data);
                }
                finish();
            }
        });

        //Loading map
        try {
            initializeMap();
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
            googleMap.setOnMapClickListener(this);
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

    private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName) {
            // Creating an instance of Geocoder class
            Geocoder geocoder = new Geocoder(getBaseContext());
            List<Address> addresses = null;

            try {
                addresses = geocoder.getFromLocationName(locationName[0], 3);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        public void onPostExecute(List<Address> addresses) {

            if (addresses == null || addresses.size() == 0) {
                Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
            }

            // Clears all the existing markers on the map
            googleMap.clear();

            // Adding Markers on Google Map for each matching address
            for (int i = 0; i < addresses.size(); i++) {
                Address address = (Address) addresses.get(i);

                // Creating an instance of GeoPoint, to display in Google Map
                latLng = new LatLng(address.getLatitude(), address.getLongitude());

                String addressText = String.format("%s, %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getCountryName());

                markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(addressText);

                googleMap.addMarker(markerOptions);

                // Locate the first location
                if (i == 0) {
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(
                            latLng).zoom(12).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        this.latLng = latLng;
        double latitude = latLng.latitude;
        double longitude = latLng.longitude;

        Toast.makeText(this.getApplicationContext(),
                "Latitude: " + latitude + " Longitude: " + longitude,
                Toast.LENGTH_SHORT).show();
        //Rmove old location and add a new marker
        if (selectedLocation != null)
            selectedLocation.remove();
        selectedLocation = googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title("here"));

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
}