/**
 * StockInfoFragment.java 
 * TradeHero
 *
 * Created by @author Siddesh Bingi on Jul 26, 2013
 */
package com.tradehero.th.fragments.trade;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.models.Trend;
import com.tradehero.th.utills.Logger;
import com.tradehero.th.utills.Logger.LogLevel;
import com.tradehero.th.utills.YUtils;
import java.util.HashMap;

public class StockInfoFragment extends SherlockFragment implements DTOView<SecurityCompactDTO>
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
    private SecurityCompactDTO securityCompactDTO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        THLog.d(TAG, "onActivityCreated()");

        //((TrendingDetailFragment) getActivity().getSupportFragmentManager()
        //        .findFragmentByTag("trending_detail")).setYahooQuoteUpdateListener(this);

        display();
    }

    @Override public void onResume()
    {
        super.onResume();
        Bundle args = getArguments();
        if (args != null)
        {
            new SecurityId(args);
            // TODO make use of it to query the cache
        }
        display();
    }

    @Override public void display(SecurityCompactDTO dto)
    {
        linkWith(dto, true);
    }

    public void linkWith(SecurityCompactDTO securityCompactDTO, boolean andDisplay)
    {
        this.securityCompactDTO = securityCompactDTO;
        if (andDisplay)
        {
            display();
        }
    }

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
            if (securityCompactDTO == null || securityCompactDTO.previousClose == null)
            {
                mPreviousClose.setText(R.string.na);
            }
            else
            {
                mPreviousClose.setText(String.format("%,.3f", securityCompactDTO.previousClose.doubleValue()));
            }
        }
    }

    public void displayOpen()
    {
        if (mOpen != null)
        {
            if (securityCompactDTO == null || securityCompactDTO.open == null)
            {
                mOpen.setText(R.string.na);
            }
            else
            {
                mOpen.setText(String.format("%,.2f", securityCompactDTO.open.doubleValue()));
            }
        }
        //double open = YUtils.parseQuoteValue(yQuotes.get("Open"));
    }

    public void displayDaysHigh()
    {
        if (mDaysHigh != null)
        {
            if (securityCompactDTO == null || securityCompactDTO.high == null)
            {
                mDaysHigh.setText(R.string.na);
            }
            else
            {
                mDaysHigh.setText(String.format("%,.2f", securityCompactDTO.high.doubleValue()));
            }
        }
        //double daysHigh = YUtils.parseQuoteValue(yQuotes.get("Day's High"));
    }

    public void displayDaysLow()
    {
        if (mDaysLow != null)
        {
            if (securityCompactDTO == null || securityCompactDTO.low == null)
            {
                mDaysLow.setText(R.string.na);
            }
            else
            {
                mDaysLow.setText(String.format("%,.2f", securityCompactDTO.low.doubleValue()));
            }
        }
        //double daysLow = YUtils.parseQuoteValue(yQuotes.get("Day's Low"));
    }

    public void displayMarketCap()
    {
        if (mMarketCap != null)
        {
            if (securityCompactDTO == null || securityCompactDTO.marketCap == null)
            {
                mMarketCap.setText(R.string.na);
            }
            else
            {
                mMarketCap.setText(String.format("%,.2f", securityCompactDTO.marketCap.doubleValue()));
            }
        }
    }

    public void displayPERatio()
    {
        if (mPERatio != null)
        {
            if (securityCompactDTO == null || securityCompactDTO.pe == null)
            {
                mPERatio.setText(R.string.na);
            }
            else
            {
                mPERatio.setText(String.format("%,.2f", securityCompactDTO.pe.doubleValue()));
            }
        }
        //double peRatio = YUtils.parseQuoteValue(yQuotes.get("P/E Ratio"));
    }

    public void displayEps()
    {
        if (mEps != null)
        {
            if (securityCompactDTO == null || securityCompactDTO.eps == null)
            {
                mEps.setText(R.string.na);
            }
            else
            {
                mEps.setText(String.format("%,.3f", securityCompactDTO.eps.doubleValue()));
            }
        }
    }

    public void displayVolume()
    {
        if (mVolume != null)
        {
            if (securityCompactDTO == null || securityCompactDTO.volume == null)
            {
                mVolume.setText(R.string.na);
            }
            else
            {
                mVolume.setText(String.format("%,.0f", securityCompactDTO.volume.doubleValue()));
            }
        }
        //double volume = YUtils.parseQuoteValue(yQuotes.get("Volume"));
    }

    public void displayAvgVolume()
    {
        if (mAvgVolume != null)
        {
            if (securityCompactDTO == null || securityCompactDTO.averageDailyVolume == null)
            {
                mAvgVolume.setText(R.string.na);
            }
            else
            {
                mAvgVolume.setText(String.format("%,.0f", securityCompactDTO.averageDailyVolume.doubleValue()));
            }
        }
        //double avgVolume = YUtils.parseQuoteValue(yQuotes.get("Average Daily Volume"));
    }
}
