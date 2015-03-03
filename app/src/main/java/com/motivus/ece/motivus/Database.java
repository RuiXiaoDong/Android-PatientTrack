package com.motivus.ece.motivus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by dongx on 2015-02-18.
 */
public class Database extends SQLiteOpenHelper {
    //Singleton
    private static Database mInstance = null;

    public static final String TABLE_NAME = "appointments";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Appointments.db";
    public static final String COLUMN_NAME_TITLE = "title";
    public static final String COLUMN_NAME_DETAIL = "detail";
    public static final String COLUMN_NAME_LATITUDE = "latitude";
    public static final String COLUMN_NAME_LONGITUDE = "longitude";
    public static final String COLUMN_NAME_PIC = "pic";
    public static final String[] COLUMNS = {COLUMN_NAME_TITLE,COLUMN_NAME_DETAIL,COLUMN_NAME_PIC};

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_NAME_TITLE + " TEXT PRIMARY KEY," +
                    COLUMN_NAME_DETAIL + " TEXT," +
                    COLUMN_NAME_LATITUDE + " TEXT," +
                    COLUMN_NAME_LONGITUDE + " TEXT," +
                    COLUMN_NAME_PIC + " BLOB" +
            " )";
    public static final String SQL_DELETE_DATABASE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

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
            db.execSQL(SQL_DELETE_ENTRIES);
            db.execSQL(SQL_CREATE_ENTRIES);
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
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
        values.put(COLUMN_NAME_TITLE, appointment.title);
        values.put(COLUMN_NAME_DETAIL, appointment.detail);
        values.put(COLUMN_NAME_LATITUDE, "" + appointment.latitude);
        values.put(COLUMN_NAME_LONGITUDE, "" + appointment.longitude);
        values.put(COLUMN_NAME_PIC, appointment.pic);

        long newRowId = -1;
        if(existAppointment(appointment.title)) {
            updateAppointment(appointment);
        }
        else {
            newRowId = db.insert(
                    TABLE_NAME,
                    null,
                    values);
        }

        return newRowId;
    }

    public boolean existAppointment(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db. query(TABLE_NAME,
                COLUMNS,
                COLUMN_NAME_TITLE + " = '"+ name + "'",
                null, null, null, null);
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
        values.put(COLUMN_NAME_TITLE, appointment.title);
        values.put(COLUMN_NAME_DETAIL, appointment.detail);
        values.put(COLUMN_NAME_LATITUDE, appointment.latitude);
        values.put(COLUMN_NAME_LONGITUDE, appointment.longitude);
        values.put(COLUMN_NAME_PIC, appointment.pic);

        //Updating row
        return db.update(TABLE_NAME, values, COLUMN_NAME_TITLE + " = ?",
                new String[] { appointment.title });
    }

    public Appointment getAppointment(String name){
        if(existAppointment(name)) {
            SQLiteDatabase db = this.getReadableDatabase();
            Appointment appointment = new Appointment();

            Cursor cursor = db.query(TABLE_NAME,
                    COLUMNS,
                    COLUMN_NAME_TITLE + " = '" + name + "'",
                    null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                appointment.title = cursor.getString(0);
                appointment.detail = cursor.getString(1);
                appointment.pic = cursor.getBlob(2);

                cursor.close();
                return appointment;
            }
        }

        return null;
    }

    public ArrayList<Appointment> getAllAppointments() {
        ArrayList<Appointment> appointments = new ArrayList<Appointment>();

        //build the query
        String query = "SELECT * FROM " + TABLE_NAME;

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
                appointment.latitude = Double.parseDouble(cursor.getString(2));
                appointment.longitude = Double.parseDouble(cursor.getString(3));
                appointment.pic = cursor.getBlob(2);

                appointments.add(appointment);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return appointments;
    }

    public void deleteAppointment(Appointment appointment) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NAME, //table name
                COLUMN_NAME_TITLE + " = ?",  // selections
                new String[] { appointment.title }); //selections args
    }
}
