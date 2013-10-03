package com.tradehero.th.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.fragments.trade.StockInfoFragment;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 12:42 PM To change this template use File | Settings | File Templates. */
public class TradeBottomStockPagerAdapter extends FragmentPagerAdapter implements DTOView<SecurityPositionDetailDTO>
{
    private SecurityPositionDetailDTO securityPositionDetailDTO;

    //<editor-fold desc="Constructors">
    public TradeBottomStockPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    public TradeBottomStockPagerAdapter(FragmentManager fm, SecurityPositionDetailDTO securityPositionDetailDTO)
    {
        super(fm);
        this.securityPositionDetailDTO = securityPositionDetailDTO;
    }
    //</editor-fold>

    public void display(SecurityPositionDetailDTO securityPositionDetailDTO)
    {
        this.securityPositionDetailDTO = securityPositionDetailDTO;
        notifyDataSetChanged();
    }

    @Override public int getCount()
    {
        return 3;
    }

    @Override public Fragment getItem(int i)
    {
        Fragment fragment = null;
        if (securityPositionDetailDTO != null)
        {
            switch(i)
            {
                case 0:
                    // graph view
                    // TEMP
                    fragment = new StockInfoFragment();
                    ((StockInfoFragment)fragment).display(securityPositionDetailDTO.security);
                    break;
                case 1:
                    fragment = new StockInfoFragment();
                    ((StockInfoFragment)fragment).display(securityPositionDetailDTO.security);
                    break;
                case 2:
                    // news view
                    // TEMP
                    fragment = new StockInfoFragment();
                    ((StockInfoFragment)fragment).display(securityPositionDetailDTO.security);
                    break;
                default:
                    throw new IllegalArgumentException("Cannot handle i=" + i);
            }
        }
        else
        {
            fragment = new StockInfoFragment();
        }

        return fragment;
    }


}
