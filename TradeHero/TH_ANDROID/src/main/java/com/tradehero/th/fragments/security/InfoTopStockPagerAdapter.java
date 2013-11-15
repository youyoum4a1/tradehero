package com.tradehero.th.fragments.security;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.utils.yahoo.ChartSize;
import com.tradehero.th.utils.yahoo.TimeSpan;

/** Created with IntelliJ IDEA. User: xavier Date: 10/31/13 Time: 12:42 PM To change this template use File | Settings | File Templates. */
public class InfoTopStockPagerAdapter extends FragmentStatePagerAdapter
{
    public static final String TAG = InfoTopStockPagerAdapter.class.getSimpleName();

    private final Context context;

    private SecurityId securityId;

    //<editor-fold desc="Constructors">
    public InfoTopStockPagerAdapter(Context context, FragmentManager fragmentManager)
    {
        super(fragmentManager);
        this.context = context;
    }
    //</editor-fold>

    public void linkWith(SecurityId securityId)
    {
        this.securityId = securityId;
    }

    public SecurityId getSecurityId()
    {
        return securityId;
    }

    @Override public int getCount()
    {
        return 2;
    }

    @Override public Fragment getItem(int position)
    {
        Fragment fragment = null;
        Bundle args = new Bundle();
        if (securityId != null)
        {
            securityId.putParameters(args);
        }
        switch(position)
        {
            case 0:
                fragment = new ChartFragment();
                args.putInt(ChartFragment.BUNDLE_KEY_TIME_SPAN_BUTTON_SET_VISIBILITY, View.VISIBLE);
                args.putString(ChartFragment.BUNDLE_KEY_TIME_SPAN_STRING, TimeSpan.month3.name());
                args.putString(ChartFragment.BUNDLE_KEY_CHART_SIZE, ChartSize.medium.name()); // TODO have a util class that decides on size
                break;
            case 1:
                fragment = new StockInfoValueFragment();
                break;

            default:
                THLog.i(TAG, "Not supported index " + position);
        }

        fragment.setArguments(args);
        fragment.setRetainInstance(false);
        return fragment;
    }

    @Override public int getItemPosition(Object object)
    {
        return POSITION_NONE;
    }
}
