package com.motivus.ece.motivus;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class NewAppointment extends ActionBarActivity {
    private EditText titleAppointment;
    private EditText detailAppointment;
    private final int GPSActivityIndex = 0;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_appointment);

        titleAppointment = (EditText) findViewById(R.id.editText_title);
        detailAppointment = (EditText) findViewById(R.id.editText_detail);
        //Add map button
        Button mapLocation = (Button) findViewById(R.id.button_location);
        mapLocation.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent map = new Intent(v.getContext(), GoogleMaps.class);
                        startActivityForResult(map, GPSActivityIndex);
                    }
                }
        );
        //Add new appointment button
        Button submitAppointment = (Button) findViewById(R.id.button_submit);
        submitAppointment.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Appointment appointment = new Appointment();
                        appointment.title = titleAppointment.getText().toString();
                        appointment.detail = detailAppointment.getText().toString();
                        appointment.latitude = latitude;
                        appointment.longitude = longitude;
                        Database.getInstance(getApplicationContext()).addAppointment(appointment);

                        finish();
                    }
                }
        );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_appointment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (GPSActivityIndex) : {
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    latitude = bundle.getDouble("latitude");
                    longitude = bundle.getDouble("longitude");
                    //Update your TextView.
                    EditText location = (EditText) findViewById(R.id.editText_location);
                    location.setText("" + latitude + "," + longitude);
                }
                break;
            }
        }
    }
}
