package com.bytelicious.barlocator.base;

import android.app.Application;

import com.bytelicious.barlocator.dagger.DI;
import com.bytelicious.barlocator.dagger.DefaultBarLocatorComponent;

/**
 * @author ylyubenov
 */

public class BarLocatorApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initDI();
    }

    private void initDI() {
        DI.init(DefaultBarLocatorComponent.create(this));
    }

}