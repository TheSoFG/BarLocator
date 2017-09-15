package com.bytelicious.barlocator.managers;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import javax.inject.Inject;

/**
 * @author ylyubenov
 */

public class BarLocationManager implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener {

    private static final String TAG = BarLocationManager.class.getSimpleName();
    private final Context app;

    private Location location;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    private ConnectionListener connectionListener;

    public interface ConnectionListener {
        void onConnectionFailed(@NonNull ConnectionResult connectionResult);

        void onConnectionSuspended(int i);

        void onConnected(Bundle bundle);

        void onLocationChanged(Location location);
    }

    @Inject
    public BarLocationManager(Context app) {
        this.app = app;
        createLocationRequest();
        createGoogleClient();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        if (connectionListener != null) {
            connectionListener.onLocationChanged(location);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionListener != null) {
            connectionListener.onConnectionFailed(connectionResult);
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        if (connectionListener != null) {
            connectionListener.onConnected(bundle);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        if (connectionListener != null) {
            connectionListener.onConnectionSuspended(i);
        }
    }

    public void requestLocation() throws SecurityException {
        if (isConnected()) {
            location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                        locationRequest, this);
            } else {
                if (connectionListener != null) {
                    connectionListener.onLocationChanged(location);
                }
            }
        }
    }

    public Location getLocation() {
        return location;
    }

    public void setConnectionListener(ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
        if (connectionListener == null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    private void createGoogleClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(app)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            googleApiClient.connect();
        }
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    private boolean isConnected() {
        return googleApiClient != null && googleApiClient.isConnected();
    }
}
