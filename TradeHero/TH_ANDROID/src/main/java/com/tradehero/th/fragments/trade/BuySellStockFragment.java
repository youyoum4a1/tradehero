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
import com.tradehero.th.fragments.base.ActionBarOwnerMixin;
import com.tradehero.th.fragments.security.BuySellBottomStockPagerAdapter;
import com.tradehero.th.fragments.security.WatchlistEditFragment;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.models.portfolio.MenuOwnedPortfolioId;
import com.tradehero.th.persistence.alert.AlertCompactListCacheRx;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCacheRx;
import com.tradehero.th.rx.TimberOnErrorAction;
import com.tradehero.th.rx.ToastAndLogOnErrorAction;
import com.tradehero.th.utils.DateUtils;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.BuySellEvent;
import com.tradehero.th.utils.metrics.events.ChartTimeEvent;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import java.util.Map;
import javax.inject.Inject;
import rx.Observer;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

@Routable({
        "stock-security/:exchange/:symbol"
})
public class BuySellStockFragment extends BuySellFragment
{
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

    protected StockDetailActionBarRelativeLayout actionBarLayout;

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

        actionBarLayout = (StockDetailActionBarRelativeLayout) LayoutInflater.from(actionBar.getThemedContext())
                .inflate(R.layout.stock_detail_custom_actionbar, null);
        onDestroyOptionsMenuSubscriptions.add(actionBarLayout.getUserActionObservable()
                .subscribe(
                        new Action1<StockActionBarRelativeLayout.UserAction>()
                        {
                            @Override public void call(StockActionBarRelativeLayout.UserAction userAction)
                            {
                                if (userAction instanceof StockActionBarRelativeLayout.WatchlistUserAction)
                                {
                                    handleAddToWatchlistRequested((StockActionBarRelativeLayout.WatchlistUserAction) userAction);
                                }
                                else if (userAction instanceof StockActionBarRelativeLayout.UpdateAlertUserAction)
                                {
                                    handleUpdateAlertRequested((StockActionBarRelativeLayout.UpdateAlertUserAction) userAction);
                                }
                                else if (userAction instanceof StockActionBarRelativeLayout.CreateAlertUserAction)
                                {
                                    handleAddAlertRequested((StockActionBarRelativeLayout.CreateAlertUserAction) userAction);
                                }
                                else
                                {
                                    throw new IllegalArgumentException("Unhandled argument UserAction." + userAction);
                                }
                            }
                        },
                        new ToastAndLogOnErrorAction("Failed to handle action bar")));
        actionBar.setCustomView(actionBarLayout);
        displayActionBar();
    }

    @Override public void onDestroyOptionsMenu()
    {
        actionBarLayout = null;
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
                        displayActionBar();
                    }

                    @Override public void onNext(Map<SecurityId, AlertCompactDTO> securityIdAlertIdMap)
                    {
                        mappedAlerts = securityIdAlertIdMap;
                        displayActionBar();
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
        displayActionBar();
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

        displayActionBar();
    }

    protected void displayActionBar()
    {
        if (actionBarLayout != null)
        {
            actionBarLayout.display(new StockDetailActionBarRelativeLayout.Requisite(
                    securityId,
                    securityCompactDTO,
                    watchedList,
                    mappedAlerts));
        }
    }

    @Override protected void linkWith(QuoteDTO quoteDTO)
    {
        super.linkWith(quoteDTO);
        onStopSubscriptions.add(
                actionBarLayout.circleProgressBar.start(getMillisecondQuoteRefresh())
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

    protected void handleAddToWatchlistRequested(@NonNull StockActionBarRelativeLayout.WatchlistUserAction userAction)
    {
        Bundle args = new Bundle();
        WatchlistEditFragment.putSecurityId(args, userAction.securityId);
        if (userAction.add)
        {
            analytics.addEvent(new SimpleEvent(AnalyticsConstants.Monitor_CreateWatchlist));
            ActionBarOwnerMixin.putActionBarTitle(args, getString(R.string.watchlist_add_title));
        }
        else
        {
            analytics.addEvent(new SimpleEvent(AnalyticsConstants.Monitor_EditWatchlist));
            ActionBarOwnerMixin.putActionBarTitle(args, getString(R.string.watchlist_edit_title));
        }
        if (navigator != null)
        {
            navigator.get().pushFragment(WatchlistEditFragment.class, args);
        }
    }

    protected void handleUpdateAlertRequested(@NonNull StockActionBarRelativeLayout.UpdateAlertUserAction userAction)
    {
        AlertEditDialogFragment.newInstance(userAction.alertCompactDTO.getAlertId(currentUserId.toUserBaseKey()))
                .show(getFragmentManager(), AlertEditDialogFragment.class.getName());
    }

    protected void handleAddAlertRequested(@NonNull StockActionBarRelativeLayout.CreateAlertUserAction userAction)
    {
        AlertCreateDialogFragment.newInstance(userAction.securityId)
                .show(getFragmentManager(), BaseAlertEditDialogFragment.class.getName());
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
