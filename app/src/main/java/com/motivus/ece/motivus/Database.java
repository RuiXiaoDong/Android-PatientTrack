package com.motivus.ece.motivus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by dongx on 2015-02-18.
 */
public class Database extends SQLiteOpenHelper {
    //Singleton
    private static Database mInstance = null;

    public static ArrayList<String> AppointmentCategory = new ArrayList<String>(Arrays.asList(
            "All",
            "Work/School",
            "Social",
            "Solo Recreation",
            "Group Recreation",
            "Health/Fitness",
            "Personal Care"
    ));
    public static final String DATABASE_NAME = "Appointments.db";
    public static final int DATABASE_VERSION = 1;

    //Appointment table
    public static final String APPOINTMENT_TABLE_NAME = "appointments";
    public static final String APPOINTMENT_COLUMN_NAME_ID = "id";
    public static final String APPOINTMENT_COLUMN_NAME_TITLE = "title";
    public static final String APPOINTMENT_COLUMN_NAME_DETAIL = "detail";
    public static final String APPOINTMENT_COLUMN_NAME_DATE = "date";
    public static final String APPOINTMENT_COLUMN_NAME_TIME = "time";
    public static final String APPOINTMENT_COLUMN_NAME_LATITUDE = "latitude";
    public static final String APPOINTMENT_COLUMN_NAME_LONGITUDE = "longitude";
    public static final String APPOINTMENT_COLUMN_NAME_DONE = "done";
    public static final String APPOINTMENT_COLUMN_NAME_SCORE = "score";
    public static final String APPOINTMENT_COLUMN_NAME_PIC = "pic";
    public static final String APPOINTMENT_COLUMN_NAME_CATEGORY = "category";
    public static final String APPOINTMENT_COLUMN_NAME_LOCKED = "locked";

    public static final String APPOINTMENT_SQL_CREATE_ENTRIES =
            "CREATE TABLE " + APPOINTMENT_TABLE_NAME + " (" +
                    APPOINTMENT_COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    APPOINTMENT_COLUMN_NAME_TITLE + " TEXT," +
                    APPOINTMENT_COLUMN_NAME_DETAIL + " TEXT," +
                    APPOINTMENT_COLUMN_NAME_DATE + " TEXT," +
                    APPOINTMENT_COLUMN_NAME_TIME + " TEXT," +
                    APPOINTMENT_COLUMN_NAME_LATITUDE + " TEXT," +
                    APPOINTMENT_COLUMN_NAME_LONGITUDE + " TEXT," +
                    APPOINTMENT_COLUMN_NAME_DONE + " INTEGER," +
                    APPOINTMENT_COLUMN_NAME_SCORE + " INTEGER," +
                    APPOINTMENT_COLUMN_NAME_PIC + " BLOB," +
                    APPOINTMENT_COLUMN_NAME_CATEGORY + " INTEGER," +
                    APPOINTMENT_COLUMN_NAME_LOCKED + " INTEGER" +
                    " )";
    public static final String APPOINTMENT_SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + APPOINTMENT_TABLE_NAME;

    //GPS table
    public static final String GPS_TABLE_NAME = "gpslocation";
    public static final String GPS_COLUMN_NAME_TIME = "time";
    public static final String GPS_COLUMN_NAME_LATITUDE = "latitude";
    public static final String GPS_COLUMN_NAME_LONGITUDE = "longitude";
    public static final String[] GPS_COLUMNS = {GPS_COLUMN_NAME_TIME,
            GPS_COLUMN_NAME_LATITUDE,
            GPS_COLUMN_NAME_LONGITUDE};

    public static final String GPS_SQL_CREATE_ENTRIES =
            "CREATE TABLE " + GPS_TABLE_NAME + " (" +
                    GPS_COLUMN_NAME_TIME + " TEXT PRIMARY KEY," +
                    GPS_COLUMN_NAME_LATITUDE + " TEXT," +
                    GPS_COLUMN_NAME_LONGITUDE + " TEXT" +
            " )";
    public static final String GPS_SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + GPS_TABLE_NAME;

    //SMS table
    public static final String SMS_TABLE_NAME = "sms";
    public static final String SMS_COLUMN_NAME_TIME = "time";
    public static final String[] SMS_COLUMNS = {SMS_TABLE_NAME,
            SMS_COLUMN_NAME_TIME};

    private int maxID;
    public static Database getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new Database(ctx.getApplicationContext());
        }
        return mInstance;
    }

    private Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        maxID = 0;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            //Appointment database
            db.execSQL(APPOINTMENT_SQL_DELETE_ENTRIES);
            db.execSQL(APPOINTMENT_SQL_CREATE_ENTRIES);
            //GPS raw data
            db.execSQL(GPS_SQL_DELETE_ENTRIES);
            db.execSQL(GPS_SQL_CREATE_ENTRIES);
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(APPOINTMENT_SQL_DELETE_ENTRIES);
        db.execSQL(GPS_SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void setMaxAppointmentID(int id) {
        if(maxID < id)
            maxID = id;
    }

    public int getMaxAppointmentID() {
        maxID++;
        return maxID;
    }

    public long addAppointment(Appointment appointment) {
        long newRowId = -1;
        if(appointment == null)
            return newRowId;
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        if(appointment.title == null || appointment.title.compareToIgnoreCase("") == 0)
            return newRowId;

        values.put(APPOINTMENT_COLUMN_NAME_ID, appointment.id);
        values.put(APPOINTMENT_COLUMN_NAME_TITLE, appointment.title);
        values.put(APPOINTMENT_COLUMN_NAME_DETAIL, appointment.detail);
        values.put(APPOINTMENT_COLUMN_NAME_DATE, appointment.date);
        values.put(APPOINTMENT_COLUMN_NAME_TIME, appointment.time);
        values.put(APPOINTMENT_COLUMN_NAME_LATITUDE, "" + appointment.latitude);
        values.put(APPOINTMENT_COLUMN_NAME_LONGITUDE, "" + appointment.longitude);
        values.put(APPOINTMENT_COLUMN_NAME_DONE, appointment.done);
        values.put(APPOINTMENT_COLUMN_NAME_SCORE, appointment.score);
        values.put(APPOINTMENT_COLUMN_NAME_PIC, appointment.pic);
        values.put(APPOINTMENT_COLUMN_NAME_CATEGORY, appointment.category);
        values.put(APPOINTMENT_COLUMN_NAME_LOCKED, appointment.locked);

        if(existAppointment(appointment.id)) {
            updateAppointment(appointment);
        }
        else {
            newRowId = db.insert(
                    APPOINTMENT_TABLE_NAME,
                    null,
                    values);
        }

        return newRowId;
    }

    public boolean existAppointment(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(APPOINTMENT_TABLE_NAME,
                null,
                APPOINTMENT_COLUMN_NAME_ID + " = ?",
                new String[] { "" + id }, null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    /**
     * Updating single contact
     */
    public int updateAppointment(Appointment appointment) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(APPOINTMENT_COLUMN_NAME_ID, appointment.id);
        values.put(APPOINTMENT_COLUMN_NAME_TITLE, appointment.title);
        values.put(APPOINTMENT_COLUMN_NAME_DETAIL, appointment.detail);
        values.put(APPOINTMENT_COLUMN_NAME_DATE, appointment.date);
        values.put(APPOINTMENT_COLUMN_NAME_TIME, appointment.time);
        values.put(APPOINTMENT_COLUMN_NAME_LATITUDE, appointment.latitude);
        values.put(APPOINTMENT_COLUMN_NAME_LONGITUDE, appointment.longitude);
        values.put(APPOINTMENT_COLUMN_NAME_DONE, appointment.done);
        values.put(APPOINTMENT_COLUMN_NAME_SCORE, appointment.score);
        values.put(APPOINTMENT_COLUMN_NAME_PIC, appointment.pic);
        values.put(APPOINTMENT_COLUMN_NAME_CATEGORY, appointment.category);
        values.put(APPOINTMENT_COLUMN_NAME_LOCKED, appointment.locked);

        //Updating row
        return db.update(APPOINTMENT_TABLE_NAME, values, APPOINTMENT_COLUMN_NAME_ID + " = ?",
                new String[] { "" + appointment.id });
    }

    public Appointment getAppointment(int id){
        if(existAppointment(id)) {
            SQLiteDatabase db = this.getReadableDatabase();
            Appointment appointment = new Appointment();

            Cursor cursor = db.query(APPOINTMENT_TABLE_NAME,
                    null,
                    APPOINTMENT_COLUMN_NAME_ID + " = ?",
                    new String[] { "" + id }, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                appointment.id = cursor.getInt(0);
                appointment.title = cursor.getString(1);
                appointment.detail = cursor.getString(2);
                appointment.date = cursor.getString(3);
                appointment.time = cursor.getString(4);
                appointment.latitude = Double.parseDouble(cursor.getString(5));
                appointment.longitude = Double.parseDouble(cursor.getString(6));
                appointment.done = cursor.getInt(7);
                appointment.score = cursor.getInt(8);
                appointment.pic = cursor.getBlob(9);
                appointment.category = cursor.getInt(10);
                appointment.locked = cursor.getInt(11);

                cursor.close();
                return appointment;
            }
        }

        return null;
    }

    public ArrayList<Appointment> getAllAppointments() {
        ArrayList<Appointment> appointments = new ArrayList<Appointment>();
        //build the query
        String query = "SELECT * FROM " + APPOINTMENT_TABLE_NAME;

        //get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        //go over each row, build book and add it to list
        Appointment appointment = null;
        if (cursor.moveToFirst()) {
            do {
                appointment = new Appointment();
                appointment.id = cursor.getInt(0);
                appointment.title = cursor.getString(1);
                appointment.detail = cursor.getString(2);
                appointment.date = cursor.getString(3);
                appointment.time = cursor.getString(4);
                appointment.latitude = Double.parseDouble(cursor.getString(5));
                appointment.longitude = Double.parseDouble(cursor.getString(6));
                appointment.done = cursor.getInt(7);
                appointment.score = cursor.getInt(8);
                appointment.pic = cursor.getBlob(9);
                appointment.category = cursor.getInt(10);
                appointment.locked = cursor.getInt(10);

                appointments.add(appointment);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return appointments;
    }

    public void deleteAppointment(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(APPOINTMENT_TABLE_NAME, //table name
                APPOINTMENT_COLUMN_NAME_ID + " = ?",  // selections
                new String[] { "" + id }); //selections args
    }

    public AppointmentStatistic[] getAppointmentStatistics_Weekly(int numOfWeeks, int category) {
        //Initialize all the accomplishment rate
        AppointmentStatistic[] appointmentStatistics = new AppointmentStatistic[numOfWeeks];
        for(int i = 0; i <  numOfWeeks; i++) {
            AppointmentStatistic appointmentStatistic = new AppointmentStatistic();
            appointmentStatistic.rate = 0.0f;
            appointmentStatistic.accomplishedAppointment = 0;
            appointmentStatistic.totalAppointment = 0;
            appointmentStatistic.currentScore = 0;
            appointmentStatistic.totalScore = 0;
            appointmentStatistics[i] = appointmentStatistic;
        }
        //Get all the appointments
        ArrayList<Appointment> appointments = getAllAppointments();
        if(category != 0) {
            for(int i = appointments.size() - 1; i >= 0; i--) {
                if(appointments.get(i).category != category)
                    appointments.remove(i);
            }
        }

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

            for(int j = 0; j <  numOfWeeks; j++) {
                if(diffDays >= j*7 && diffDays < (j+1)*7) {
                    appointmentStatistics[j].totalAppointment++;
                    appointmentStatistics[j].totalScore = appointmentStatistics[j].totalScore + 100;
                    appointmentStatistics[j].currentScore = appointmentStatistics[j].currentScore + appointments.get(i).score;
                    if (appointments.get(i).done == 1) {
                        appointmentStatistics[j].accomplishedAppointment++;
                    }
                }
            }
        }
        for(int i = 0; i <  numOfWeeks; i++) {
            if(appointmentStatistics[i].totalAppointment != 0)
                appointmentStatistics[i].rate = (float) appointmentStatistics[i].accomplishedAppointment / appointmentStatistics[i].totalAppointment;
        }

        return appointmentStatistics;
    }

    public long addGPS(GPSlocation gpsLocation){
        SQLiteDatabase db = this.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(GPS_COLUMN_NAME_TIME, gpsLocation.time);//2014/08/06-15:59:48
        values.put(GPS_COLUMN_NAME_LATITUDE, "" + gpsLocation.latitude);
        values.put(GPS_COLUMN_NAME_LONGITUDE, "" + gpsLocation.longitude);

        long newRowId = db.insert(
                GPS_TABLE_NAME,
                null,
                values);
        return newRowId;
    }

    public ArrayList<GPSlocation> getAllGPSs() {
        ArrayList<GPSlocation> gpsLocations = new ArrayList<GPSlocation>();

        //build the query
        String query = "SELECT * FROM " + GPS_TABLE_NAME;

        //get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        //go over each row, build book and add it to list
        GPSlocation gpsLocation = null;
        if (cursor.moveToFirst()) {
            do {
                gpsLocation = new GPSlocation();
                gpsLocation.time = cursor.getString(0);
                gpsLocation.latitude = Double.parseDouble(cursor.getString(1));
                gpsLocation.longitude = Double.parseDouble(cursor.getString(2));

                gpsLocations.add(gpsLocation);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return gpsLocations;
    }
}
