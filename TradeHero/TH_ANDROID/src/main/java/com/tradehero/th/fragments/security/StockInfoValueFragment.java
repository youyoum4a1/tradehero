package com.tradehero.th.fragments.security;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

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

    @Inject protected SecurityCompactCacheRx securityCompactCache;
    @Nullable Subscription securityCompactCacheSubscription;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        HierarchyInjector.inject(this);
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

    @Override public void onDestroyView()
    {
        unsubscribe(securityCompactCacheSubscription);
        securityCompactCacheSubscription = null;
        super.onDestroyView();
    }

    @Override protected SecurityCompactCacheRx getInfoCache()
    {
        return securityCompactCache;
    }

    @Override public void linkWith(SecurityId securityId, final boolean andDisplay)
    {
        super.linkWith(securityId, andDisplay);
        if (this.securityId != null)
        {
            unsubscribe(securityCompactCacheSubscription);
            securityCompactCacheSubscription = securityCompactCache.get(securityId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Pair<SecurityId, SecurityCompactDTO>>()
                    {
                        @Override public void onCompleted()
                        {
                        }

                        @Override public void onError(Throwable e)
                        {
                            THToast.show(R.string.error_fetch_security_info);
                        }

                        @Override public void onNext(Pair<SecurityId, SecurityCompactDTO> pair)
                        {
                            linkWith(pair.second, andDisplay);
                        }
                    });
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
                mPreviousClose.setText(THSignedMoney
                        .builder(value.previousClose)
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
                mOpen.setText(THSignedMoney.builder(value.open)
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
                mDaysHigh.setText(THSignedMoney.builder(value.high)
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
                mDaysLow.setText(THSignedMoney.builder(value.low)
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
                mMarketCap.setText(THSignedMoney.builder(value.marketCap)
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
                mPERatio.setText(THSignedNumber.builder(value.pe)
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
                mEps.setText(THSignedNumber.builder(value.eps)
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
                mVolume.setText(THSignedNumber.builder(value.volume)
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
                mAvgVolume.setText(THSignedNumber.builder(value.averageDailyVolume)
                        .build().toString());
            }
        }
    }
    //</editor-fold>
}
