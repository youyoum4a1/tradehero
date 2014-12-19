package com.tradehero.th.fragments.trade;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.InjectView;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.route.Routable;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderDTOList;
import com.tradehero.th.api.fx.FXChartDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.compact.FxSecurityCompactDTO;
import com.tradehero.th.api.security.key.FxPairSecurityId;
import com.tradehero.th.fragments.portfolio.header.MarginCloseOutStatusTextView;
import com.tradehero.th.models.chart.ChartTimeSpan;
import com.tradehero.th.models.chart.yahoo.YahooTimeSpan;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.portfolio.MenuOwnedPortfolioId;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import com.tradehero.th.widget.KChartsView;
import com.tradehero.th.widget.news.TimeSpanButtonSet;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;

@Routable("securityFx/:securityRawInfo")
public class BuySellFXFragment extends BuySellFragment
        implements TimeSpanButtonSet.OnTimeSpanButtonSelectedListener
{
    @Inject SecurityServiceWrapper securityServiceWrapper;

    @InjectView(R.id.margin_close_out_status) protected MarginCloseOutStatusTextView marginCloseOutStatus;
    @InjectView(R.id.chart_image_wrapper) protected BetterViewAnimator mChartWrapper;
    @InjectView(R.id.my_charts_view) protected KChartsView mKChartsView;
    @InjectView(R.id.chart_time_span_button_set) protected TimeSpanButtonSet mTimeSpanButtonSet;

    @Nullable protected Subscription fxHistorySubscription;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_fx_buy_sell, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        fetchKChart(YahooTimeSpan.min1.code);
        initTimeSpanButton();
    }

    private void initTimeSpanButton()
    {
        mTimeSpanButtonSet.addAllChildButtons();
        mTimeSpanButtonSet.setListener(this);
        mTimeSpanButtonSet.setActive(new ChartTimeSpan(ChartTimeSpan.MIN_1));
    }

    @Override public void onStop()
    {
        unsubscribe(fxHistorySubscription);
        fxHistorySubscription = null;
        super.onStop();
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        unsubscribe(fxHistorySubscription);
        fxHistorySubscription = null;
    }

    @Override protected void linkWith(PortfolioCompactDTO portfolioCompactDTO, boolean andDisplay)
    {
        super.linkWith(portfolioCompactDTO, andDisplay);
        marginCloseOutStatus.linkWith(portfolioCompactDTO);
    }

    private void fetchKChart(String code)
    {
        fxHistorySubscription = AndroidObservable.bindFragment(
                this,
                securityServiceWrapper.getFXHistory(securityId, code))
                .subscribe(createFXHistoryFetchObserver());
    }

    //<editor-fold desc="Display Methods"> //hide switch portfolios for temp

    @Override public void displayStockName()
    {
        super.displayStockName();
        if (securityCompactDTO instanceof FxSecurityCompactDTO)
        {
            FxPairSecurityId fxPairSecurityId = ((FxSecurityCompactDTO) securityCompactDTO).getFxPair();
            setActionBarTitle(String.format("%s/%s", fxPairSecurityId.left, fxPairSecurityId.right));
            setActionBarSubtitle(null);
        }
    }

    @Override public void displayBuySellPrice()
    {
        if (mBuyBtn != null && mSellBtn != null)
        {
            String bPrice;
            String sPrice;
            THSignedNumber bthSignedNumber;
            THSignedNumber sthSignedNumber;
            if (quoteDTO == null)
            {
                return;
            }
            else
            {
                if (quoteDTO.ask == null)
                {
                    bPrice = getString(R.string.buy_sell_ask_price_not_available);
                }
                else
                {
                    bthSignedNumber = THSignedNumber.builder(quoteDTO.ask)
                            .signTypeArrow()
                            .relevantDigitCount(10)
                            .build();
                    bPrice = bthSignedNumber.toString();
                }

                if (quoteDTO.bid == null)
                {
                    sPrice = getString(R.string.buy_sell_bid_price_not_available);
                }
                else
                {
                    sthSignedNumber = THSignedNumber.builder(quoteDTO.bid)
                            .signTypeArrow()
                            .relevantDigitCount(10)
                            .build();
                    sPrice = sthSignedNumber.toString();
                }
            }
            mBuyBtn.setText(getString(R.string.fx_buy, bPrice));
            mSellBtn.setText(getString(R.string.fx_sell, sPrice));
        }
    }

    @Override public boolean isBuySellReady()
    {
        return quoteDTO != null && securityPositionDetailDTO != null;
    }

    @Override
    public void onTimeSpanButtonSelected(ChartTimeSpan selected) {
        fetchKChart(checkTime(selected.duration));
        mChartWrapper.setDisplayedChild(0);
    }

    private String checkTime(long duration) {
        switch ((int)duration)
        {
            case (int)ChartTimeSpan.MIN_1:
                return YahooTimeSpan.min1.code;
            case (int)ChartTimeSpan.MIN_5:
                return YahooTimeSpan.min5.code;
            case (int)ChartTimeSpan.MIN_15:
                return YahooTimeSpan.min15.code;
            case (int)ChartTimeSpan.MIN_30:
                return YahooTimeSpan.min30.code;
            case (int)ChartTimeSpan.HOUR_1:
                return YahooTimeSpan.hour1.code;
            case (int)ChartTimeSpan.HOUR_4:
                return YahooTimeSpan.hour4.code;
            case (int)ChartTimeSpan.DAY_1:
                return "D";
        }
        return YahooTimeSpan.min1.code;
    }

    @Override protected boolean getSupportSell()
    {
        return true;
    }
    //</editor-fold>

    @NonNull protected Observer<FXChartDTO> createFXHistoryFetchObserver()
    {
        return new TrendingFXHistoryFetchObserver();
    }

    protected class TrendingFXHistoryFetchObserver implements Observer<FXChartDTO>
    {
        @Override public void onNext(FXChartDTO fxChartDTO)
        {
            mKChartsView.setOHLCData(fxChartDTO.candles);
            mChartWrapper.setDisplayedChild(1);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(R.string.error_fetch_provider_competition_list);
        }
    }

    @Override public void linkWith(@NonNull final SecurityPositionDetailDTO securityPositionDetailDTO)
    {
        this.securityPositionDetailDTO = securityPositionDetailDTO;
        linkWith(securityPositionDetailDTO.security, true);
        linkWith(securityPositionDetailDTO.positions, true);

        ProviderDTOList providerDTOs = securityPositionDetailDTO.providers;
        if (providerDTOs != null)
        {
            for (ProviderDTO providerDTO : providerDTOs)
            {
                if (providerDTO.associatedPortfolio != null)
                {
                    mSelectedPortfolioContainer.addMenuOwnedPortfolioIdforFX(
                            new MenuOwnedPortfolioId(
                                    currentUserId.toUserBaseKey(),
                                    providerDTO.associatedPortfolio));
                }
            }
        }

        setInitialSellQuantityIfCan();

        displayBuySellSwitch();
        displayBuySellPrice();
        displayBuySellContainer();
    }

    @Override protected void softFetchPortfolioCompactList()
    {
        // Force a proper fetch
//        fetchPortfolioCompactList();
    }
}
