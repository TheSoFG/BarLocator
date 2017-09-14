package com.bytelicious.barlocator.dagger;

/**
 * @author ylyubenov
 */

public class DI {

    private static BarLocatorComponent dependencyInjector;

    public static void init(BarLocatorComponent di) {
        if (dependencyInjector == null) {
            dependencyInjector = di;
        } else {
            throw new IllegalStateException("Already initialized");
        }
    }

    public static BarLocatorComponent getInstance() {
        if (dependencyInjector == null) {
            throw new IllegalStateException("Not initialized. You must call init().");
        }
        return dependencyInjector;
    }

}