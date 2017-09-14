package com.bytelicious.barlocator.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author ylyubenov
 */

public class BarGeometry implements Parcelable{

    @Expose
    @SerializedName("location")
    private BarLocation location;

    public static final Creator<BarGeometry> CREATOR = new Creator<BarGeometry>() {
        @Override
        public BarGeometry createFromParcel(Parcel in) {
            return new BarGeometry(in);
        }

        @Override
        public BarGeometry[] newArray(int size) {
            return new BarGeometry[size];
        }
    };

    public BarGeometry() {

    }

    protected BarGeometry(Parcel in) {
        location = in.readParcelable(BarLocation.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(location, i);
    }

    public BarLocation getLocation() {
        return location;
    }

}