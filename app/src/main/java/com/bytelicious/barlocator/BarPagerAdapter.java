package com.bytelicious.barlocator;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.bytelicious.barlocator.base.BarFragment;
import com.bytelicious.barlocator.list.BarListFragment;
import com.bytelicious.barlocator.map.BarMapFragment;

/**
 * @author ylyubenov
 */

public class BarPagerAdapter extends FragmentStatePagerAdapter {

    private BarListFragment barListFragment;
    private BarMapFragment barMapFragment;

    public static final int LIST = 0;
    public static final int MAP = 1;

    public BarPagerAdapter(FragmentManager fm) {
        super(fm);
        barListFragment = BarListFragment.newInstance();
        barMapFragment = BarMapFragment.newInstance();
    }

    @Override
    public BarFragment getItem(int position) {
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
