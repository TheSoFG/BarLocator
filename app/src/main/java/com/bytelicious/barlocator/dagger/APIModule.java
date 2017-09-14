package com.bytelicious.barlocator.dagger;

import android.content.Context;

import com.bytelicious.barlocator.networking.API;
import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * @author ylyubenov
 */
@Module
public class APIModule {

    private OkHttpClient okHttpClient;
    private Retrofit retrofit;
    private API restClient;
    private Context app;
    private Gson gson;

    public APIModule(Context app, API restClient, OkHttpClient okHttpClient, Gson gson, Retrofit retrofit) {
        this.app = app;
        this.gson = gson;
        this.retrofit = retrofit;
        this.restClient = restClient;
        this.okHttpClient = okHttpClient;
    }

    @Provides
    @Singleton
    API provideRestClient() {
        return restClient;
    }

    @Provides
    @Singleton
    OkHttpClient provideHttpClient() {
        return okHttpClient;
    }

    @Provides
    @Singleton
    Gson provideGson() {
        return gson;
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit() {
        return retrofit;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return app;
    }

}
