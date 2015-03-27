package com.tradehero.th.fragments.security;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.compact.WarrantDTO;
import timber.log.Timber;

public class InfoTopStockPagerAdapter extends FragmentStatePagerAdapter
{

    private SecurityCompactDTO securityCompactDTO;
    private ProviderId providerId;

    //<editor-fold desc="Constructors">
    public InfoTopStockPagerAdapter(Context context, FragmentManager fragmentManager)
    {
        super(fragmentManager);
    }
    //</editor-fold>

    public void linkWith(ProviderId providerId)
    {
        this.providerId = providerId;
    }

    public void linkWith(SecurityCompactDTO securityCompactDTO)
    {
        this.securityCompactDTO = securityCompactDTO;
    }

    public SecurityCompactDTO getSecurityCompactDTO()
    {
        return securityCompactDTO;
    }

    @Override public int getCount()
    {
        if (securityCompactDTO == null)
        {
            return 0;
        }
        else if (securityCompactDTO instanceof WarrantDTO)
        {
            return 3;
        }
        else
        {
            return 2;
        }
    }

    /**
     * Equity: Chart / StockInfo
     * Warrant: WarrantInfo / Chart / StockInfo
     *
     * @param position
     * @return
     */
    @Override public Fragment getItem(int position)
    {
        Fragment fragment;
        Bundle args = new Bundle();
        if (securityCompactDTO instanceof WarrantDTO && position == 0)
        {
            fragment = new WarrantInfoValueFragment();
            populateForWarrantInfoFragment(args);
        }
        else
        {
            if (securityCompactDTO instanceof WarrantDTO)
            {
                position--;
            }

            switch(position)
            {
                case 0:
                case 1:
                    fragment = new StockInfoValueFragment();
                    populateForStockInfoFragment(args);
                    break;

                default:
                    Timber.w("Not supported index %d", position);
                    throw new UnsupportedOperationException("Not implemented");
            }
        }

        fragment.setArguments(args);
        fragment.setRetainInstance(false);
        return fragment;
    }

    private void populateForWarrantInfoFragment(Bundle args)
    {
        if (securityCompactDTO != null)
        {
            WarrantInfoValueFragment.putSecurityId(args, securityCompactDTO.getSecurityId());
        }
        if (providerId != null)
        {
            args.putBundle(WarrantInfoValueFragment.BUNDLE_KEY_PROVIDER_ID_KEY, providerId.getArgs());
        }
    }

    private void populateForStockInfoFragment(Bundle args)
    {
        if (securityCompactDTO != null)
        {
            StockInfoValueFragment.putSecurityId(args, securityCompactDTO.getSecurityId());
        }
    }

    @Override public int getItemPosition(Object object)
    {
        return POSITION_NONE;
    }
}
