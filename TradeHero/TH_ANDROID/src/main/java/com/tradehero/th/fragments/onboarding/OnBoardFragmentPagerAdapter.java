package com.tradehero.th.fragments.onboarding;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.tradehero.th.api.security.key.ExchangeSectorSecurityListType;
import com.tradehero.th.fragments.onboarding.hero.OnBoardPickHeroFragment;
import com.tradehero.th.fragments.onboarding.pref.OnBoardPickExchangeSectorFragment;
import com.tradehero.th.fragments.onboarding.stock.OnBoardPickStockFragment;
import org.jetbrains.annotations.Nullable;

public class OnBoardFragmentPagerAdapter extends FragmentStatePagerAdapter
{
    private static final int FRAGMENT_PREF = 0;
    private static final int FRAGMENT_HERO = 1;
    private static final int FRAGMENT_STOCK = 2;

    @Nullable ExchangeSectorSecurityListType exchangeSectorSecurityListType;

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
            case FRAGMENT_PREF:
                fragment = new OnBoardPickExchangeSectorFragment();
                break;
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

    public void setExchangeSectorSecurityListType(@Nullable ExchangeSectorSecurityListType exchangeSectorSecurityListType)
    {
        this.exchangeSectorSecurityListType = exchangeSectorSecurityListType;
        ((OnBoardPickStockFragment) getItem(FRAGMENT_STOCK))
                .setExchangeSectorSecurityListType(exchangeSectorSecurityListType);
        ((OnBoardPickHeroFragment) getItem(FRAGMENT_HERO))
                .setExchangeSectorSecurityListType(exchangeSectorSecurityListType);
    }
}
