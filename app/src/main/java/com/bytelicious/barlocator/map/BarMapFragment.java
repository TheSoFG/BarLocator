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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import static com.bytelicious.barlocator.Utils.DistanceBetween;

/**
 * @author ylyubenov
 */

public class BarMapFragment extends BarFragment implements OnMapReadyCallback {

    public static final String TITLE = "Bar Map";
    private static final String SELECTED_BAR = "BarMapFragment.barId";
    private static final int DEFAULT_PADDING = 100;
    private GoogleMap map;

    private static final int DEFAULT_ZOOM_LEVEL = 18;
    private String barId;

    public static BarMapFragment newInstance() {
        return new BarMapFragment();
    }

    private SupportMapFragment mapFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            barId = savedInstanceState.getString(SELECTED_BAR);
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
        outState.putString(SELECTED_BAR, barId);
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
        if (barId != null) {
            moveToBar(barId);
        } else if (bars != null && bars.size() > 0) {
            resetMarkers();
        }
    }

    public void onBarSelected(String barId) {
        this.barId = barId;
        moveToBar(barId);
    }

    @Override
    public void setBars(ArrayList<Bar> bars, Location location) {
        super.setBars(bars, location);
        if (map != null) {
            resetMarkers();
        }
    }

    private void resetMarkers() {
        map.clear();
        if (bars.size() > 0) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Bar bar : bars) {
                Marker marker = addMarker(bar);
                builder.include(marker.getPosition());
            }
            addUserPosition();
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),
                    DEFAULT_PADDING));
        }
    }

    private void addUserPosition() {
        if (location != null) {
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                    .title(getString(R.string.youre_here)));
            marker.showInfoWindow();
        }
    }

    private void moveToBar(String barId) {
        Bar selectedBar = null;
        for (Bar bar : bars) {
            if (bar.getId().equals(barId)) {
                selectedBar = bar;
            }
        }
        if (selectedBar != null) {
            LatLng barPosition = new LatLng(selectedBar.getGeometry().getLocation().getLat(),
                    selectedBar.getGeometry().getLocation().getLng());
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(barPosition, DEFAULT_ZOOM_LEVEL));
        }
    }

    private Marker addMarker(Bar bar) {
        double distance = DistanceBetween(location, bar);
        String baseString = getString(R.string.bar_distance);
        return map.addMarker(new MarkerOptions()
                .position(new LatLng(bar.getGeometry().getLocation().getLat(),
                        bar.getGeometry().getLocation().getLng()))
                .title(bar.getName())
                .snippet(String.format(baseString, String.valueOf(distance))));
    }

}