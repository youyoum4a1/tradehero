package com.tradehero.th.fragments.trade;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.th.R;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionDTOList;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.models.parcelable.LiveBuySellParcelable;
import com.tradehero.th.models.parcelable.LiveTransactionParcelable;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import dagger.Lazy;
import java.text.DecimalFormat;
import javax.inject.Inject;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

public class LiveBuySellFragment extends DashboardFragment
{
    @Bind(R.id.stock_logo) ImageView stockLogoImageView;
    @Bind(R.id.live_price) TextView livePriceTextView;
    @Bind(R.id.stock_roi) TextView stockRoiTextView;
    @Bind(R.id.last_update) TextView lastUpdateTextView;
    @Bind(R.id.price_high) TextView highTextView;
    @Bind(R.id.spread_per_unit) TextView spreadPerUnitTextView;
    @Bind(R.id.premium_buy) TextView premiumBuyTextView;
    @Bind(R.id.initial_margin) TextView initialMarginTextView;
    @Bind(R.id.leverage) TextView leverageTextView;
    @Bind(R.id.price_low) TextView lowTextView;
    @Bind(R.id.spread_percent) TextView spreadPercentTextView;
    @Bind(R.id.premium_sell) TextView premiumSellTextView;
    @Bind(R.id.maintenance_margin) TextView maintenanceMarginTextView;
    @Bind(R.id.expires_daily) TextView expiresDailyTextView;
    @Bind(R.id.btn_buy) Button buyBtn;
    @Bind(R.id.btn_sell) Button sellBtn;

    @Inject Lazy<SecurityCompactCacheRx> securityCompactCacheRx;
    @Inject Lazy<SecurityServiceWrapper> securityServiceWrapper;

    private SecurityId securityId;
    private PositionDTO closeablePostionDTO;

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_live_buysell, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        Bundle data = getActivity().getIntent().getExtras();
        LiveBuySellParcelable parcelable = data.getParcelable("LiveBuySellParcelable");

        if (parcelable != null)
        {
            if (parcelable.getShares() > 0)
            {
                LiveTransactionParcelable liveTransactionParcelable = new LiveTransactionParcelable(parcelable.getSecurityId(), parcelable.getShares(), false);
                getActivity().getIntent().putExtra("LiveTransactionParcelable", liveTransactionParcelable);

                // replace the old parcelable for the new fragment come back to this fragment
                LiveBuySellParcelable newParcelable = new LiveBuySellParcelable(parcelable.getSecurityId(), 0);
                getActivity().getIntent().putExtra("LiveBuySellParcelable", newParcelable);

                navigator.get().pushFragment(LiveTransactionFragment.class);
            }

            fetchSecurityData(parcelable.getSecurityId());
            securityId = parcelable.getSecurityId();

            securityServiceWrapper.get().getSecurityPositions(securityId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<PositionDTOList>()
                    {
                        @Override public void call(PositionDTOList positionDTOs)
                        {
                            if (positionDTOs.size() != 0)
                            {
                                closeablePostionDTO = positionDTOs.get(0);

                                if (closeablePostionDTO.shares != null && closeablePostionDTO.shares != 0)
                                {
                                    sellBtn.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }, new Action1<Throwable>()
                    {
                        @Override public void call(Throwable throwable)
                        {
                            Timber.e(throwable.toString());
                        }
                    });
        }
    }

    @Override public void onStart()
    {
        super.onStart();
    }

    @Override public boolean shouldHandleLiveColor()
    {
        return true;
    }

    private void fetchSecurityData(@NonNull SecurityId securityId)
    {
        securityCompactCacheRx.get().get(securityId)
                .observeOn(AndroidSchedulers.mainThread())
                .map(new PairGetSecond<SecurityId, SecurityCompactDTO>())
                .subscribe(new Action1<SecurityCompactDTO>()
                {
                    @Override public void call(SecurityCompactDTO securityCompactDTO)
                    {
                        setActionBarTitle(securityCompactDTO.name);

                        if (securityCompactDTO.imageBlobUrl != null)
                        {
                            Picasso.with(getContext())
                                    .load(Uri.parse(securityCompactDTO.imageBlobUrl))
                                    .into(stockLogoImageView);
                        }
                        else
                        {
                            Picasso.with(getContext())
                                    .load(securityCompactDTO.getExchangeLogoId())
                                    .into(stockLogoImageView);
                        }

                        if (securityCompactDTO.lastPrice != null)
                        {
                            livePriceTextView.setText(THSignedNumber.builder(securityCompactDTO.lastPrice).build().toString());
                        }

                        if (securityCompactDTO.lastPriceDateAndTimeUtc != null)
                        {
                            lastUpdateTextView.setText(securityCompactDTO.lastPriceDateAndTimeUtc.toString());
                        }

                        highTextView.setText(THSignedNumber.builder(securityCompactDTO.high).build().toString());
                        lowTextView.setText(THSignedNumber.builder(securityCompactDTO.low).build().toString());

                        double roi = securityCompactDTO.risePercent * 100;
                        int roiStringRes = R.string.positive_roi;

                        if (roi < 0)
                        {
                            // TODO: handle different color for china/ hongkong/ taiwan
                            stockRoiTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                            roiStringRes = R.string.negative_roi;
                        }
                        else if (roi == 0)
                        {
                            stockRoiTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.darker_grey));
                            roiStringRes = R.string.zero_roi;
                        }

                        stockRoiTextView.setText(getString(roiStringRes, String.format("%.2f", Math.abs(roi))));
                        buyBtn.setText(getString(R.string.live_buy_btn, String.format("%.2f", securityCompactDTO.askPrice)));
                        sellBtn.setText(getString(R.string.live_sell_btn, String.format("%.2f", securityCompactDTO.bidPrice)));

                        // TODO: dummy text while pending server
                        spreadPerUnitTextView.setText("0.40");
                        spreadPercentTextView.setText("0.28%");
                        premiumBuyTextView.setText("-0.03%");
                        premiumSellTextView.setText("0.01%");
                        initialMarginTextView.setText("13.10");
                        maintenanceMarginTextView.setText("13.10");
                        leverageTextView.setText("1:20");
                        expiresDailyTextView.setText("No");
                    }
                }, new Action1<Throwable>()
                {
                    @Override public void call(Throwable throwable)
                    {
                        Timber.e(throwable.toString());
                    }
                });
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.btn_buy) public void buyBtnOnClicked()
    {
        LiveTransactionParcelable liveTransactionParcelable = new LiveTransactionParcelable(securityId, 0, true);
        getActivity().getIntent().putExtra("LiveTransactionParcelable", liveTransactionParcelable);

        navigator.get().pushFragment(LiveTransactionFragment.class);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.btn_sell) public void sellBtnOnClicked()
    {
        Integer shares = 0;

        if (closeablePostionDTO.shares != null)
        {
            shares = closeablePostionDTO.shares;
        }

        LiveTransactionParcelable liveTransactionParcelable = new LiveTransactionParcelable(securityId, shares, false);
        getActivity().getIntent().putExtra("LiveTransactionParcelable", liveTransactionParcelable);

        navigator.get().pushFragment(LiveTransactionFragment.class);
    }
}
