package com.motivus.ece.motivus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by dongx on 2015-02-18.
 */
public class Database extends SQLiteOpenHelper {
    //Singleton
    private static Database mInstance = null;

    public static final String DATABASE_NAME = "Appointments.db";
    public static final int DATABASE_VERSION = 1;

    //Appointment table
    public static final String APPOINTMENT_TABLE_NAME = "appointments";
    public static final String APPOINTMENT_COLUMN_NAME_TITLE = "title";
    public static final String APPOINTMENT_COLUMN_NAME_DETAIL = "detail";
    public static final String APPOINTMENT_COLUMN_NAME_DATE = "date";
    public static final String APPOINTMENT_COLUMN_NAME_TIME = "time";
    public static final String APPOINTMENT_COLUMN_NAME_LATITUDE = "latitude";
    public static final String APPOINTMENT_COLUMN_NAME_LONGITUDE = "longitude";
    public static final String APPOINTMENT_COLUMN_NAME_DONE = "done";
    public static final String APPOINTMENT_COLUMN_NAME_PIC = "pic";

    public static final String APPOINTMENT_SQL_CREATE_ENTRIES =
            "CREATE TABLE " + APPOINTMENT_TABLE_NAME + " (" +
                    APPOINTMENT_COLUMN_NAME_TITLE + " TEXT PRIMARY KEY," +
                    APPOINTMENT_COLUMN_NAME_DETAIL + " TEXT," +
                    APPOINTMENT_COLUMN_NAME_DATE + " TEXT," +
                    APPOINTMENT_COLUMN_NAME_TIME + " TEXT," +
                    APPOINTMENT_COLUMN_NAME_LATITUDE + " TEXT," +
                    APPOINTMENT_COLUMN_NAME_LONGITUDE + " TEXT," +
                    APPOINTMENT_COLUMN_NAME_DONE + " INTEGER," +
                    APPOINTMENT_COLUMN_NAME_PIC + " BLOB" +
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

    public static Database getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new Database(ctx.getApplicationContext());
        }
        return mInstance;
    }

    private Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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

    public long addAppointment(Appointment appointment){
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(APPOINTMENT_COLUMN_NAME_TITLE, appointment.title);
        values.put(APPOINTMENT_COLUMN_NAME_DETAIL, appointment.detail);
        values.put(APPOINTMENT_COLUMN_NAME_DATE, appointment.date);
        values.put(APPOINTMENT_COLUMN_NAME_TIME, appointment.time);
        values.put(APPOINTMENT_COLUMN_NAME_LATITUDE, "" + appointment.latitude);
        values.put(APPOINTMENT_COLUMN_NAME_LONGITUDE, "" + appointment.longitude);
        values.put(APPOINTMENT_COLUMN_NAME_DONE, appointment.done);
        values.put(APPOINTMENT_COLUMN_NAME_PIC, appointment.pic);

        long newRowId = -1;
        if(existAppointment(appointment.title)) {
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

    public boolean existAppointment(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(APPOINTMENT_TABLE_NAME,
                null,
                APPOINTMENT_COLUMN_NAME_TITLE + " = ?",
                new String[] { name }, null, null, null);
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
        values.put(APPOINTMENT_COLUMN_NAME_TITLE, appointment.title);
        values.put(APPOINTMENT_COLUMN_NAME_DETAIL, appointment.detail);
        values.put(APPOINTMENT_COLUMN_NAME_DATE, appointment.date);
        values.put(APPOINTMENT_COLUMN_NAME_TIME, appointment.time);
        values.put(APPOINTMENT_COLUMN_NAME_LATITUDE, appointment.latitude);
        values.put(APPOINTMENT_COLUMN_NAME_LONGITUDE, appointment.longitude);
        values.put(APPOINTMENT_COLUMN_NAME_DONE, appointment.done);
        values.put(APPOINTMENT_COLUMN_NAME_PIC, appointment.pic);

        //Updating row
        return db.update(APPOINTMENT_TABLE_NAME, values, APPOINTMENT_COLUMN_NAME_TITLE + " = ?",
                new String[] { appointment.title });
    }

    public Appointment getAppointment(String name){
        if(existAppointment(name)) {
            SQLiteDatabase db = this.getReadableDatabase();
            Appointment appointment = new Appointment();

            Cursor cursor = db.query(APPOINTMENT_TABLE_NAME,
                    null,
                    APPOINTMENT_COLUMN_NAME_TITLE + " = ?",
                    new String[] { name }, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                appointment.title = cursor.getString(0);
                appointment.detail = cursor.getString(1);
                appointment.date = cursor.getString(2);
                appointment.time = cursor.getString(3);
                appointment.latitude = Double.parseDouble(cursor.getString(4));
                appointment.longitude = Double.parseDouble(cursor.getString(5));
                appointment.done = cursor.getInt(6);
                appointment.pic = cursor.getBlob(7);

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
                appointment.title = cursor.getString(0);
                appointment.detail = cursor.getString(1);
                appointment.date = cursor.getString(2);
                appointment.time = cursor.getString(3);
                appointment.latitude = Double.parseDouble(cursor.getString(4));
                appointment.longitude = Double.parseDouble(cursor.getString(5));
                appointment.done = cursor.getInt(6);
                appointment.pic = cursor.getBlob(7);

                appointments.add(appointment);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return appointments;
    }

    public void deleteAppointment(String appointmentTitle) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(APPOINTMENT_TABLE_NAME, //table name
                APPOINTMENT_COLUMN_NAME_TITLE + " = ?",  // selections
                new String[] { appointmentTitle }); //selections args
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
