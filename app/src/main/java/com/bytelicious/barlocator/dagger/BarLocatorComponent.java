package com.bytelicious.barlocator.dagger;

import com.bytelicious.barlocator.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author ylyubenov
 */

@Component(modules = {APIModule.class, ManagerModule.class})
@Singleton
public interface BarLocatorComponent {

    void inject(MainActivity mainActivity);

}
