package com.tradehero.th.fragments.security;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import com.tradehero.th.rx.ToastAction;
import javax.inject.Inject;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class StockInfoValueFragment extends AbstractSecurityInfoFragment
{
    @InjectView(R.id.vprevious_close) TextView mPreviousClose;
    @InjectView(R.id.vopen) TextView mOpen;
    @InjectView(R.id.vdays_high) TextView mDaysHigh;
    @InjectView(R.id.vdays_low) TextView mDaysLow;
    @InjectView(R.id.vmarket_cap) TextView mMarketCap;
    @InjectView(R.id.vpe_ratio) TextView mPERatio;
    @InjectView(R.id.veps) TextView mEps;
    @InjectView(R.id.vvolume) TextView mVolume;
    @InjectView(R.id.vavg_volume) TextView mAvgVolume;

    @Inject protected SecurityCompactCacheRx securityCompactCache;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        HierarchyInjector.inject(this);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_stockinfo_value, container, false);
    }

    @Override public void onViewCreated(View v, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(v, savedInstanceState);
        ButterKnife.inject(this, v);
        fetchSecurity();
    }

    protected void fetchSecurity()
    {
        if (this.securityId != null)
        {
            onDestroyViewSubscriptions.add(securityCompactCache.get(securityId)
                    .map(new PairGetSecond<SecurityId, SecurityCompactDTO>())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            new Action1<SecurityCompactDTO>()
                            {
                                @Override public void call(SecurityCompactDTO securityCompactDTO)
                                {
                                    linkWith(securityCompactDTO);
                                }
                            },
                            new ToastAction<Throwable>(getString(R.string.error_fetch_security_info))));
        }
    }

    public void linkWith(SecurityCompactDTO value)
    {
        this.securityCompactDTO = value;

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
