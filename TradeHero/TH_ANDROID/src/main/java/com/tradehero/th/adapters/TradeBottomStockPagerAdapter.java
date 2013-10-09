package com.tradehero.th.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.fragments.trade.ChartFragment;
import com.tradehero.th.fragments.trade.StockInfoFragment;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 12:42 PM To change this template use File | Settings | File Templates. */
public class TradeBottomStockPagerAdapter extends FragmentPagerAdapter implements DTOView<SecurityPositionDetailDTO>
{
    public static final String TAG = TradeBottomStockPagerAdapter.class.getSimpleName();
    public static Class classes[] = new Class[]{ ChartFragment.class, StockInfoFragment.class };

    private SecurityPositionDetailDTO securityPositionDetailDTO;

    //<editor-fold desc="Constructors">
    public TradeBottomStockPagerAdapter(FragmentManager fm)
    {
        this(fm, null);
        THLog.d(TAG, "constructor");
    }

    public TradeBottomStockPagerAdapter(FragmentManager fm, SecurityPositionDetailDTO securityPositionDetailDTO)
    {
        super(fm);
        THLog.d(TAG, "constructor with dto");
        this.securityPositionDetailDTO = securityPositionDetailDTO;
    }
    //</editor-fold>


    @Override
    public int getItemPosition(Object object)
    {
        if (securityPositionDetailDTO != null)
        {
            ((DTOView<SecurityCompactDTO>)object).display(securityPositionDetailDTO.security);
        }
        return POSITION_UNCHANGED;
    }

    public void display(SecurityPositionDetailDTO securityPositionDetailDTO)
    {
        this.securityPositionDetailDTO = securityPositionDetailDTO;
        notifyDataSetChanged();
    }

    @Override public int getCount()
    {
        return classes.length;
    }

    @Override public Fragment getItem(int i)
    {
        Fragment fragment = null;
        try
        {
            fragment = (Fragment)classes[i].newInstance();
        } catch (InstantiationException e)
        {
        } catch (IllegalAccessException e)
        {
        }

        if (securityPositionDetailDTO != null)
        {
            ((DTOView<SecurityCompactDTO>) fragment).display(securityPositionDetailDTO.security);
        }
        return fragment;
    }


}
