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
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.models.parcelable.LiveBuySellParcelable;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import dagger.Lazy;
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

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarTitle("Facebook Inc");
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
            fetchSecurityData(parcelable.getSecurityId());
        }
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

                        Picasso.with(getContext())
                                .load(Uri.parse(securityCompactDTO.imageBlobUrl))
                                .into(stockLogoImageView);

                        livePriceTextView.setText(THSignedNumber.builder(securityCompactDTO.lastPrice).build().toString());
                        highTextView.setText(THSignedNumber.builder(securityCompactDTO.high).build().toString());
                        lowTextView.setText(THSignedNumber.builder(securityCompactDTO.low).build().toString());
                        lastUpdateTextView.setText(securityCompactDTO.lastPriceDateAndTimeUtc.toString());

                        double roi = securityCompactDTO.risePercent * 100;
                        stockRoiTextView.setText(String.format("%.2f%", roi));

                        if (roi < 0)
                        {
                            stockRoiTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                        }
                        else if (roi == 0)
                        {
                            stockRoiTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.darker_grey));
                        }
                    }
                });
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.btn_buy) public void buyBtnOnClicked()
    {
        Bundle args = new Bundle();
        navigator.get().pushFragment(LiveTransactionFragment.class, args);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.btn_sell) public void sellBtnOnClicked()
    {
        Bundle args = new Bundle();
        navigator.get().pushFragment(LiveTransactionFragment.class, args);
    }
}
