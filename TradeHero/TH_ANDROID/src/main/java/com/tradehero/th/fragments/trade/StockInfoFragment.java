/**
 * StockInfoFragment.java 
 * TradeHero
 *
 * Created by @author Siddesh Bingi on Jul 26, 2013
 */
package com.tradehero.th.fragments.trade;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.NumberDisplayUtils;
import com.tradehero.th.utils.SecurityUtils;
import dagger.Lazy;
import javax.inject.Inject;
import sun.security.util.SecurityConstants;

public class StockInfoFragment extends AbstractSecurityInfoFragment<SecurityCompactDTO>
{
    private final static String TAG = StockInfoFragment.class.getSimpleName();

    private TextView mPreviousClose;
    private TextView mOpen;
    private TextView mDaysHigh;
    private TextView mDaysLow;
    private TextView mMarketCap;
    private TextView mPERatio;
    private TextView mEps;
    private TextView mVolume;
    private TextView mAvgVolume;

    @Inject protected Lazy<SecurityCompactCache> securityCompactCache;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = null;
        view = inflater.inflate(R.layout.fragment_stockinfo, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View v)
    {
        mPreviousClose = (TextView) v.findViewById(R.id.vprevious_close);
        mOpen = (TextView) v.findViewById(R.id.vopen);
        mDaysHigh = (TextView) v.findViewById(R.id.vdays_high);
        mDaysLow = (TextView) v.findViewById(R.id.vdays_low);
        mMarketCap = (TextView) v.findViewById(R.id.vmarket_cap);
        mPERatio = (TextView) v.findViewById(R.id.vpe_ratio);
        mEps = (TextView) v.findViewById(R.id.veps);
        mVolume = (TextView) v.findViewById(R.id.vvolume);
        mAvgVolume = (TextView) v.findViewById(R.id.vavg_volume);
    }

    @Override public void onPause()
    {
        if (securityId != null)
        {
            securityCompactCache.get().unRegisterListener(this);
        }
        super.onPause();
    }

    @Override public void linkWith(SecurityId securityId, boolean andDisplay)
    {
        super.linkWith(securityId, andDisplay);
        if (this.securityId != null)
        {
            securityCompactCache.get().registerListener(this);
            linkWith(securityCompactCache.get().get(this.securityId), andDisplay);
        }
    }

    //<editor-fold desc="Display Methods">
    public void display()
    {
        displayPreviousClose();
        displayOpen();
        displayDaysHigh();
        displayDaysLow();
        displayMarketCap();
        displayPERatio();
        displayEps();
        displayVolume();
        displayAvgVolume();
    }

    public void displayPreviousClose()
    {
        if (mPreviousClose != null)
        {
            if (value == null || value.previousClose == null)
            {
                mPreviousClose.setText(R.string.na);
            }
            else
            {
                mPreviousClose.setText(String.format("%s %,.3f", value.currencyDisplay, value.previousClose.doubleValue()));
            }
        }
    }

    public void displayOpen()
    {
        if (mOpen != null)
        {
            if (value == null || value.open == null)
            {
                mOpen.setText(R.string.na);
            }
            else
            {
                mOpen.setText(String.format("%s %,.2f", value.currencyDisplay, value.open.doubleValue()));
            }
        }
        //double open = YUtils.parseQuoteValue(yQuotes.get("Open"));
    }

    public void displayDaysHigh()
    {
        if (mDaysHigh != null)
        {
            if (value == null || value.high == null)
            {
                mDaysHigh.setText(R.string.na);
            }
            else
            {
                mDaysHigh.setText(String.format("%s %,.2f", value.currencyDisplay, value.high.doubleValue()));
            }
        }
        //double daysHigh = YUtils.parseQuoteValue(yQuotes.get("Day's High"));
    }

    public void displayDaysLow()
    {
        if (mDaysLow != null)
        {
            if (value == null || value.low == null)
            {
                mDaysLow.setText(R.string.na);
            }
            else
            {
                mDaysLow.setText(String.format("%s %,.2f", value.currencyDisplay, value.low.doubleValue()));
            }
        }
        //double daysLow = YUtils.parseQuoteValue(yQuotes.get("Day's Low"));
    }

    public void displayMarketCap()
    {
        if (mMarketCap != null)
        {
            if (value == null || value.marketCap == null)
            {
                mMarketCap.setText(R.string.na);
            }
            else
            {
                mMarketCap.setText(String.format("%s %s", SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY, NumberDisplayUtils.formatWithRelevantDigits(value.marketCap.doubleValue(), 4)));
            }
        }
    }

    public void displayPERatio()
    {
        if (mPERatio != null)
        {
            if (value == null || value.pe == null)
            {
                mPERatio.setText(R.string.na);
            }
            else
            {
                mPERatio.setText(String.format("%,.2f", value.pe.doubleValue()));
            }
        }
        //double peRatio = YUtils.parseQuoteValue(yQuotes.get("P/E Ratio"));
    }

    public void displayEps()
    {
        if (mEps != null)
        {
            if (value == null || value.eps == null)
            {
                mEps.setText(R.string.na);
            }
            else
            {
                mEps.setText(String.format("%,.3f", value.eps.doubleValue()));
            }
        }
    }

    public void displayVolume()
    {
        if (mVolume != null)
        {
            if (value == null || value.volume == null)
            {
                mVolume.setText(R.string.na);
            }
            else
            {
                mVolume.setText(String.format("%,.0f", value.volume.doubleValue()));
            }
        }
        //double volume = YUtils.parseQuoteValue(yQuotes.get("Volume"));
    }

    public void displayAvgVolume()
    {
        if (mAvgVolume != null)
        {
            if (value == null || value.averageDailyVolume == null)
            {
                mAvgVolume.setText(R.string.na);
            }
            else
            {
                mAvgVolume.setText(String.format("%,.0f", value.averageDailyVolume.doubleValue()));
            }
        }
        //double avgVolume = YUtils.parseQuoteValue(yQuotes.get("Average Daily Volume"));
    }
    //</editor-fold>
}
