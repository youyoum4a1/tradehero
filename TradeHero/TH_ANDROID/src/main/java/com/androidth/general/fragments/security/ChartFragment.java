package com.androidth.general.fragments.security;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.internal.util.Predicate;
import com.androidth.general.R;
import com.androidth.general.activities.StockChartActivity;
import com.androidth.general.api.alert.AlertCompactDTO;
import com.androidth.general.api.quote.QuoteDTO;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.api.security.SecurityId;
import com.androidth.general.api.security.TillExchangeOpenDuration;
import com.androidth.general.api.security.compact.WarrantDTO;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.watchlist.WatchlistPositionDTO;
import com.androidth.general.api.watchlist.WatchlistPositionDTOList;
import com.androidth.general.common.annotation.ViewVisibilityValue;
import com.androidth.general.common.rx.PairGetSecond;
import com.androidth.general.common.widget.BetterViewAnimator;
import com.androidth.general.fragments.DashboardNavigator;
import com.androidth.general.fragments.alert.AlertCreateDialogFragment;
import com.androidth.general.fragments.alert.AlertEditDialogFragment;
import com.androidth.general.fragments.alert.BaseAlertEditDialogFragment;
import com.androidth.general.fragments.base.ActionBarOwnerMixin;
import com.androidth.general.fragments.base.FragmentOuterElements;
import com.androidth.general.fragments.trade.AbstractBuySellFragment;
import com.androidth.general.fragments.trade.AlertDialogBuySellRxUtil;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.models.chart.ChartDTO;
import com.androidth.general.models.chart.ChartDTOFactory;
import com.androidth.general.models.chart.ChartSize;
import com.androidth.general.models.chart.ChartTimeSpan;
import com.androidth.general.models.number.THSignedMoney;
import com.androidth.general.models.number.THSignedNumber;
import com.androidth.general.models.number.THSignedPercentage;
import com.androidth.general.network.retrofit.RequestHeaders;
import com.androidth.general.persistence.alert.AlertCompactListCacheRx;
import com.androidth.general.persistence.security.SecurityCompactCacheRx;
import com.androidth.general.persistence.watchlist.UserWatchlistPositionCacheRx;
import com.androidth.general.rx.EmptyAction1;
import com.androidth.general.rx.TimberOnErrorAction1;
import com.androidth.general.rx.ToastOnErrorAction1;
import com.androidth.general.rx.dialog.OnDialogClickEvent;
import com.androidth.general.utils.DateUtils;
import com.androidth.general.widget.news.TimeSpanButtonSet;
import com.etiennelawlor.quickreturn.library.views.NotifyingScrollView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.widgets.AspectRatioImageViewCallback;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.Lazy;
import rx.Observable;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class ChartFragment extends AbstractSecurityInfoFragment
{
    private final static String BUNDLE_KEY_TIME_SPAN_BUTTON_SET_VISIBILITY = ChartFragment.class.getName() + ".timeSpanButtonSetVisibility";
    private final static String BUNDLE_KEY_TIME_SPAN_SECONDS_LONG = ChartFragment.class.getName() + ".timeSpanSecondsLong";
    private final static String BUNDLE_KEY_CHART_SIZE_ARRAY_INT = ChartFragment.class.getName() + ".chartSizeArrayInt";
    private final static int STOCK_ACTIVITY_REQUEST_CODE = 102;

    @ColorRes private static final int COLOR_RES_UNWATCHED = R.color.darker_grey;
    @ColorRes private static final int COLOR_RES_WATCHED = R.color.watchlist_button_color;

    private static final float ALPHA_INACTIVE = 0.5f;
    private static final float ALPHA_ACTIVE = 1f;

    @Bind(R.id.chart_imageView) protected ChartImageView chartImage;
    private TimeSpanButtonSet timeSpanButtonSet;
    private TimeSpanButtonSet.OnTimeSpanButtonSelectedListener timeSpanButtonSetListener;
    private ChartDTO chartDTO;
    @Nullable private WarrantDTO warrantDTO;
    @ViewVisibilityValue private int timeSpanButtonSetVisibility = View.VISIBLE;

    @Bind(R.id.chart_scroll_view) @Nullable NotifyingScrollView scrollView;

    @Bind(R.id.close) @Nullable protected Button mCloseButton;

    @Bind(R.id.chart_image_wrapper) @Nullable protected BetterViewAnimator chartImageWrapper;

    //Stock info
    @Bind(R.id.buy_price) @Nullable TextView buyPrice;
    @Bind(R.id.sell_price) @Nullable TextView sellPrice;
    @Bind(R.id.tv_stock_roi) @Nullable TextView stockRoi;
    @Bind(R.id.market_close_hint) @Nullable protected TextView marketCloseHint;
    @Bind(R.id.btn_watched) @Nullable protected ImageView btnWatched;
    @Bind(R.id.btn_alerted) @Nullable protected View btnAlerted;

    // Warrant specific
    @Bind(R.id.row_warrant_type) @Nullable protected View rowWarrantType;
    @Bind(R.id.vwarrant_type) @Nullable protected TextView mWarrantType;
    @Bind(R.id.row_warrant_code) @Nullable protected View rowWarrantCode;
    @Bind(R.id.vwarrant_code) @Nullable protected TextView mWarrantCode;
    @Bind(R.id.row_warrant_expiry) @Nullable protected View rowWarrantExpiry;
    @Bind(R.id.vwarrant_expiry) @Nullable protected TextView mWarrantExpiry;
    @Bind(R.id.row_warrant_strike_price) @Nullable protected View rowStrikePrice;
    @Bind(R.id.vwarrant_strike_price) @Nullable protected TextView mStrikePrice;
    @Bind(R.id.row_warrant_underlying) @Nullable protected View rowUnderlying;
    @Bind(R.id.vwarrant_underlying) @Nullable protected TextView mUnderlying;
    @Bind(R.id.row_warrant_issuer) @Nullable protected View rowIssuer;
    @Bind(R.id.vwarrant_issuer) @Nullable protected TextView mIssuer;

    @Bind(R.id.vprevious_close) @Nullable protected TextView mPreviousClose;
    @Bind(R.id.vopen) @Nullable protected TextView mOpen;
    @Bind(R.id.vdays_high) @Nullable protected TextView mDaysHigh;
    @Bind(R.id.vdays_low) @Nullable protected TextView mDaysLow;
    @Bind(R.id.vpe_ratio) @Nullable protected TextView mPERatio;
    @Bind(R.id.veps) @Nullable protected TextView mEps;
    @Bind(R.id.vvolume) @Nullable protected TextView mVolume;
    @Bind(R.id.vavg_volume) @Nullable protected TextView mAvgVolume;

    @Inject SecurityCompactCacheRx securityCompactCacheRx;
    @Inject Picasso picasso;
    @Inject ChartDTOFactory chartDTOFactory;
    //TODO Change Analytics
    //@Inject Analytics analytics;
    @Inject FragmentOuterElements fragmentElements;
    @Inject UserWatchlistPositionCacheRx userWatchlistPositionCache;
    @Inject AlertCompactListCacheRx alertCompactListCache;
    @Inject CurrentUserId currentUserId;
    @Inject RequestHeaders requestHeaders;
    @Inject Lazy<DashboardNavigator> navigator;

    private Runnable chooseChartImageSizeTask;
    private Callback chartImageCallback;
    private Subscription quoteSubscription;
    //private SignalRManager signalRManager;
    //private HubProxy hubProxy;

    @Nullable private Map<SecurityId, AlertCompactDTO> mappedAlerts;
    @Nullable protected WatchlistPositionDTOList watchedList;

    //<editor-fold desc="Arguments passing">
    public static void putChartTimeSpan(@NonNull Bundle args, @NonNull ChartTimeSpan chartTimeSpan)
    {
        args.putLong(BUNDLE_KEY_TIME_SPAN_SECONDS_LONG, chartTimeSpan.duration);
    }

    @Nullable private static ChartTimeSpan getChartTimeSpan(@NonNull Bundle args)
    {
        if (args.containsKey(BUNDLE_KEY_TIME_SPAN_SECONDS_LONG))
        {
            return new ChartTimeSpan(args.getLong(BUNDLE_KEY_TIME_SPAN_SECONDS_LONG));
        }
        return null;
    }

    public static void putButtonSetVisibility(@NonNull Bundle args, @ViewVisibilityValue int visibility)
    {
        args.putInt(BUNDLE_KEY_TIME_SPAN_BUTTON_SET_VISIBILITY, visibility);
    }

    @ViewVisibilityValue private static int getButtonSetVisibility(@NonNull Bundle args, @ViewVisibilityValue int defaultValue)
    {
        int visibility = args.getInt(BUNDLE_KEY_TIME_SPAN_BUTTON_SET_VISIBILITY, defaultValue);
        switch (visibility)
        {
            case View.VISIBLE:
                return View.VISIBLE;
            case View.INVISIBLE:
                return View.INVISIBLE;
            case View.GONE:
                return View.GONE;
        }
        throw new IllegalArgumentException("Visibility " + visibility + " is not a valid value");
    }

    public static void putChartSize(@NonNull Bundle args, @NonNull ChartSize chartSize)
    {
        args.putIntArray(BUNDLE_KEY_CHART_SIZE_ARRAY_INT, chartSize.getSizeArray());
    }

    @Nullable private static ChartSize getChartSize(@NonNull Bundle args)
    {
        int[] intArray = args.getIntArray(BUNDLE_KEY_CHART_SIZE_ARRAY_INT);
        if (intArray != null)
        {
            new ChartSize(intArray);
        }
        return null;
    }
    //</editor-fold>

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        HierarchyInjector.inject(this);
        chartDTO = chartDTOFactory.createChartDTO();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);

        ButterKnife.bind(this, view);

        Bundle args = getArguments();
        if (args != null)
        {
            timeSpanButtonSetVisibility = getButtonSetVisibility(args, timeSpanButtonSetVisibility);
            ChartTimeSpan timeSpan = getChartTimeSpan(args);
            if (timeSpan != null)
            {
                chartDTO.setChartTimeSpan(timeSpan);
            }
            ChartSize chartSize = getChartSize(args);
            if (chartSize != null)
            {
                chartDTO.setChartSize(chartSize);
            }
        }

        chartDTO.setIncludeVolume(chartImage.includeVolume);

        // Override with saved value if any
        if (savedInstanceState != null)
        {
            timeSpanButtonSetVisibility = getButtonSetVisibility(savedInstanceState, timeSpanButtonSetVisibility);
            ChartTimeSpan timeSpan = getChartTimeSpan(savedInstanceState);
            if (timeSpan != null)
            {
                chartDTO.setChartTimeSpan(timeSpan);
            }
            ChartSize chartSize = getChartSize(savedInstanceState);
            if (chartSize != null)
            {
                chartDTO.setChartSize(chartSize);
            }
        }

        if (chartImage != null)
        {
            chartImage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            if (getActivity().getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            {
                chartImage.setOnClickListener(createChartImageClickListener());
            }
        }

        this.timeSpanButtonSetListener = new TimeSpanButtonSet.OnTimeSpanButtonSelectedListener()
        {
            @Override public void onTimeSpanButtonSelected(ChartTimeSpan selected)
            {
                //TODO Change Analytics
                //analytics.fireEvent(new ChartTimeEvent(securityId, selected));
                linkWith(selected);
            }
        };

        TimeSpanButtonSet timeSpanButtonSetTemp = (TimeSpanButtonSet) view.findViewById(R.id.chart_time_span_button_set);
        if (timeSpanButtonSetTemp != null)
        {
            timeSpanButtonSetTemp.addAllChildButtons();
            timeSpanButtonSetTemp.setListener(this.timeSpanButtonSetListener);
        }
        this.timeSpanButtonSet = timeSpanButtonSetTemp;

        if (mCloseButton != null)
        {
            mCloseButton.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    Bundle args = new Bundle();
                    putChartTimeSpan(args, chartDTO.getChartTimeSpan());
                    Intent result = new Intent();
                    result.putExtras(args);
                    ChartFragment.this.getActivity().setResult(Activity.RESULT_OK, result);
                    ChartFragment.this.getActivity().finish();
                }
            });
        }

        chartImageCallback = new AspectRatioImageViewCallback(chartImage)
        {
            @Override public void onSuccess()
            {
                super.onSuccess();
                if (chartImageWrapper != null)
                {
                    chartImageWrapper.setDisplayedChildByLayoutId(chartImage.getId());
                }
            }

            @Override public void onError()
            {
                super.onError();
                Timber.d("Load chartImage error");
            }
        };

        if (scrollView != null)
        {
            scrollView.setOnScrollChangedListener(fragmentElements.getScrollViewListener());
        }

        return view;
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (!(getParentFragment() instanceof AbstractBuySellFragment))
        {
            //Chart Fragment in layout-land mode.
            fetchSecurity();
        }
        else
        {
            onDestroyViewSubscriptions.add(Observable.combineLatest(createAlertsObservable(), createWatchlistObservable(),
                    new Func2<Map<SecurityId, AlertCompactDTO>, WatchlistPositionDTOList, Pair<Map<SecurityId, AlertCompactDTO>, WatchlistPositionDTOList>>()
                    {
                        @Override
                        public Pair<Map<SecurityId, AlertCompactDTO>, WatchlistPositionDTOList> call(
                                Map<SecurityId, AlertCompactDTO> securityIdAlertCompactDTOMap,
                                WatchlistPositionDTOList watchlistPositionDTOs)
                        {
                            return Pair.create(securityIdAlertCompactDTOMap, watchlistPositionDTOs);
                        }
                    })
                    .subscribe(
                            new Action1<Pair<Map<SecurityId, AlertCompactDTO>, WatchlistPositionDTOList>>()
                            {
                                @Override public void call(Pair<Map<SecurityId, AlertCompactDTO>, WatchlistPositionDTOList> pair)
                                {
                                    stockIsWatched(pair.second);
                                    stockIsOnAlert(pair.first);
                                }
                            },
                            new TimberOnErrorAction1("Failed to listen to alerts and watchlist on chart fragment")));
        }
    }

    @NonNull protected Observable<Map<SecurityId, AlertCompactDTO>> createAlertsObservable()
    {
        return alertCompactListCache.getOneSecurityMappedAlerts(currentUserId.toUserBaseKey())
                .map(new Func1<Map<SecurityId, AlertCompactDTO>, Map<SecurityId, AlertCompactDTO>>()
                {
                    @Override public Map<SecurityId, AlertCompactDTO> call(Map<SecurityId, AlertCompactDTO> map)
                    {
                        mappedAlerts = map;
                        return map;
                    }
                })
                .share();
    }

    @NonNull protected Observable<WatchlistPositionDTOList> createWatchlistObservable()
    {
        return userWatchlistPositionCache.getOne(currentUserId.toUserBaseKey())
                .map(new Func1<android.util.Pair<UserBaseKey, WatchlistPositionDTOList>, WatchlistPositionDTOList>()
                {
                    @Override
                    public WatchlistPositionDTOList call(@NonNull android.util.Pair<UserBaseKey, WatchlistPositionDTOList> pair)
                    {
                        watchedList = pair.second;
                        return pair.second;
                    }
                })
                .share();
    }

    private void stockIsWatched(WatchlistPositionDTOList watchedList)
    {
        this.watchedList = watchedList;
        if (btnWatched != null)
        {
            boolean watched = watchedList.contains(securityId);
            Drawable drawable = DrawableCompat.wrap(btnWatched.getDrawable());
            DrawableCompat.setTint(
                    drawable,
                    getResources().getColor(watched
                            ? COLOR_RES_WATCHED
                            : COLOR_RES_UNWATCHED));
            btnWatched.setImageDrawable(drawable);
            btnWatched.setAlpha(watched ? ALPHA_ACTIVE : ALPHA_INACTIVE);
        }
    }

    private void stockIsOnAlert(Map<SecurityId, AlertCompactDTO> mappedAlerts)
    {
        this.mappedAlerts = mappedAlerts;
        if (btnAlerted != null)
        {
            float alpha;
            AlertCompactDTO compactDTO = mappedAlerts.get(securityId);
            if ((compactDTO != null) && compactDTO.active)
            {
                alpha = ALPHA_ACTIVE;
            }
            else
            {
                alpha = ALPHA_INACTIVE;
            }
            btnAlerted.setAlpha(alpha);
        }
    }

    @SuppressWarnings("unused")
    @Nullable @OnClick(R.id.btn_watched)
    protected void onButtonWatchedClicked(View view)
    {
        if (watchedList != null)
        {
            handleAddToWatchlistRequested(securityId, watchedList.findFirstWhere(new Predicate<WatchlistPositionDTO>()
            {
                @Override public boolean apply(WatchlistPositionDTO watchlistPositionDTO)
                {
                    return watchlistPositionDTO.securityDTO != null
                            && watchlistPositionDTO.securityDTO.getSecurityId().equals(securityId);
                }
            }) == null);
        }
    }

    @SuppressWarnings("unused")
    @Nullable @OnClick(R.id.btn_alerted)
    protected void onButtonAlertedClicked(View view)
    {
        if (mappedAlerts != null)
        {
            AlertCompactDTO alert = mappedAlerts.get(securityId);
            if (alert == null)
            {
                handleAddAlertRequested(securityId);
            }
            else
            {
                handleUpdateAlertRequested(alert);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //signalRManager = new SignalRManager(requestHeaders, currentUserId);
        //hubProxy = signalRManager.getDefaultProxy();
        //signalRBuySellPrices();
        if (getParentFragment() instanceof AbstractBuySellFragment) {
                quoteSubscription = Observable.combineLatest(((AbstractBuySellFragment) getParentFragment()).quoteObservable,
                        ((AbstractBuySellFragment) getParentFragment()).securityObservable.observeOn(AndroidSchedulers.mainThread())
                                .doOnNext(new Action1<SecurityCompactDTO>() {
                                    @Override
                                    public void call(SecurityCompactDTO securityCompactDTO) {
                                        linkWith(securityCompactDTO);
                                    }
                                }), new Func2<QuoteDTO, SecurityCompactDTO, Pair<SecurityCompactDTO, QuoteDTO>>() {
                            @Override
                            public Pair<SecurityCompactDTO, QuoteDTO> call(QuoteDTO quoteDTO, SecurityCompactDTO securityCompactDTO) {
                                return Pair.create(securityCompactDTO, quoteDTO);
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                new Action1<Pair<SecurityCompactDTO, QuoteDTO>>() {
                                    @Override
                                    public void call(Pair<SecurityCompactDTO, QuoteDTO> securityCompactDTOQuoteDTOPair) {
                                        //if(securityCompactDTOQuoteDTOPair.second!=null)
                                            //displayBuySellPrice(securityCompactDTOQuoteDTOPair.first, securityCompactDTOQuoteDTOPair.second.ask, securityCompactDTOQuoteDTOPair.second.bid);
                                    }
                                },
                                new TimberOnErrorAction1("Failed to combine quote and security in ChartFragment"));
            }
        }

    public void displayBuySellPrice(@NonNull SecurityCompactDTO securityCompactDTO, Double ask, Double bid)
    {
        if (buyPrice != null && sellPrice != null)
        {
            String display = securityCompactDTO.currencyDisplay;
            String bPrice;
            String sPrice;
            THSignedNumber bthSignedNumber;
            THSignedNumber sthSignedNumber;
            if (ask == null)
            {
                bPrice = getString(R.string.buy_sell_ask_price_not_available);
            }
            else
            {
                bthSignedNumber = THSignedNumber.builder(ask)
                        .withOutSign()
                        .build();
                bPrice = bthSignedNumber.toString();
            }

            if (bid == null)
            {
                sPrice = getString(R.string.buy_sell_bid_price_not_available);
            }
            else
            {
                sthSignedNumber = THSignedNumber.builder(bid)
                        .withOutSign()
                        .build();
                sPrice = sthSignedNumber.toString();
            }
            String buyPriceText = getString(R.string.buy_sell_button_buy, display, bPrice);
            String sellPriceText = getString(R.string.buy_sell_button_sell, display, sPrice);
            buyPrice.setText(buyPriceText);
            sellPrice.setText(sellPriceText);
        }

        displayStockRoi(securityCompactDTO);
    }

    private void displayStockRoi(SecurityCompactDTO securityCompactDTO)
    {
        if (stockRoi != null)
        {
            if (securityCompactDTO != null && securityCompactDTO.risePercent != null)
            {
                double roi = securityCompactDTO.risePercent;
                THSignedPercentage
                        .builder(roi * 100)
                        .withSign()
                        .relevantDigitCount(3)
                        .withDefaultColor()
                        .defaultColorForBackground()
                        .signTypePlusMinusAlways()
                        .build()
                        .into(stockRoi);
            }
            else
            {
                //tvStockRoi.setText(R.string.na);
                stockRoi.setVisibility(View.GONE);
            }
        }
    }

    private void displayMarketClosedHint(SecurityCompactDTO securityCompactDTO)
    {
        if (marketCloseHint != null)
        {
            boolean marketIsOpen = securityCompactDTO.marketOpen == null
                    || securityCompactDTO.marketOpen;
            marketCloseHint.setVisibility(marketIsOpen ? View.GONE : View.VISIBLE);
            if (!marketIsOpen)
            {
                marketCloseHint.setText(getMarketCloseHint(securityCompactDTO));
            }
        }
    }

    @NonNull private String getMarketCloseHint(@NonNull SecurityCompactDTO securityCompactDTO)
    {
        TillExchangeOpenDuration duration = securityCompactDTO.getTillExchangeOpen();
        if (duration == null)
        {
            return "";
        }
        return getString(R.string.market_close_hint) + " " +
                DateUtils.getDurationText(getResources(), duration.days, duration.hours, duration.minutes);
    }

    @Override public void onPause()
    {
        super.onPause();
        if (quoteSubscription != null)
        {
            quoteSubscription.unsubscribe();
            quoteSubscription = null;
        }
    }

    @Nullable @OnClick(R.id.market_close_hint)
    protected void notifyMarketClosed()
    {
        onStopSubscriptions.add(AlertDialogBuySellRxUtil.popMarketClosed(getActivity(), securityId)
                .subscribe(new EmptyAction1<OnDialogClickEvent>(),
                        new EmptyAction1<Throwable>()));
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        putChartTimeSpan(outState, chartDTO.getChartTimeSpan());
        putChartSize(outState, chartDTO.getChartSize());
        putButtonSetVisibility(outState, timeSpanButtonSetVisibility);
    }

    protected void handleAddToWatchlistRequested(@NonNull SecurityId securityId, boolean isAdd)
    {
        Bundle args = new Bundle();
        WatchlistEditFragment.putSecurityId(args, securityId);
        if (isAdd)
        {
            //TODO Change Analytics
            //analytics.addEvent(new SimpleEvent(AnalyticsConstants.Monitor_CreateWatchlist));
            ActionBarOwnerMixin.putActionBarTitle(args, getString(R.string.watchlist_add_title));
        }
        else
        {
            //TODO Change Analytics
            //analytics.addEvent(new SimpleEvent(AnalyticsConstants.Monitor_EditWatchlist));
            ActionBarOwnerMixin.putActionBarTitle(args, getString(R.string.watchlist_edit_title));
        }
        if (navigator != null)
        {
            navigator.get().pushFragment(WatchlistEditFragment.class, args);
        }
    }

    protected void handleUpdateAlertRequested(@NonNull AlertCompactDTO alertCompactDTO)
    {
        AlertEditDialogFragment.newInstance(alertCompactDTO.getAlertId(currentUserId.toUserBaseKey()))
                .show(getFragmentManager(), AlertEditDialogFragment.class.getName());
    }

    protected void handleAddAlertRequested(@NonNull SecurityId securityId)
    {
        AlertCreateDialogFragment.newInstance(securityId)
                .show(getFragmentManager(), BaseAlertEditDialogFragment.class.getName());
    }

    @Override public void onDestroyView()
    {
        if (chartImage != null)
        {
            chartImage.setOnClickListener(null);
        }
        this.chartImage = null;

        TimeSpanButtonSet buttonSet = this.timeSpanButtonSet;
        if (buttonSet != null)
        {
            buttonSet.setListener(null);
        }
        this.timeSpanButtonSet = null;
        this.timeSpanButtonSetListener = null;
        View rootView = getView();
        if (chooseChartImageSizeTask != null && rootView != null)
        {
            rootView.removeCallbacks(chooseChartImageSizeTask);
            chooseChartImageSizeTask = null;
        }
        if (mCloseButton != null)
        {
            mCloseButton.setOnClickListener(null);
            mCloseButton = null;
        }
        if (scrollView != null)
        {
            scrollView.setOnScrollChangedListener(null);
        }
        watchedList = null;
        mappedAlerts = null;
        chartImageCallback = null;
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    public int getTimeSpanButtonSetVisibility()
    {
        return timeSpanButtonSetVisibility;
    }

    public void setTimeSpanButtonSetVisibility(int timeSpanButtonSetVisibility)
    {
        this.timeSpanButtonSetVisibility = timeSpanButtonSetVisibility;
        displayTimeSpanButtonSet();
    }

    protected void fetchSecurity()
    {
        if (securityId != null)
        {
            onDestroyViewSubscriptions.add(AppObservable.bindSupportFragment(
                    this,
                    securityCompactCacheRx.get(securityId))
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
                            new ToastOnErrorAction1(getString(R.string.error_fetch_security_info))));
        }
    }

    public void linkWith(SecurityCompactDTO value)
    {
        securityCompactDTO = value;
        if (value != null)
        {
            ChartDTO chartDTOCopy = chartDTO;
            if (chartDTOCopy != null)
            {
                chartDTOCopy.setSecurityCompactDTO(value);
            }
            linkWith((value instanceof WarrantDTO) ? (WarrantDTO) value : null);
        }
        displayMarketClosedHint(value);
        displayChartImage();
        displayTimeSpanButtonSet();
        displayPreviousClose();
        displayOpen();
        displayDaysHigh();
        displayDaysLow();
        displayPERatio();
        displayEps();
        displayVolume();
        displayAvgVolume();
    }

    public void linkWith(ChartTimeSpan timeSpan)
    {
        chartDTO.setChartTimeSpan(timeSpan);
        displayChartImage();
        displayTimeSpanButtonSet();
    }

    public void linkWith(@Nullable WarrantDTO warrantDTO)
    {
        this.warrantDTO = warrantDTO;
        displayWarrantRows();
        displayWarrantType();
        displayWarrantCode();
        displayExpiry();
        displayStrikePrice();
        displayUnderlying();
        displayIssuer();
    }

    public void displayTimeSpanButtonSet()
    {
        TimeSpanButtonSet buttonSet = this.timeSpanButtonSet;
        if (!isDetached() && buttonSet != null)
        {
            buttonSet.setVisibility(timeSpanButtonSetVisibility);
            buttonSet.setActive(chartDTO.getChartTimeSpan());
        }
    }

    public void displayChartImage()
    {
        ImageView image = this.chartImage;
        if (!isDetached() && image != null)
        {
            String imageURL = chartDTO.getChartUrl();
            // HACK TODO find something better than skipCache to avoid OutOfMemory
            this.picasso
                    .load(imageURL)
                    .skipMemoryCache()
                    .into(image, chartImageCallback);

            if (getActivity().getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            {
                postChooseOtherSize();
            }
        }
    }

    public void postChooseOtherSize()
    {
        chooseChartImageSizeTask = new Runnable()
        {
            @Override public void run()
            {
                ChartFragment.this.chooseOtherSize();
            }
        };
        postDelayed(chooseChartImageSizeTask, 500);
    }

    protected void chooseOtherSize()
    {
        ImageView image = chartImage;
        if (image != null)
        {
            ChartSize currentSize = chartDTO.getChartSize();
            chartDTO.setChartSize(new ChartSize(image.getWidth(), image.getHeight()));
            if (!chartDTO.getChartSize().equals(currentSize))
            {
                displayChartImage();
            }
        }
    }

    protected void postDelayed(Runnable runnable, long delayMillis)
    {
        View view = getView();
        if (view != null)
        {
            view.postDelayed(runnable, delayMillis);
        }
    }

    @NonNull private View.OnClickListener createChartImageClickListener()
    {
        return new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
                //Intent intent = new Intent(BuySellFragment.EVENT_CHART_IMAGE_CLICKED);
                //LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                Intent intent = new Intent(ChartFragment.this.getActivity().getApplicationContext(), StockChartActivity.class);
                StockChartActivity.putSecurityId(intent, securityId);
                StockChartActivity.putChartTimeSpan(intent, chartDTO.getChartTimeSpan());
                StockChartActivity.putButtonSetVisibility(intent, timeSpanButtonSetVisibility);
                ChartFragment.this.getActivity().startActivityForResult(intent, STOCK_ACTIVITY_REQUEST_CODE);
            }
        };
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == STOCK_ACTIVITY_REQUEST_CODE && data != null)
        {
            Bundle extras = data.getExtras();
            if (extras != null)
            {
                ChartTimeSpan timeSpan = getChartTimeSpan(extras);
                if (timeSpan != null)
                {
                    linkWith(timeSpan);
                }
            }
        }
    }

    public void displayWarrantRows()
    {
        if (rowWarrantType != null)
        {
            rowWarrantType.setVisibility(getWarrantVisibility());
        }
        if (rowWarrantCode != null)
        {
            rowWarrantCode.setVisibility(getWarrantVisibility());
        }
        if (rowWarrantExpiry != null)
        {
            rowWarrantExpiry.setVisibility(getWarrantVisibility());
        }
        if (rowStrikePrice != null)
        {
            rowStrikePrice.setVisibility(getWarrantVisibility());
        }
        if (rowUnderlying != null)
        {
            rowUnderlying.setVisibility(getWarrantVisibility());
        }
        if (rowIssuer != null)
        {
            rowIssuer.setVisibility(getWarrantVisibility());
        }
    }

    @ViewVisibilityValue private int getWarrantVisibility()
    {
        return (warrantDTO == null) ? View.GONE : View.VISIBLE;
    }

    public void displayWarrantType()
    {
        if (!isDetached() && mWarrantType != null)
        {
            if (warrantDTO == null || warrantDTO.getWarrantType() == null)
            {
                mWarrantType.setText(R.string.na);
            }
            else
            {
                int warrantTypeStringResId;
                switch (warrantDTO.getWarrantType())
                {
                    case CALL:
                        warrantTypeStringResId = R.string.warrant_type_call;
                        break;
                    case PUT:
                        warrantTypeStringResId = R.string.warrant_type_put;
                        break;
                    default:
                        throw new IllegalArgumentException("Unhandled warrant type " + warrantDTO.getWarrantType());
                }
                mWarrantType.setText(warrantTypeStringResId);
            }
        }
    }

    public void displayWarrantCode()
    {
        if (!isDetached() && mWarrantCode != null)
        {
            if (securityCompactDTO == null || securityCompactDTO.symbol == null)
            {
                mWarrantCode.setText(R.string.na);
            }
            else
            {
                mWarrantCode.setText(securityCompactDTO.symbol);
            }
        }
    }

    public void displayExpiry()
    {
        if (!isDetached() && mWarrantExpiry != null)
        {
            if (warrantDTO == null || warrantDTO.expiryDate == null)
            {
                mWarrantExpiry.setText(R.string.na);
            }
            else
            {
                SimpleDateFormat df = new SimpleDateFormat("d MMM yy", Locale.US);
                mWarrantExpiry.setText(df.format(warrantDTO.expiryDate));
            }
        }
    }

    public void displayStrikePrice()
    {
        if (!isDetached() && mStrikePrice != null)
        {
            if (warrantDTO == null || warrantDTO.strikePrice == null || warrantDTO.strikePriceCcy == null)
            {
                mStrikePrice.setText(R.string.na);
            }
            else
            {
                mStrikePrice.setText(THSignedMoney.builder(warrantDTO.strikePrice)
                        .currency(warrantDTO.strikePriceCcy)
                        .build().toString());
            }
        }
    }

    public void displayUnderlying()
    {
        if (!isDetached() && mUnderlying != null)
        {
            if (warrantDTO == null || warrantDTO.underlyingName == null)
            {
                mUnderlying.setText(R.string.na);
            }
            else
            {
                mUnderlying.setText(warrantDTO.underlyingName);
            }
        }
    }

    public void displayIssuer()
    {
        if (!isDetached() && mIssuer != null)
        {
            if (warrantDTO == null || warrantDTO.issuerName == null)
            {
                mIssuer.setText(R.string.na);
            }
            else
            {
                mIssuer.setText(warrantDTO.issuerName.toUpperCase()); // HACK upperCase
            }
        }
    }

    public void displayPreviousClose()
    {
        if (!isDetached() && mPreviousClose != null)
        {
            if (securityCompactDTO == null || securityCompactDTO.previousClose == null)
            {
                mPreviousClose.setText(R.string.na);
            }
            else
            {
                mPreviousClose.setText(THSignedMoney.builder(securityCompactDTO.previousClose)
                        .currency(securityCompactDTO.currencyDisplay)
                        .build().toString());
            }
        }
    }

    public void displayOpen()
    {
        if (!isDetached() && mOpen != null)
        {
            if (securityCompactDTO == null || securityCompactDTO.open == null)
            {
                mOpen.setText(R.string.na);
            }
            else
            {
                mOpen.setText(THSignedMoney.builder(securityCompactDTO.open)
                        .currency(securityCompactDTO.currencyDisplay)
                        .build().toString());
            }
        }
    }

    public void displayDaysHigh()
    {
        if (!isDetached() && mDaysHigh != null)
        {
            if (securityCompactDTO == null || securityCompactDTO.high == null)
            {
                mDaysHigh.setText(R.string.na);
            }
            else
            {
                mDaysHigh.setText(THSignedMoney.builder(securityCompactDTO.high)
                        .currency(securityCompactDTO.currencyDisplay)
                        .build().toString());
            }
        }
    }

    public void displayDaysLow()
    {
        if (!isDetached() && mDaysLow != null)
        {
            if (securityCompactDTO == null || securityCompactDTO.low == null)
            {
                mDaysLow.setText(R.string.na);
            }
            else
            {
                mDaysLow.setText(THSignedMoney.builder(securityCompactDTO.low)
                        .currency(securityCompactDTO.currencyDisplay)
                        .build().toString());
            }
        }
    }

    public void displayPERatio()
    {
        if (!isDetached() && mPERatio != null)
        {
            if (securityCompactDTO == null || securityCompactDTO.pe == null)
            {
                mPERatio.setText(R.string.na);
            }
            else
            {
                mPERatio.setText(THSignedNumber.builder(securityCompactDTO.pe)
                        .build().toString());
            }
        }
    }

    public void displayEps()
    {
        if (!isDetached() && mEps != null)
        {
            if (securityCompactDTO == null || securityCompactDTO.eps == null)
            {
                mEps.setText(R.string.na);
            }
            else
            {
                mEps.setText(THSignedNumber.builder(securityCompactDTO.eps)
                        .build().toString());
            }
        }
    }

    public void displayVolume()
    {
        if (!isDetached() && mVolume != null)
        {
            if (securityCompactDTO == null || securityCompactDTO.volume == null)
            {
                mVolume.setText(R.string.na);
            }
            else
            {
                mVolume.setText(THSignedNumber.builder(securityCompactDTO.volume)
                        .build().toString());
            }
        }
    }

    public void displayAvgVolume()
    {
        if (!isDetached() && mAvgVolume != null)
        {
            if (securityCompactDTO == null || securityCompactDTO.averageDailyVolume == null)
            {
                mAvgVolume.setText(R.string.na);
            }
            else
            {
                mAvgVolume.setText(THSignedNumber.builder(securityCompactDTO.averageDailyVolume)
                        .build().toString());
            }
        }
    }
}


