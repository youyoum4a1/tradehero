package com.tradehero.th.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.fragments.trade.ChartFragment;
import com.tradehero.th.fragments.trade.StockInfoFragment;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 12:42 PM To change this template use File | Settings | File Templates. */
public class TradeBottomStockPagerAdapter extends FragmentStatePagerAdapter implements DTOView<SecurityCompactDTO>
{
    public static final String TAG = TradeBottomStockPagerAdapter.class.getSimpleName();
    public final Class subViewClasses[] = new Class[] {ChartFragment.class, StockInfoFragment.class, StockInfoFragment.class};

    private Fragment[] subViews;
    private final Context context;

    private SecurityCompactDTO securityCompactDTO;

    //<editor-fold desc="Constructors">

    public TradeBottomStockPagerAdapter(Context context, FragmentManager fragmentManager)
    {
        super(fragmentManager);
        this.context = context;
    }
    //</editor-fold>

    @Override public void display(SecurityCompactDTO securityCompactDTO)
    {
        this.securityCompactDTO = securityCompactDTO;
    }

    @Override public int getCount()
    {
        return subViewClasses.length;
    }

    @Override public void notifyDataSetChanged()
    {
        initFragments();
        super.notifyDataSetChanged();
    }

    private void initFragments()
    {
        if (subViews == null)
        {
            subViews = new Fragment[subViewClasses.length];
        }
        for (int i = 0; i < subViews.length; ++i)
        {
            if (subViews[i] == null)
            {
                subViews[i] = Fragment.instantiate(context, subViewClasses[i].getName(), null);
            }
            if (subViews[i] != null && securityCompactDTO != null)
            {
                ((DTOView<SecurityCompactDTO>) subViews[i]).display(securityCompactDTO);
            }
        }
    }

    @Override public Fragment getItem(int position)
    {
        initFragments();
        return subViews[position];
    }
}
