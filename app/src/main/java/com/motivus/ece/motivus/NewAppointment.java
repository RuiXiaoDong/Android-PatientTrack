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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;


public class NewAppointment extends ActionBarActivity {
    private EditText titleAppointment;
    private EditText detailAppointment;
    private final int GPSActivityIndex = 0;
    private double latitude;
    private double longitude;
    DatePickerDialog mDatePicker;
    TimePickerDialog mTimePicker;
    /*int hour;
    int minute;
    int year ;
    int month ;
    int day ;*/

    int hour2;
    int minute2;
    int year2 ;
    int month2 ;
    int day2 ;


LatLng latLng;
    String loc;
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

        //Add time button
        final EditText dateAppointment = (EditText) findViewById(R.id.editText_date);
        //final EditText enddateAppointment = (EditText) findViewById(R.id.editText_enddate);
        final EditText timeAppointment = (EditText) findViewById(R.id.editText_time);
        //Add map button
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
                        month2 = (monthOfYear < 10 ) ?  monthOfYear :   monthOfYear;
                        year2= year;
                        day2= (dayOfMonth < 10) ?  dayOfMonth :  dayOfMonth;

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
                       // appointment.enddate = enddateAppointment.getText().toString();
                        appointment.time = timeAppointment.getText().toString();

                        appointment.latitude = latitude;
                        appointment.longitude = longitude;

                        Database.getInstance(getApplication()).addAppointment(appointment);


                        //***********


                      /*  Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.DAY_OF_MONTH, day);
                        cal.set(Calendar.MONTH, month);
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.HOUR_OF_DAY, hour);
                        cal.set(Calendar.MINUTE, minute);*/
                        // long calId = 0;
                        //  long startMillis = 0;*/
                     Calendar beginTime = Calendar.getInstance();

                    beginTime.set(year2, month2, day2, hour2, minute2);
                     /*   Calendar endTime = Calendar.getInstance();
                        endTime.set(2015, 4, 17, 8, 30);*/

                        Intent intent = new Intent(Intent.ACTION_INSERT)
                                .setData(CalendarContract.Events.CONTENT_URI)
                                .putExtra(CalendarContract.Events._ID, 8)
                                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                                        //    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                                .putExtra(CalendarContract.Events.TITLE, appointment.title)
                                .putExtra(CalendarContract.Events.DESCRIPTION, appointment.detail)
                                .putExtra(CalendarContract.Events.EVENT_LOCATION, loc);
                        //long eventId = intent.getLong(intent.getColumnIndex("_id"));
                        //    .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                        //    .putExtra(Intent.EXTRA_EMAIL, "rowan@example.com,trevor@example.com");

                        startActivity(intent);
                        //***









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


    private void addEvent() {
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
                  //  latLng = new LatLng(latitude,
                           // longitude);
                    loc= latitude + "," + longitude;
                    //latLng.toString();

                    //Update your TextView.
                    EditText location = (EditText) findViewById(R.id.editText_location);
                    location.setText("" + latitude + "," + longitude);
                }
                break;
            }
        }
    }
}
