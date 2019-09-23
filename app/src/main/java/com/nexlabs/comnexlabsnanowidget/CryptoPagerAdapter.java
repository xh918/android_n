package com.nexlabs.comnexlabsnanowidget;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.nexlabs.comnexlabsnanowidget.fragments.HostFragment;
import com.nexlabs.comnexlabsnanowidget.fragments.MarketFragment;
import com.nexlabs.comnexlabsnanowidget.fragments.PreferencesFragment;

import java.util.ArrayList;
import java.util.List;

public class CryptoPagerAdapter extends FragmentPagerAdapter {
    private String titles[] = {
            "Market",
            "Prefs"
    };

    List<Fragment> tabs = new ArrayList<>();

    public CryptoPagerAdapter(FragmentManager fm) {
        super(fm);
        initTabs();
    }

    private void initTabs(){

        PreferencesFragment preferencesFragment = PreferencesFragment.newInstance();
        MarketFragment marketFragment = MarketFragment.newInstance(preferencesFragment);
        tabs.add(HostFragment
                .newInstance(marketFragment));
        tabs.add(HostFragment.newInstance(preferencesFragment));
    }

    @Override
    public Fragment getItem(int i) {
        return tabs.get(i);
    }

    @Override
    public int getCount() {
        return tabs.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

}
