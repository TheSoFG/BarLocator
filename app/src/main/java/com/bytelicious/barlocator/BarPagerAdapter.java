package com.bytelicious.barlocator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.bytelicious.barlocator.list.BarListFragment;
import com.bytelicious.barlocator.map.BarMapFragment;

/**
 * @author ylyubenov
 */

class BarPagerAdapter extends FragmentStatePagerAdapter {

    private BarListFragment barListFragment;
    private BarMapFragment barMapFragment;

    static final int LIST = 0;
    static final int MAP = 1;

    BarPagerAdapter(FragmentManager fm) {
        super(fm);
        barListFragment = BarListFragment.newInstance();
        barMapFragment = BarMapFragment.newInstance();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return barListFragment;
            case 1:
                return barMapFragment;
            default:
                return barListFragment;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return BarListFragment.TITLE;
            case 1:
                return BarMapFragment.TITLE;
            default:
                return "";
        }
    }
}
