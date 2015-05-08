package com.tradehero.th.fragments.trade;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.InjectView;
import com.android.common.SlidingTabLayout;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.tradehero.th.R;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.compact.WarrantDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import com.tradehero.th.fragments.alert.AlertCreateDialogFragment;
import com.tradehero.th.fragments.alert.AlertEditDialogFragment;
import com.tradehero.th.fragments.alert.BaseAlertEditDialogFragment;
import com.tradehero.th.fragments.security.BuySellBottomStockPagerAdapter;
import com.tradehero.th.fragments.security.SecurityCircleProgressBar;
import com.tradehero.th.fragments.security.WatchlistEditFragment;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.models.portfolio.MenuOwnedPortfolioId;
import com.tradehero.th.persistence.alert.AlertCompactListCacheRx;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCacheRx;
import com.tradehero.th.rx.TimberOnErrorAction;
import com.tradehero.th.utils.DateUtils;
import com.tradehero.th.utils.GraphicUtil;
import com.tradehero.th.utils.StringUtils;
import com.tradehero.th.utils.metrics.events.BuySellEvent;
import com.tradehero.th.utils.metrics.events.ChartTimeEvent;
import java.util.Map;
import javax.inject.Inject;
import rx.Observer;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

@Routable({
        "security/:securityRawInfo",
        "stockSecurity/:exchange/:symbol"
})
public class BuySellStockFragment extends BuySellFragment
{
    private static final float WATCHED_ALPHA_UNWATCHED = 0.5f;
    private static final float WATCHED_ALPHA_WATCHED = 1f;

    @InjectView(R.id.buy_price) protected TextView mBuyPrice;
    @InjectView(R.id.sell_price) protected TextView mSellPrice;
    @InjectView(R.id.vprice_as_of) protected TextView mVPriceAsOf;
    @InjectView(R.id.tabs) protected SlidingTabLayout mSlidingTabLayout;

    @InjectView(R.id.chart_frame) protected RelativeLayout mInfoFrame;
    @InjectView(R.id.trade_bottom_pager) protected ViewPager mBottomViewPager;

    @InjectView(R.id.tv_stock_roi) protected TextView tvStockRoi;

    @Inject UserWatchlistPositionCacheRx userWatchlistPositionCache;
    @Inject AlertCompactListCacheRx alertCompactListCache;
    @Inject Analytics analytics;

    @RouteProperty("exchange") String exchange;
    @RouteProperty("symbol") String symbol;

    private PortfolioCompactDTO defaultPortfolio;

    @Nullable protected WatchlistPositionDTOList watchedList;

    private BuySellBottomStockPagerAdapter bottomViewPagerAdapter;
    @Nullable private Map<SecurityId, AlertCompactDTO> mappedAlerts;

    protected TextView mTvStockTitle;
    protected TextView mTvStockSubTitle;
    protected SecurityCircleProgressBar circleProgressBar;
    protected ImageView btnWatched;
    protected View btnAlerted;
    protected View marketCloseIcon;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (securityId == null)
        {
            securityId = new SecurityId(exchange, symbol);
        }
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_stock_buy_sell, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mSlidingTabLayout.setCustomTabView(R.layout.th_page_indicator, android.R.id.title);
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.tradehero_tab_indicator_color));
    }

    @Override public void onStart()
    {
        super.onStart();
        analytics.fireEvent(new ChartTimeEvent(securityId, BuySellBottomStockPagerAdapter.getDefaultChartTimeSpan()));
        fetchAlertCompactList();
        fetchWatchlist();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        if (mTvStockTitle == null)
        {
            LayoutInflater inflator = LayoutInflater.from(actionBar.getThemedContext());
            View v = inflator.inflate(R.layout.stock_detail_custom_actionbar, null);
            mTvStockTitle = (TextView) v.findViewById(R.id.tv_stock_title);
            mTvStockSubTitle = (TextView) v.findViewById(R.id.tv_stock_sub_title);
            circleProgressBar = (SecurityCircleProgressBar) v.findViewById(R.id.circle_progressbar);
            marketCloseIcon = v.findViewById(R.id.action_bar_market_closed_icon);

            btnWatched = (ImageView) v.findViewById(R.id.btn_watched);
            btnAlerted = v.findViewById(R.id.btn_alerted);

            btnAlerted.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    handleBtnAddTriggerClicked();
                }
            });

            btnWatched.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    handleBtnWatchlistClicked();
                }
            });

            actionBar.setCustomView(v);

            if (securityCompactDTO != null)
            {
                displayActionBar(securityCompactDTO);
            }
        }
    }

    @Override public void onDestroyOptionsMenu()
    {
        btnAlerted = null;
        btnWatched = null;
        circleProgressBar = null;
        mTvStockSubTitle = null;
        mTvStockTitle = null;
        super.onDestroyOptionsMenu();
    }

    @Override public void onDestroyView()
    {
        bottomViewPagerAdapter = null;
        defaultPortfolio = null;
        super.onDestroyView();
    }

    public void fetchAlertCompactList()
    {
        unsubscribe(alertCompactListCacheSubscription);
        alertCompactListCacheSubscription = AppObservable.bindFragment(
                this,
                alertCompactListCache.getSecurityMappedAlerts(currentUserId.toUserBaseKey()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Map<SecurityId, AlertCompactDTO>>()
                {
                    @Override public void onCompleted()
                    {
                    }

                    @Override public void onError(Throwable e)
                    {
                        Timber.e(e, "There was an error getting the alert ids");
                        displayTriggerButton();
                    }

                    @Override public void onNext(Map<SecurityId, AlertCompactDTO> securityIdAlertIdMap)
                    {
                        mappedAlerts = securityIdAlertIdMap;
                        displayTriggerButton();
                    }
                });
    }

    public void fetchWatchlist()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                        this,
                        userWatchlistPositionCache.get(currentUserId.toUserBaseKey()))
                        .observeOn(AndroidSchedulers.mainThread())
                        .take(1)
                        .subscribe(new Observer<Pair<UserBaseKey, WatchlistPositionDTOList>>()
                        {
                            @Override public void onCompleted()
                            {
                            }

                            @Override public void onError(Throwable e)
                            {
                                Timber.e(e, "Failed to fetch list of watch list items");
                                THToast.show(R.string.error_fetch_portfolio_list_info);
                            }

                            @Override public void onNext(Pair<UserBaseKey, WatchlistPositionDTOList> pair)
                            {
                                linkWith(pair.second);
                            }
                        })
        );
    }

    protected void linkWith(@NonNull PortfolioCompactDTOList portfolioCompactDTOs)
    {
        defaultPortfolio = portfolioCompactDTOs.getDefaultPortfolio();
        addDefaultMainPortfolioIfShould();
        setInitialSellQuantityIfCan();
    }

    protected void addDefaultMainPortfolioIfShould()
    {
        if (defaultPortfolio != null && securityCompactDTO != null && !(securityCompactDTO instanceof WarrantDTO))
        {
            mSelectedPortfolioContainer.addMenuOwnedPortfolioId(new MenuOwnedPortfolioId(currentUserId.toUserBaseKey(), defaultPortfolio));
        }
    }

    @Override protected void linkWithApplicable(OwnedPortfolioId purchaseApplicablePortfolioId, boolean andDisplay)
    {
        super.linkWithApplicable(purchaseApplicablePortfolioId, andDisplay);
        if (bottomViewPagerAdapter == null)
        {
            bottomViewPagerAdapter = new BuySellBottomStockPagerAdapter(
                    getActivity(),
                    this.getChildFragmentManager(),
                    purchaseApplicablePortfolioId,
                    securityId,
                    currentUserId.toUserBaseKey());
            mBottomViewPager.setAdapter(bottomViewPagerAdapter);
            mSlidingTabLayout.setViewPager(mBottomViewPager);
        }
    }

    protected void linkWith(WatchlistPositionDTOList watchedList)
    {
        this.watchedList = watchedList;
        displayWatchlistButton();
    }

    @Override public void linkWith(@NonNull SecurityCompactDTO securityCompactDTO, boolean andDisplay)
    {
        super.linkWith(securityCompactDTO, andDisplay);
        addDefaultMainPortfolioIfShould();
        if (andDisplay)
        {
            //loadStockLogo();
            displayAsOf();
        }

        displayActionBar(securityCompactDTO);
    }

    protected void displayActionBar(@NonNull SecurityCompactDTO securityCompactDTO)
    {
        if (!StringUtils.isNullOrEmpty(securityCompactDTO.name))
        {
            if (mTvStockTitle != null)
            {
                mTvStockTitle.setText(securityCompactDTO.name);
            }
            if (mTvStockSubTitle != null)
            {
                mTvStockSubTitle.setText(securityCompactDTO.getExchangeSymbol());
            }
        }
        else
        {
            if (mTvStockTitle != null)
            {
                mTvStockTitle.setText(securityCompactDTO.getExchangeSymbol());
            }
            if (mTvStockSubTitle != null)
            {
                mTvStockSubTitle.setText(null);
            }
        }

        circleProgressBar.display(securityCompactDTO);
        if (marketCloseIcon != null)
        {
            boolean marketIsOpen = securityCompactDTO == null
                    || securityCompactDTO.marketOpen == null
                    || securityCompactDTO.marketOpen;
            marketCloseIcon.setVisibility(marketIsOpen ? View.GONE : View.VISIBLE);
        }
    }

    @Override protected void linkWith(QuoteDTO quoteDTO)
    {
        super.linkWith(quoteDTO);
        onStopSubscriptions.add(
                circleProgressBar.start(getMillisecondQuoteRefresh())
                        .subscribe(
                                new Action1<Boolean>()
                                {
                                    @Override public void call(Boolean aBoolean)
                                    {
                                        // Nothing to do, but we want to then request a new quote
                                    }
                                },
                                new TimberOnErrorAction("Failed to listen to end of animation")));
        displayAsOf();
    }

    @Override public void displayBuySellPrice()
    {
        if (mBuyPrice != null)
        {
            String display = securityCompactDTO == null ? "-" : securityCompactDTO.currencyDisplay;
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
                            .withOutSign()
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
                            .withOutSign()
                            .build();
                    sPrice = sthSignedNumber.toString();
                }
            }
            String buyPriceText = getString(R.string.buy_sell_button_buy, display, bPrice);
            String sellPriceText = getString(R.string.buy_sell_button_sell, display, sPrice);
            mBuyPrice.setText(buyPriceText);
            mSellPrice.setText(sellPriceText);
        }

        displayStockRoi();
    }

    public void displayAsOf()
    {
        if (mVPriceAsOf != null)
        {
            String text;
            if (quoteDTO != null && quoteDTO.asOfUtc != null)
            {
                text = DateUtils.getFormattedDate(getResources(), quoteDTO.asOfUtc);
            }
            else if (securityCompactDTO != null
                    && securityCompactDTO.lastPriceDateAndTimeUtc != null)
            {
                text = DateUtils.getFormattedDate(getResources(), securityCompactDTO.lastPriceDateAndTimeUtc);
            }
            else
            {
                text = "";
            }
            mVPriceAsOf.setText(
                    getResources().getString(R.string.buy_sell_price_as_of) + " " + text);
        }
    }

    public boolean isBuySellReady()
    {
        return quoteDTO != null && positionDTOList != null && applicableOwnedPortfolioIds != null;
    }

    public void displayTriggerButton()
    {
        if (btnAlerted != null)
        {
            btnAlerted.setVisibility(mappedAlerts != null ? View.VISIBLE : View.GONE);
            if (mappedAlerts != null)
            {
                float alpha;
                AlertCompactDTO compactDTO = mappedAlerts.get(securityId);
                if ((compactDTO != null) && compactDTO.active)
                {
                    alpha = 1.0f;
                }
                else
                {
                    alpha = 0.5f;
                }

                btnAlerted.setAlpha(alpha);
            }
        }
    }

    public void displayWatchlistButton()
    {
        if (btnWatched != null)
        {
            if (securityId == null || watchedList == null)
            {
                // TODO show disabled
                btnWatched.setVisibility(View.INVISIBLE);
            }
            else
            {
                btnWatched.setVisibility(View.VISIBLE);
                boolean watched = watchedList.contains(securityId);
                btnWatched.setAlpha(watched ?
                        WATCHED_ALPHA_WATCHED :
                        WATCHED_ALPHA_UNWATCHED);
                GraphicUtil.applyColorFilter(
                        btnWatched,
                        getResources().getColor(
                                watched
                                        ? R.color.watchlist_button_color
                                        : R.color.white));
            }
        }
    }

    @Override protected boolean getSupportSell()
    {
        boolean supportSell;
        if (positionDTOList == null
                || positionDTOList.size() == 0
                || purchaseApplicableOwnedPortfolioId == null)
        {
            supportSell = false;
        }
        else
        {
            Integer maxSellableShares = getMaxSellableShares();
            supportSell = maxSellableShares != null && maxSellableShares > 0;
        }
        return supportSell;
    }

    protected void handleBtnAddTriggerClicked()
    {
        if (mappedAlerts != null)
        {
            AlertCompactDTO alertDTO = mappedAlerts.get(securityId);
            BaseAlertEditDialogFragment dialog;
            if (alertDTO != null)
            {
                AlertId alertId = alertDTO.getAlertId(currentUserId.toUserBaseKey());
                dialog = AlertEditDialogFragment.newInstance(alertId);
            }
            else
            {
                dialog = AlertCreateDialogFragment.newInstance(securityId);
            }
            dialog.show(getFragmentManager(), BaseAlertEditDialogFragment.class.getName());
        }
        else
        {
            THToast.show(R.string.error_incomplete_info_message);
        }
    }

    protected void handleBtnWatchlistClicked()
    {
        if (securityId != null)
        {
            Bundle args = new Bundle();
            WatchlistEditFragment.putSecurityId(args, securityId);
            navigator.get().pushFragment(WatchlistEditFragment.class, args);
        }
        else
        {
            THToast.show(R.string.watchlist_not_enough_info);
        }
    }

    @Override protected void handleBuySellButtonsClicked(View view)
    {
        trackBuyClickEvent();
        super.handleBuySellButtonsClicked(view);
    }

    private void trackBuyClickEvent()
    {
        analytics.fireEvent(new BuySellEvent(isTransactionTypeBuy, securityId));
    }

    private void displayStockRoi()
    {
        if (tvStockRoi != null)
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
                        .into(tvStockRoi);
            }
            else
            {
                //tvStockRoi.setText(R.string.na);
                tvStockRoi.setVisibility(View.GONE);
            }
        }
    }
}
