package com.bytelicious.barlocator.base;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.bytelicious.barlocator.model.Bar;

import java.util.ArrayList;

/**
 * @author ylybenov
 */

public abstract class BarFragment extends Fragment {

    protected ArrayList<Bar> bars;
    protected Location location;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bars = new ArrayList<>();
    }

    public void setBars(ArrayList<Bar> bars) {
        if (this.bars != null) {
            this.bars.clear();
        } else {
            this.bars = new ArrayList<>();
        }
        if (bars != null) {
            this.bars.addAll(bars);
        }
    }

    public void setBars(ArrayList<Bar> bars, Location location) {
        this.location = location;
        setBars(bars);
    }
}

