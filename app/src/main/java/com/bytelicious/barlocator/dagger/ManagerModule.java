package com.bytelicious.barlocator.dagger;

import android.content.Context;

import com.bytelicious.barlocator.managers.BarLocationManager;
import com.bytelicious.barlocator.managers.NetworkManager;
import com.bytelicious.barlocator.networking.API;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author ylyubenov
 */

@Module
public class ManagerModule {

    public ManagerModule() {
    }

    @Provides
    @Singleton
    BarLocationManager provideLocationManager(Context app) {
        return new BarLocationManager(app);
    }

    @Provides
    @Singleton
    NetworkManager provideNetworkManager(API api) {
        return new NetworkManager(api);
    }

}
