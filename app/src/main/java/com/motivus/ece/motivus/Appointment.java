package com.motivus.ece.motivus;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dongx on 2015-02-18.
 */
public final class Appointment implements Parcelable {
    public String title;
    public String detail;
    public String date;
    public String time;
    public double latitude;
    public double longitude;
    public boolean check = false;
    public byte[] pic;

    public Appointment() {

    }

    public Appointment(Parcel in) {
        super();
        readFromParcel(in);
    }

    public Appointment(String title, String detail, String date, String time, Double latitude, Double longitude, byte[] pic) {
        super();
        this.title = title;
        this.detail = detail;
        this.date = date;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.done = done;
        this.pic = pic;
    }

    public void readFromParcel(Parcel in) {
        this.title = in.readString();
        this.detail = in.readString();
        this.date = in.readString();
        this.time = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.done = in.readInt();
        in.readByteArray(this.pic);
    }
    public int describeContents() {
        return 0;
    }
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.detail);
        dest.writeString(this.date);
        dest.writeString(this.time);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeInt(this.done);
        dest.writeByteArray(this.pic);
    }

    public static final Creator<Appointment> CREATOR = new Creator<Appointment>() {
        public Appointment createFromParcel(Parcel in) {
            return new Appointment(in);
        }

        public Appointment[] newArray(int size) {
            return new Appointment[size];
        }
    };
}
