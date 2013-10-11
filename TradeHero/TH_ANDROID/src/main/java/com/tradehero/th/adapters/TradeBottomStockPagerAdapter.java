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

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 12:42 PM To change this template use File | Settings | File Templates. */
public class TradeBottomStockPagerAdapter extends FragmentStatePagerAdapter
{
    public static final String TAG = TradeBottomStockPagerAdapter.class.getSimpleName();
    private final Class subViewClasses[] = new Class[] {ChartFragment.class, StockInfoFragment.class, StockInfoFragment.class};
    public final int fragmentCount = subViewClasses.length;

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
        return fragmentCount;
    }

    @Override public Fragment getItem(int position)
    {
        Fragment fragment = Fragment.instantiate(context, subViewClasses[position].getName(), null);
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
