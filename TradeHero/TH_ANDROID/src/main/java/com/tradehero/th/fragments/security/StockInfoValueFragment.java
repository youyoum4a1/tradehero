package com.tradehero.th.fragments.security;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.models.number.THSignedNumber;
import javax.inject.Inject;

public class StockInfoValueFragment extends AbstractSecurityInfoFragment<SecurityCompactDTO>
{
    private TextView mPreviousClose;
    private TextView mOpen;
    private TextView mDaysHigh;
    private TextView mDaysLow;
    private TextView mMarketCap;
    private TextView mPERatio;
    private TextView mEps;
    private TextView mVolume;
    private TextView mAvgVolume;

    @Inject protected SecurityCompactCache securityCompactCache;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DaggerUtils.inject(this);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view;
        view = inflater.inflate(R.layout.fragment_stockinfo_value, container, false);
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

    @Override protected SecurityCompactCache getInfoCache()
    {
        return securityCompactCache;
    }

    @Override public void linkWith(SecurityId securityId, boolean andDisplay)
    {
        super.linkWith(securityId, andDisplay);
        if (this.securityId != null)
        {
            linkWith(securityCompactCache.get(this.securityId), andDisplay);
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
        if (!isDetached() && mPreviousClose != null)
        {
            if (value == null || value.previousClose == null)
            {
                mPreviousClose.setText(R.string.na);
            }
            else
            {
                mPreviousClose.setText(THSignedMoney.builder()
                        .number(value.previousClose)
                        .currency(value.currencyDisplay)
                        .build().toString());
            }
        }
    }

    public void displayOpen()
    {
        if (!isDetached() && mOpen != null)
        {
            if (value == null || value.open == null)
            {
                mOpen.setText(R.string.na);
            }
            else
            {
                mOpen.setText(THSignedMoney.builder()
                        .number(value.open)
                        .currency(value.currencyDisplay)
                        .build().toString());
            }
        }
    }

    public void displayDaysHigh()
    {
        if (!isDetached() && mDaysHigh != null)
        {
            if (value == null || value.high == null)
            {
                mDaysHigh.setText(R.string.na);
            }
            else
            {
                mDaysHigh.setText(THSignedMoney.builder()
                        .number(value.high)
                        .currency(value.currencyDisplay)
                        .build().toString());
            }
        }
    }

    public void displayDaysLow()
    {
        if (!isDetached() && mDaysLow != null)
        {
            if (value == null || value.low == null)
            {
                mDaysLow.setText(R.string.na);
            }
            else
            {
                mDaysLow.setText(THSignedMoney.builder()
                        .number(value.low)
                        .currency(value.currencyDisplay)
                        .build().toString());
            }
        }
    }

    public void displayMarketCap()
    {
        if (!isDetached() && mMarketCap != null)
        {
            if (value == null || value.marketCap == null)
            {
                mMarketCap.setText(R.string.na);
            }
            else
            {
                mMarketCap.setText(THSignedMoney.builder()
                        .number(value.marketCap)
                        .currency(value.currencyDisplay)
                        .build().toString());
            }
        }
    }

    public void displayPERatio()
    {
        if (!isDetached() && mPERatio != null)
        {
            if (value == null || value.pe == null)
            {
                mPERatio.setText(R.string.na);
            }
            else
            {
                mPERatio.setText(THSignedNumber.builder()
                        .number(value.pe)
                        .withOutSign()
                        .build().toString());
            }
        }
    }

    public void displayEps()
    {
        if (!isDetached() && mEps != null)
        {
            if (value == null || value.eps == null)
            {
                mEps.setText(R.string.na);
            }
            else
            {
                mEps.setText(THSignedNumber.builder()
                        .number(value.eps)
                        .withOutSign()
                        .build().toString());
            }
        }
    }

    public void displayVolume()
    {
        if (!isDetached() && mVolume != null)
        {
            if (value == null || value.volume == null)
            {
                mVolume.setText(R.string.na);
            }
            else
            {
                mVolume.setText(THSignedNumber.builder()
                        .number(value.volume)
                        .build().toString());
            }
        }
    }

    public void displayAvgVolume()
    {
        if (!isDetached() && mAvgVolume != null)
        {
            if (value == null || value.averageDailyVolume == null)
            {
                mAvgVolume.setText(R.string.na);
            }
            else
            {
                mAvgVolume.setText(THSignedNumber.builder()
                        .number(value.averageDailyVolume)
                        .build().toString());
            }
        }
    }
    //</editor-fold>
}
