package com.motivus.ece.motivus;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;


public class NewAppointment extends ActionBarActivity {
    private EditText titleAppointment;
    private EditText detailAppointment;
    private final int GPSActivityIndex = 0;
    private double latitude = 0.0;
    private double longitude = 0.0;
    DatePickerDialog mDatePicker;
    TimePickerDialog mTimePicker;
    
    int hour2;
    int minute2;
    int year2 ;
    int month2 ;
    int day2 ;
    String loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_appointment);

        //Category
        ArrayAdapter<String> adapterCategory = new ArrayAdapter<String>(this,
                R.layout.spinner_item, Database.AppointmentCategory.subList(1,Database.AppointmentCategory.size()));
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinCategory = (Spinner)findViewById(R.id.spinner_category);
        spinCategory.setAdapter(adapterCategory);

        titleAppointment = (EditText) findViewById(R.id.editText_title);
        detailAppointment = (EditText) findViewById(R.id.editText_detail);
        //Map button
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

        final EditText dateAppointment = (EditText) findViewById(R.id.editText_date);
        //final EditText enddateAppointment = (EditText) findViewById(R.id.editText_enddate);
        final EditText timeAppointment = (EditText) findViewById(R.id.editText_time);
        //Time and Date picker button
        Button timePicker = (Button) findViewById(R.id.button_timePicker);
        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                //Pick time within the day
                mTimePicker = new TimePickerDialog(NewAppointment.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int  selectedHour,  int selectedMinute) {
                        String  hours = (selectedHour < 10 ) ? "0" + selectedHour : "" + selectedHour;
                        String mins = (selectedMinute < 10) ? "0" + selectedMinute : "" + selectedMinute;
                        timeAppointment.setText(hours + ":" + mins);
                        hour2= selectedHour;
                        minute2= selectedMinute;
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.show();

                //Pick the date, month, year
                mDatePicker = new DatePickerDialog(NewAppointment.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker timePicker, int year, int monthOfYear, int dayOfMonth) {
                        monthOfYear = monthOfYear + 1;
                        String months = (monthOfYear < 10 ) ? "0" + monthOfYear : "" + monthOfYear;
                        String days = (dayOfMonth < 10) ? "0" + dayOfMonth : "" + dayOfMonth;
                        dateAppointment.setText(year + "-" + months + "-" + days);
                        month2 = monthOfYear -1;
                        year2 = year;
                        day2 = dayOfMonth;

                    }
                }, year, month, day);//Yes 24 hour time
                mDatePicker.show();
            }
        });

        //Add new appointment button
        Button submitAppointment = (Button) findViewById(R.id.button_submit);
        submitAppointment.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Appointment appointment = new Appointment();
                        appointment.id = Database.getInstance(getApplication()).getMaxAppointmentID();
                        appointment.title = titleAppointment.getText().toString();
                        appointment.detail = detailAppointment.getText().toString();
                        appointment.date = dateAppointment.getText().toString();
                        appointment.time = timeAppointment.getText().toString();
                        appointment.latitude = latitude;
                        appointment.longitude = longitude;
                        appointment.done = 0;
                        appointment.score = 0;
                        Spinner spinner = (Spinner)findViewById(R.id.spinner_category);
                        appointment.category = Database.AppointmentCategory.indexOf(spinner.getSelectedItem().toString())  + 1; //plus one since the "index 0 : all" is hidden

                        Database.getInstance(getApplication()).addAppointment(appointment);
                        Calendar beginTime = Calendar.getInstance();

                        beginTime.set(year2, month2, day2, hour2, minute2);

                        //Sync with google
                        Intent intent = new Intent(Intent.ACTION_INSERT)
                                .setData(CalendarContract.Events.CONTENT_URI)
                                .putExtra(CalendarContract.Events._ID, 8)
                                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                                .putExtra(CalendarContract.Events.TITLE, appointment.title)
                                .putExtra(CalendarContract.Events.DESCRIPTION, appointment.detail)
                                .putExtra(CalendarContract.Events.EVENT_LOCATION, loc);
                        startActivity(intent);

                        Intent data = new Intent();
                        if (getParent() == null) {
                            setResult(Activity.RESULT_OK, data);
                        } else {
                            getParent().setResult(Activity.RESULT_OK, data);
                        }
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
                    loc= latitude + "," + longitude;

                    //Update your TextView.
                    EditText location = (EditText) findViewById(R.id.editText_location);
                    location.setText("" + latitude + "," + longitude);
                }
                break;
            }
        }
    }
}
