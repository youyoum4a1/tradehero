package com.tradehero.th.fragments.live.ayondo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SignUpLiveAyondoPagerAdapter extends FragmentPagerAdapter
{
    @NonNull private final Bundle args;
    @NonNull private final Fragment[] fragments = new Fragment[] {
            new LiveSignUpStep1AyondoFragment(),
            new LiveSignUpStep2AyondoFragment(),
            new LiveSignUpStep3AyondoFragment(),
            new LiveSignUpStep4AyondoFragment(),
            new LiveSignUpStep5AyondoFragment()};

    public SignUpLiveAyondoPagerAdapter(@NonNull FragmentManager fm, @NonNull Bundle args)
    {
        super(fm);
        this.args = args;
    }

    @Override public int getCount()
    {
        return fragments.length;
    }

    @Override public CharSequence getPageTitle(int position)
    {
        return String.valueOf(position + 1);
    }

    @Override public Fragment getItem(int position)
    {
        Fragment f = fragments[position];
        f.setArguments(args);
        return f;
    }
}
