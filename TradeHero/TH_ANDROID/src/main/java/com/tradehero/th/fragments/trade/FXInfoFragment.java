package com.tradehero.th.fragments.trade;

import android.app.Activity;
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
import com.tradehero.th.R;
import com.tradehero.th.api.fx.FXChartDTO;
import com.tradehero.th.api.fx.FXChartGranularity;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionDTOCompact;
import com.tradehero.th.api.position.PositionDTOList;
import com.tradehero.th.api.position.PositionDTOUtil;
import com.tradehero.th.api.position.PositionStatus;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.portfolio.header.MarginCloseOutStatusTextView;
import com.tradehero.th.fragments.security.AbstractSecurityInfoFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.chart.ChartTimeSpan;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.network.service.QuoteServiceWrapper;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import com.tradehero.th.persistence.position.PositionListCacheRx;
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
import rx.subjects.BehaviorSubject;

public class FXInfoFragment extends AbstractSecurityInfoFragment
        implements TimeSpanButtonSet.OnTimeSpanButtonSelectedListener
{
    private final static String BUNDLE_KEY_APPLICABLE_PORTFOLIO_ID = FXInfoFragment.class.getName() + ".applicablePortfolioId";
    private final static long MILLISECOND_FX_QUOTE_REFRESH = 5000;
    private final static long MILLISECOND_FX_CANDLE_CHART_REFRESH = 60000;

    @Inject SecurityServiceWrapper securityServiceWrapper;
    @Inject protected PositionListCacheRx positionCompactListCache;
    @Inject protected QuoteServiceWrapper quoteServiceWrapper;

    @InjectView(R.id.margin_close_out_status) protected MarginCloseOutStatusTextView marginCloseOutStatus;
    @InjectView(R.id.chart_image_wrapper) protected BetterViewAnimator mChartWrapper;
    @InjectView(R.id.my_charts_view) protected KChartsView mKChartsView;
    @InjectView(R.id.chart_time_span_button_set) protected TimeSpanButtonSet mTimeSpanButtonSet;
    @InjectView(R.id.ll_position_status) protected LinearLayout llPositionStatus;
    @InjectView(R.id.tv_position_units) protected TextView tvPositionUnits;
    @InjectView(R.id.tv_position_money) protected TextView tvPositionMoney;

    @Nullable protected PositionDTOList positionDTOList;
    @Nullable protected PositionDTOCompact positionDTOCompact;
    @Nullable protected PortfolioCompactDTO portfolioCompactDTO;
    @NonNull protected BehaviorSubject<PortfolioCompactDTO> portfolioCompactDTOBehavior;
    @Nullable protected QuoteDTO quoteDTO;
    protected OwnedPortfolioId purchaseApplicableOwnedPortfolioId;

    public static void putApplicablePortfolioId(@NonNull Bundle args, @NonNull OwnedPortfolioId applicablePortfolioId)
    {
        args.putBundle(BUNDLE_KEY_APPLICABLE_PORTFOLIO_ID, applicablePortfolioId.getArgs());
    }

    private static OwnedPortfolioId getPurchaseApplicableOwnedPortfolioId(@NonNull Bundle args)
    {
        return new OwnedPortfolioId(args.getBundle(BUNDLE_KEY_APPLICABLE_PORTFOLIO_ID));
    }

    public FXInfoFragment()
    {
        portfolioCompactDTOBehavior = BehaviorSubject.create();
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        HierarchyInjector.inject(this);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        purchaseApplicableOwnedPortfolioId = getPurchaseApplicableOwnedPortfolioId(getArguments());
    }

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
        fetchKChart(FXChartGranularity.min1);
        fetchPositionCompactList();
        fetchQuote();
        registerToPortfolio();
    }

    @NonNull public Observer<PortfolioCompactDTO> getPortfolioObserver()
    {
        return portfolioCompactDTOBehavior;
    }

    protected void fetchPositionCompactList()
    {
        onDestroyViewSubscriptions.add(AppObservable.bindFragment(
                this,
                positionCompactListCache.get(securityId))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Pair<SecurityId, PositionDTOList>>()
                        {
                            @Override public void call(Pair<SecurityId, PositionDTOList> pair)
                            {
                                linkWith(pair.second);
                            }
                        },
                        new ToastAndLogOnErrorAction(
                                getString(R.string.error_fetch_position_list_info),
                                "Failed to fetch positions for this security")));
    }

    public void linkWith(final PositionDTOList positionDTOs)
    {
        this.positionDTOList = positionDTOs;
        if (positionDTOList != null && portfolioCompactDTO != null)
        {
            this.positionDTOCompact = positionDTOList.findFirstWhere(new Predicate<PositionDTO>()
            {
                @Override public boolean apply(PositionDTO position)
                {
                    return position.portfolioId == portfolioCompactDTO.id && position.shares != null && position.shares != 0;
                }
            });
            if (positionDTOCompact == null)
            {
                this.positionDTOCompact = positionDTOList.findFirstWhere(new Predicate<PositionDTO>()
                {
                    @Override public boolean apply(PositionDTO position)
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
        onDestroyViewSubscriptions.add(AppObservable.bindFragment(
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

    private void fetchKChart(@NonNull FXChartGranularity granularity)
    {
        onDestroyViewSubscriptions.add(AppObservable.bindFragment(
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

    private void registerToPortfolio()
    {
        onDestroyViewSubscriptions.add(AppObservable.bindFragment(
                this,
                portfolioCompactDTOBehavior)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<PortfolioCompactDTO>()
                        {
                            @Override public void call(PortfolioCompactDTO portfolioCompactDTO)
                            {
                                FXInfoFragment.this.portfolioCompactDTO = portfolioCompactDTO;
                                if (marginCloseOutStatus != null)
                                {
                                    marginCloseOutStatus.linkWith(portfolioCompactDTO);
                                }
                                displayPositionStatus();
                            }
                        },
                        new ToastAndLogOnErrorAction("Failed to listen to portfolio")));
    }

    //<editor-fold desc="Display Methods"> //hide switch portfolios for temp
    public void displayPositionStatus()
    {
        if (llPositionStatus != null && positionDTOCompact != null)
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
        if (ownedPortfolioId != null && positionDTOList != null
                && this.quoteDTO != null && portfolioCompactDTO != null)
        {
            return PositionDTOUtil.getUnRealizedPLRefCcy(
                    positionDTOList,
                    this.quoteDTO,
                    portfolioCompactDTO
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
}
