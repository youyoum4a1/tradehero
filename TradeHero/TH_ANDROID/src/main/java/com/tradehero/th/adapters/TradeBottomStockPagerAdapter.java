package com.tradehero.th.adapters;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.trade.ChartFragment;
import com.tradehero.th.fragments.trade.StockInfoFragment;
import com.tradehero.th.fragments.trade.YahooNewsFragment;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 12:42 PM To change this template use File | Settings | File Templates. */
public class TradeBottomStockPagerAdapter extends FragmentStatePagerAdapter
{
    public static final String TAG = TradeBottomStockPagerAdapter.class.getSimpleName();

    private final Context context;

    private SecurityId securityId;

    //<editor-fold desc="Constructors">
    public TradeBottomStockPagerAdapter(Context context, FragmentManager fragmentManager)
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
        return 3;
    }

    @Override public Fragment getItem(int position)
    {
        Fragment fragment = null;
        switch(position)
        {
            case 0:
                fragment = new ChartFragment();
                break;
            case 1:
                fragment = new StockInfoFragment();
                break;
            case 2:
                fragment = new YahooNewsFragment();

            default:
                THLog.i(TAG, "Not supported index " + position);
        }

        if (securityId != null)
        {
            fragment.setArguments(securityId.getArgs());
        }
        fragment.setRetainInstance(false);
        return fragment;
    }

    @Override public int getItemPosition(Object object)
    {
        return POSITION_NONE;
    }
}
