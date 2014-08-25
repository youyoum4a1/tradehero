package com.tradehero.th.fragments.onboarding;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.tradehero.th.fragments.onboarding.hero.OnBoardPickHeroFragment;
import com.tradehero.th.fragments.onboarding.stock.OnBoardPickStockFragment;

public class OnBoardFragmentPagerAdapter extends FragmentStatePagerAdapter
{
    private static final int FRAGMENT_HERO = 0;
    private static final int FRAGMENT_STOCK = 1;

    //<editor-fold desc="Constructors">
    public OnBoardFragmentPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }
    //</editor-fold>

    @Override public int getCount()
    {
        return 2;
    }

    @Override public Fragment getItem(int position)
    {
        Fragment fragment;
        switch(position)
        {
            case FRAGMENT_HERO:
                fragment = new OnBoardPickHeroFragment();
                break;
            case FRAGMENT_STOCK:
                fragment = new OnBoardPickStockFragment();
                break;
            default:
                throw new IllegalArgumentException("Unhandled position " + position);
        }
        fragment.setArguments(new Bundle());
        return fragment;
    }
}
