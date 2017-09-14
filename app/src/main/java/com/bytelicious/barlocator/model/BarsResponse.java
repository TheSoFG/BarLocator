package com.bytelicious.barlocator.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * @author ylyubenov
 */

public class BarsResponse {

    @Expose
    @SerializedName("results")
    private ArrayList<Bar> bars;

    public ArrayList<Bar> getBars() {
        return bars;
    }
}
