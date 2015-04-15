package com.tradehero.th.fragments.trade;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.android.internal.util.Predicate;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.route.InjectRoute;
import com.tradehero.route.Routable;
import com.tradehero.th.R;
import com.tradehero.th.api.fx.FXChartDTO;
import com.tradehero.th.api.fx.FXChartGranularity;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.position.PositionDTOCompact;
import com.tradehero.th.api.position.PositionDTOCompactList;
import com.tradehero.th.api.position.PositionStatus;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.portfolio.header.MarginCloseOutStatusTextView;
import com.tradehero.th.models.chart.ChartTimeSpan;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.network.service.QuoteServiceWrapper;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import com.tradehero.th.persistence.position.PositionCompactListCacheRx;
import com.tradehero.th.rx.ToastAndLogOnErrorAction;
import com.tradehero.th.rx.ToastOnErrorAction;
import com.tradehero.th.utils.SecurityUtils;
import com.tradehero.th.widget.KChartsView;
import com.tradehero.th.widget.news.TimeSpanButtonSet;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import rx.Observable;
import rx.Observer;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

@Routable("securityFx/:securityRawInfo")
public class FXInfoFragment extends DashboardFragment
        implements TimeSpanButtonSet.OnTimeSpanButtonSelectedListener
{
    private final static long MILLISECOND_FX_QUOTE_REFRESH = 5000;
    private final static long MILLISECOND_FX_CANDLE_CHART_REFRESH = 60000;
    private final static String BUNDLE_KEY_SECURITY_ID_BUNDLE = "securityId";

    @Inject SecurityServiceWrapper securityServiceWrapper;
    @Inject protected PositionCompactListCacheRx positionCompactListCache;
    @Inject protected QuoteServiceWrapper quoteServiceWrapper;

    @InjectView(R.id.margin_close_out_status) protected MarginCloseOutStatusTextView marginCloseOutStatus;
    @InjectView(R.id.chart_image_wrapper) protected BetterViewAnimator mChartWrapper;
    @InjectView(R.id.my_charts_view) protected KChartsView mKChartsView;
    @InjectView(R.id.chart_time_span_button_set) protected TimeSpanButtonSet mTimeSpanButtonSet;
    @InjectView(R.id.ll_position_status) protected LinearLayout llPositionStatus;
    @InjectView(R.id.tv_position_units) protected TextView tvPositionUnits;
    @InjectView(R.id.tv_position_money) protected TextView tvPositionMoney;

    @InjectRoute protected SecurityId securityId;
    @Nullable protected PositionDTOCompactList positionDTOCompactList;
    @Nullable protected PositionDTOCompact positionDTOCompact;
    @Nullable static protected PortfolioCompactDTO portfolioCompactDTO;
    @Nullable protected QuoteDTO quoteDTO;
    @Nullable static protected OwnedPortfolioId purchaseApplicableOwnedPortfolioId;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_fx_info, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        setRetainInstance(false);
        initTimeSpanButton();
        securityId = getSecurityId(getArguments());
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchKChart(FXChartGranularity.min1);
        fetchPositionCompactList();
        fetchQuote();
    }

    @Nullable public static SecurityId getSecurityId(@NonNull Bundle args)
    {
        Bundle securityIdBundle = args.getBundle(BUNDLE_KEY_SECURITY_ID_BUNDLE);
        if (securityIdBundle == null)
        {
            return null;
        }
        return new SecurityId(securityIdBundle);
    }

    protected void fetchPositionCompactList()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                positionCompactListCache.get(securityId))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Pair<SecurityId, PositionDTOCompactList>>()
                        {
                            @Override public void call(Pair<SecurityId, PositionDTOCompactList> pair)
                            {
                                linkWith(pair.second);
                            }
                        },
                        new ToastAndLogOnErrorAction(
                                getString(R.string.error_fetch_position_list_info),
                                "Failed to fetch positions for this security")));
    }

    public void linkWith(final PositionDTOCompactList positionDTOCompacts)
    {
        this.positionDTOCompactList = positionDTOCompacts;
        if (positionDTOCompactList != null && portfolioCompactDTO != null)
        {
            this.positionDTOCompact = positionDTOCompactList.findFirstWhere(new Predicate<PositionDTOCompact>()
            {
                @Override public boolean apply(PositionDTOCompact position)
                {
                    return position.portfolioId == portfolioCompactDTO.id && position.shares != 0;
                }
            });
            if (positionDTOCompact == null)
            {
                this.positionDTOCompact = positionDTOCompactList.findFirstWhere(new Predicate<PositionDTOCompact>()
                {
                    @Override public boolean apply(PositionDTOCompact position)
                    {
                        return position.portfolioId == portfolioCompactDTO.id;
                    }
                });
            }
        }
        displayPositionStatus();
    }

    protected void fetchQuote()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                quoteServiceWrapper.getQuoteRx(securityId)
                        .repeatWhen(new Func1<Observable<? extends Void>, Observable<?>>()
                        {
                            @Override public Observable<?> call(Observable<? extends Void> observable)
                            {
                                return observable.delay(MILLISECOND_FX_QUOTE_REFRESH, TimeUnit.MILLISECONDS);
                            }
                        }))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<QuoteDTO>()
                        {
                            @Override public void call(QuoteDTO quote)
                            {
                                quoteDTO = quote;
                                if (portfolioCompactDTO != null)
                                {
                                    marginCloseOutStatus.linkWith(portfolioCompactDTO);
                                }
                                displayPositionStatus();
                            }
                        },
                        new ToastOnErrorAction()));
    }

    private void initTimeSpanButton()
    {
        mTimeSpanButtonSet.addAllChildButtons();
        mTimeSpanButtonSet.setListener(this);
        mTimeSpanButtonSet.setActive(new ChartTimeSpan(ChartTimeSpan.MIN_1));
    }

    @Override public void onDestroyView()
    {
        super.onDestroyView();
    }

    private void fetchKChart(@NonNull FXChartGranularity granularity)
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                securityServiceWrapper.getFXHistory(securityId, granularity)
                        .repeatWhen(new Func1<Observable<? extends Void>, Observable<?>>()
                        {
                            @Override public Observable<?> call(Observable<? extends Void> observable)
                            {
                                return observable.delay(MILLISECOND_FX_CANDLE_CHART_REFRESH, TimeUnit.MILLISECONDS);
                            }
                        }))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createFXHistoryFetchObserver()));
    }

    //<editor-fold desc="Display Methods"> //hide switch portfolios for temp
    public void displayPositionStatus()
    {
        if (positionDTOCompact == null)
        {
            // Not enough info
            return;
        }

        if (llPositionStatus != null)
        {
            boolean toShow = positionDTOCompact.shares != null
                    && positionDTOCompact.positionStatus != null;
            llPositionStatus.setVisibility(toShow ? View.VISIBLE : View.GONE);
            if (toShow)
            {
                THSignedNumber.Builder builder = THSignedNumber.builder(positionDTOCompact.shares)
                        .withOutSign();
                if (positionDTOCompact.shares == 0)
                {
                    tvPositionUnits.setText(getString(R.string.no_current_position_units));
                }
                else
                {
                    if (positionDTOCompact.positionStatus.equals(PositionStatus.LONG))
                    {
                        builder.format(getString(R.string.long_position_units));
                    }
                    else if (positionDTOCompact.positionStatus.equals(PositionStatus.SHORT))
                    {
                        builder.format(getString(R.string.short_position_units));
                    }
                    builder.build().into(tvPositionUnits);
                }

                Double unRealizedPLRefCcy = getUnRealizedPLRefCcy();

                if (unRealizedPLRefCcy != null)
                {
                    THSignedMoney.builder(unRealizedPLRefCcy)
                            .currency(portfolioCompactDTO == null
                                    ? SecurityUtils.getDefaultCurrency()
                                    : portfolioCompactDTO.getNiceCurrency())
                            .withSign()
                            .withDefaultColor()
                            .signTypeArrow()
                            .build()
                            .into(tvPositionMoney);
                }
                else
                {
                    tvPositionMoney.setText(R.string.na);
                }
            }
        }
    }

    public Double getUnRealizedPLRefCcy()
    {
        OwnedPortfolioId ownedPortfolioId = purchaseApplicableOwnedPortfolioId;
        if (ownedPortfolioId != null && positionDTOCompactList != null
                && this.quoteDTO != null && portfolioCompactDTO != null)
        {
            return positionDTOCompactList.getUnRealizedPLRefCcy(
                    this.quoteDTO,
                    this.portfolioCompactDTO
            );
        }
        return null;
    }

    @Override
    public void onTimeSpanButtonSelected(ChartTimeSpan selected)
    {
        fetchKChart(FXChartGranularity.getBestApproximation(selected));
        mChartWrapper.setDisplayedChild(0);
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
            THToast.show(R.string.error_fx_candle_charts_load_fail);
        }
    }

    public static void setPurchaseApplicableOwnedPortfolioId(@Nullable OwnedPortfolioId spurchaseApplicableOwnedPortfolioId)
    {
        purchaseApplicableOwnedPortfolioId = spurchaseApplicableOwnedPortfolioId;
    }

    public static void setPortfolioCompactDTO(@Nullable PortfolioCompactDTO sportfolioCompactDTO)
    {
        portfolioCompactDTO = sportfolioCompactDTO;
    }
}
