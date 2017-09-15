package com.bytelicious.barlocator.map;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bytelicious.barlocator.R;
import com.bytelicious.barlocator.base.BarFragment;
import com.bytelicious.barlocator.model.Bar;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * @author ylyubenov
 */

public class BarMapFragment extends BarFragment implements OnMapReadyCallback {

    public static final String TITLE = "Bar Map";
    private static final String SELECTED_BAR = "BarMapFragment.bar";
    private GoogleMap map;

    private static final int DEFAULT_ZOOM_LEVEL = 18;
    private Bar bar;

    public static BarMapFragment newInstance() {
        return new BarMapFragment();
    }

    private SupportMapFragment mapFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            bar = savedInstanceState.getParcelable(SELECTED_BAR);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bar_map, container, false);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SELECTED_BAR, bar);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        map.clear();
        map = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (bar != null) {
            moveToBar(bar);
        }
    }

    public void onBarSelected(Bar bar) {
        this.bar = bar;
        moveToBar(bar);
    }

    @Override
    public void setBars(ArrayList<Bar> bars, Location location) {
        super.setBars(bars, location);

        resetMarkers(location);
    }

    private void resetMarkers(Location location) {
        map.clear();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Bar bar1 : bars) {
            Marker marker = addMarker(bar1, new LatLng(location.getLatitude(),
                    location.getLongitude()));
            builder.include(marker.getPosition());
        }
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(builder.build().getCenter(),
                DEFAULT_ZOOM_LEVEL));
    }

    private void moveToBar(Bar bar) {
        LatLng barPosition = new LatLng(bar.getGeometry().getLocation().getLat(),
                bar.getGeometry().getLocation().getLng());
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(barPosition, DEFAULT_ZOOM_LEVEL));
    }

    private Marker addMarker(Bar bar, LatLng barPosition) {
        return map.addMarker(new MarkerOptions()
                .position(barPosition)
                .title(bar.getName())
                .snippet(bar.getStreet()));
    }

}