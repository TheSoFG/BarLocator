package com.bytelicious.barlocator.dagger;

import android.content.Context;

import com.bytelicious.barlocator.base.BarLocatorApplication;
import com.bytelicious.barlocator.networking.API;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author ylyubenov
 */

public class DefaultBarLocatorComponent {

    private DefaultBarLocatorComponent() {

    }

    public static BarLocatorComponent create(BarLocatorApplication app) {
        return DaggerBarLocatorComponent
                .builder()
                .managerModule(new ManagerModule())
                .aPIModule(createAPIModule(app))
                .build();
    }

    private static APIModule createAPIModule(Context app) {

        OkHttpClient client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .serializeNulls()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        API api = retrofit.create(API.class);
        return new APIModule(app, api, client, gson, retrofit);
    }

}
