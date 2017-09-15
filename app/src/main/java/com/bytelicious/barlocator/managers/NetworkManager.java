package com.bytelicious.barlocator.managers;

import android.location.Location;
import android.support.annotation.NonNull;

import com.bytelicious.barlocator.model.Bar;
import com.bytelicious.barlocator.model.BarsResponse;
import com.bytelicious.barlocator.networking.API;

import java.util.ArrayList;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author ylyubenov
 */

public class NetworkManager {

    @Inject
    API api;

    public interface OnBarsReadyListener {
        void onBarsSuccess(ArrayList<Bar> bars, Location location);
        void onBarsFailure();
    }

    private OnBarsReadyListener onBarsReadyListener;

    public NetworkManager (API api) {
        this.api = api;
    }

    public void setOnBarsReadyListener(OnBarsReadyListener onBarsReadyListener) {
        this.onBarsReadyListener = onBarsReadyListener;
    }

    public void getBars(final Location location, int defaultRadius, String apiKey) {
        api.getBars(location.getLatitude() + "," + location.getLongitude(), defaultRadius, apiKey)
                .enqueue(new Callback<BarsResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<BarsResponse> call,
                                           @NonNull Response<BarsResponse> response) {
                        if(onBarsReadyListener != null) {
                            if (response.isSuccessful() && response.body() != null) {
                                onBarsReadyListener.onBarsSuccess(response.body().getBars(),
                                        location);
                            } else {
                                onBarsReadyListener.onBarsFailure();
                            }
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<BarsResponse> call, @NonNull Throwable t) {
                        onBarsReadyListener.onBarsFailure();
                    }
                });

    }

}