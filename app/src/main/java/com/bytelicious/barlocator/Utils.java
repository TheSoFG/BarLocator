package com.bytelicious.barlocator;

import android.location.Location;

import com.bytelicious.barlocator.model.Bar;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author ylyubenov
 */

public class Utils {

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double DistanceBetween(Location location, Bar bar) {
        float[] results = new float[3];
        Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                bar.getGeometry().getLocation().getLat(),
                bar.getGeometry().getLocation().getLng(), results);
        return Utils.round(results[0], 2);
    }

}