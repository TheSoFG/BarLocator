package com.bytelicious.barlocator.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author ylyubenov
 */

public class Bar implements Parcelable {

    @Expose
    @SerializedName("name")
    private String name;
    @Expose
    @SerializedName("geometry")
    private BarGeometry geometry;
    @Expose
    @SerializedName("id")
    private String id;
    @Expose
    @SerializedName("vicinity")
    private String street;

    public static final Creator<Bar> CREATOR = new Creator<Bar>() {
        @Override
        public Bar createFromParcel(Parcel in) {
            return new Bar(in);
        }

        @Override
        public Bar[] newArray(int size) {
            return new Bar[size];
        }
    };

    public Bar(String name, BarGeometry geometry, String id, String street) {
        this.geometry = geometry;
        this.street = street;
        this.name = name;
        this.id = id;
    }

    protected Bar(Parcel in) {
        geometry = in.readParcelable(BarLocation.class.getClassLoader());
        street = in.readString();
        name = in.readString();
        id = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(getGeometry(), i);
        parcel.writeString(getStreet());
        parcel.writeString(getName());
        parcel.writeString(getId());
    }

    public BarGeometry getGeometry() {
        return geometry;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getStreet() {
        return street;
    }
}