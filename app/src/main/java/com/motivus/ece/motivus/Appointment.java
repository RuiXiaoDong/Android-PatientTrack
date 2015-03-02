package com.motivus.ece.motivus;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dongx on 2015-02-18.
 */
public final class Appointment implements Parcelable {
    public String title;
    public String detail;
    public double latitude;
    public double longitude;
    public byte[] pic;
    public Appointment() {

    }

    public Appointment(Parcel in) {
        super();
        readFromParcel(in);
    }

    public Appointment(String title, String detail, Double latitude, Double longitude, byte[] pic) {
        super();
        this.title = title;
        this.detail = detail;
        this.latitude = latitude;
        this.longitude = longitude;
        this.pic = pic;
    }

    public void readFromParcel(Parcel in) {
        this.title = in.readString();
        this.detail = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        in.readByteArray(this.pic);
    }
    public int describeContents() {
        return 0;
    }
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.detail);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
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
