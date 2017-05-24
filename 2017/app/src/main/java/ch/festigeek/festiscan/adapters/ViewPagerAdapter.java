package ch.festigeek.festiscan.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import ch.festigeek.festiscan.fragments.*;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    int mNumberOfTabs;

    public ViewPagerAdapter(FragmentManager fm, int numberOfTabs) {
        super(fm);
        mNumberOfTabs = numberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return new ListUsersFragment();
            default:
                return new ScannerFragment();
        }
    }

    @Override
    public int getCount() {
        return mNumberOfTabs;
    }
}