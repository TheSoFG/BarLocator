package com.bytelicious.barlocator.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author ylyubenov
 */

public class BarLocation implements Parcelable {

    @Expose
    @SerializedName("lat")
    private double lat;

    @Expose
    @SerializedName("lng")
    private double lng;

    public BarLocation(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    protected BarLocation(Parcel in) {
        lat = in.readDouble();
        lng = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(lat);
        parcel.writeDouble(lng);
    }

    public static final Creator<BarLocation> CREATOR = new Creator<BarLocation>() {
        @Override
        public BarLocation createFromParcel(Parcel in) {
            return new BarLocation(in);
        }

        @Override
        public BarLocation[] newArray(int size) {
            return new BarLocation[size];
        }
    };

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

}