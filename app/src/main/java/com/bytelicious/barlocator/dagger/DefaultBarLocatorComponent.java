package com.bytelicious.barlocator.dagger;

import com.bytelicious.barlocator.base.BarLocatorApplication;
import com.bytelicious.barlocator.managers.BarLocationManager;
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
                .locationModule(new LocationModule(new BarLocationManager(app)))
                .aPIModule(createAPIModule())
                .build();
    }

    private static APIModule createAPIModule() {

        OkHttpClient client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .followRedirects(true)
                .followSslRedirects(true)
                .build();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .excludeFieldsWithoutExposeAnnotation()
                .serializeNulls()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        API api = retrofit.create(API.class);
        return new APIModule(api, client, gson, retrofit);
    }

}
