package com.tradehero.th.fragments.onboarding;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;
import com.tradehero.th.api.security.key.ExchangeSectorSecurityListType;
import com.tradehero.th.fragments.onboarding.hero.OnBoardPickHeroFragment;
import com.tradehero.th.fragments.onboarding.pref.OnBoardPickExchangeSectorFragment;
import com.tradehero.th.fragments.onboarding.stock.OnBoardPickStockFragment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OnBoardFragmentPagerAdapter extends FragmentStatePagerAdapter
{
    static final int FRAGMENT_PREF = 0;
    static final int FRAGMENT_HERO = 1;
    static final int FRAGMENT_STOCK = 2;

    @NotNull private SparseArray<Fragment> fragments;
    @Nullable ExchangeSectorSecurityListType exchangeSectorSecurityListType;

    //<editor-fold desc="Constructors">
    public OnBoardFragmentPagerAdapter(FragmentManager fm)
    {
        super(fm);
        this.fragments = new SparseArray<>();
    }
    //</editor-fold>

    @Override public int getCount()
    {
        return 3;
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
        fragments.put(position, fragment);
        return fragment;
    }

    @Override public void destroyItem(ViewGroup container, int position, Object object)
    {
        fragments.remove(position);
        super.destroyItem(container, position, object);
    }

    Fragment getFragmentAt(int position)
    {
        return fragments.get(position);
    }
}
