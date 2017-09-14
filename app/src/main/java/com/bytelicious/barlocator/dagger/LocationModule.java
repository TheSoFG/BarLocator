package com.bytelicious.barlocator.dagger;

import com.bytelicious.barlocator.managers.BarLocationManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author ylyubenov
 */

@Module
public class LocationModule {

    private BarLocationManager barLocationManager;

    public LocationModule(BarLocationManager barLocationManager) {
        this.barLocationManager = barLocationManager;
    }

    @Provides
    @Singleton
    BarLocationManager provideLocationManager() {
        return barLocationManager;
    }

}
