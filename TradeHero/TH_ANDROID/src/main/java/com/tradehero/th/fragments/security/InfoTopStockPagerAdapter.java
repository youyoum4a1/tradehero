package com.tradehero.th.fragments.security;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.WarrantDTO;
import com.tradehero.th.utils.yahoo.ChartSize;
import com.tradehero.th.utils.yahoo.TimeSpan;

/** Created with IntelliJ IDEA. User: xavier Date: 10/31/13 Time: 12:42 PM To change this template use File | Settings | File Templates. */
public class InfoTopStockPagerAdapter extends FragmentStatePagerAdapter
{
    public static final String TAG = InfoTopStockPagerAdapter.class.getSimpleName();

    private final Context context;

    private SecurityCompactDTO securityCompactDTO;
    private ProviderId providerId;

    //<editor-fold desc="Constructors">
    public InfoTopStockPagerAdapter(Context context, FragmentManager fragmentManager)
    {
        super(fragmentManager);
        this.context = context;
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
        if (securityCompactDTO != null)
        {
            args.putBundle(AbstractSecurityInfoFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityCompactDTO.getSecurityId().getArgs());
        }

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
                    fragment = new ChartFragment();
                    populateForChartFragment(args);
                    break;
                case 1:
                    fragment = new StockInfoValueFragment();
                    break;

                default:
                    THLog.w(TAG, "Not supported index " + position);
                    throw new UnsupportedOperationException("Not implemented");
            }
        }

        fragment.setArguments(args);
        fragment.setRetainInstance(false);
        return fragment;
    }

    private void populateForWarrantInfoFragment(Bundle args)
    {
        if (providerId != null)
        {
            args.putBundle(WarrantInfoValueFragment.BUNDLE_KEY_PROVIDER_ID_KEY, providerId.getArgs());
        }
    }

    private void populateForChartFragment(Bundle args)
    {
        args.putInt(ChartFragment.BUNDLE_KEY_TIME_SPAN_BUTTON_SET_VISIBILITY, View.VISIBLE);
        args.putString(ChartFragment.BUNDLE_KEY_TIME_SPAN_STRING, TimeSpan.month3.name());
        args.putString(ChartFragment.BUNDLE_KEY_CHART_SIZE, ChartSize.medium.name()); // TODO have a util class that decides on size
    }

    @Override public int getItemPosition(Object object)
    {
        return POSITION_NONE;
    }
}
