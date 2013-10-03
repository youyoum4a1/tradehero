package com.tradehero.th.widget.trade;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import com.tradehero.th.adapters.TradeBottomStockPagerAdapter;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 11:03 AM To change this template use File | Settings | File Templates. */
public class BottomViewPager extends ViewPager implements DTOView<SecurityPositionDetailDTO>
{
    private FragmentManager fragmentManager;
    private TradeBottomStockPagerAdapter pagerAdapter;

    //<editor-fold desc="Constructors">
    public BottomViewPager(Context context)
    {
        super(context);
    }

    public BottomViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    public void setFragmentManager(FragmentManager fragmentManager)
    {
        this.fragmentManager = fragmentManager;
        setPagerAdapter();
    }

    private void setPagerAdapter()
    {
        pagerAdapter = new TradeBottomStockPagerAdapter(fragmentManager);
        setAdapter(pagerAdapter);
    }

    public void display(SecurityPositionDetailDTO securityPositionDetailDTO)
    {
        pagerAdapter.display(securityPositionDetailDTO);
    }
}
