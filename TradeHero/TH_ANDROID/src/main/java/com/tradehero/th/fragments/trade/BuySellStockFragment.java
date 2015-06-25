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
import com.tradehero.metrics.Analytics;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.tradehero.th.R;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import com.tradehero.th.fragments.alert.AlertCreateDialogFragment;
import com.tradehero.th.fragments.alert.AlertEditDialogFragment;
import com.tradehero.th.fragments.alert.BaseAlertEditDialogFragment;
import com.tradehero.th.fragments.base.ActionBarOwnerMixin;
import com.tradehero.th.fragments.base.LiveFragmentUtil;
import com.tradehero.th.fragments.security.BuySellBottomStockPagerAdapter;
import com.tradehero.th.fragments.security.WatchlistEditFragment;
import com.tradehero.th.fragments.social.FragmentUtils;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
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
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func3;

@Routable({
        "stock-security/:exchange/:symbol"
})
public class BuySellStockFragment extends AbstractBuySellFragment
{
    @InjectView(R.id.buy_price) protected TextView mBuyPrice;
    @InjectView(R.id.sell_price) protected TextView mSellPrice;
    @InjectView(R.id.vprice_as_of) protected TextView mVPriceAsOf;
    @InjectView(R.id.tabs) protected SlidingTabLayout mSlidingTabLayout;
    @InjectView(R.id.stock_details_header) ViewGroup stockDetailHeader;

    @InjectView(R.id.chart_frame) protected RelativeLayout mInfoFrame;
    @InjectView(R.id.trade_bottom_pager) protected ViewPager mBottomViewPager;

    @InjectView(R.id.tv_stock_roi) protected TextView tvStockRoi;

    @Inject UserWatchlistPositionCacheRx userWatchlistPositionCache;
    @Inject AlertCompactListCacheRx alertCompactListCache;
    @Inject Analytics analytics;

    @RouteProperty("exchange") String exchange;
    @RouteProperty("symbol") String symbol;

    @Nullable protected WatchlistPositionDTOList watchedList;

    private BuySellBottomStockPagerAdapter bottomViewPagerAdapter;
    @Nullable private Map<SecurityId, AlertCompactDTO> mappedAlerts;

    protected StockDetailActionBarRelativeLayout actionBarLayout;

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
        analytics.fireEvent(new ChartTimeEvent(requisite.securityId, BuySellBottomStockPagerAdapter.getDefaultChartTimeSpan()));

        onStopSubscriptions.add(Observable.combineLatest(
                getSecurityObservable().observeOn(AndroidSchedulers.mainThread()),
                getQuoteObservable().observeOn(AndroidSchedulers.mainThread()),
                new Func2<SecurityCompactDTO, QuoteDTO, Boolean>()
                {
                    @Override public Boolean call(@NonNull SecurityCompactDTO securityCompactDTO, @NonNull QuoteDTO quoteDTO)
                    {
                        displayAsOf(securityCompactDTO, quoteDTO);
                        return true;
                    }
                })
                .subscribe(
                        new Action1<Boolean>()
                        {
                            @Override public void call(Boolean aBoolean)
                            {
                                // Nothing to do
                            }
                        },
                        new TimberOnErrorAction("Failed to update AsOf")));
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        final StockDetailActionBarRelativeLayout actionBarLayout =
                (StockDetailActionBarRelativeLayout) LayoutInflater.from(actionBar.getThemedContext())
                        .inflate(R.layout.stock_detail_custom_actionbar, null);
        this.actionBarLayout = actionBarLayout;
        onDestroyOptionsMenuSubscriptions.add(getQuoteObservable().observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<QuoteDTO, Observable<Boolean>>()
                {
                    @Override public Observable<Boolean> call(@NonNull QuoteDTO quoteDTO)
                    {
                        return actionBarLayout.circleProgressBar.start(getMillisecondQuoteRefresh());
                    }
                })
                .subscribe(
                        new Action1<Boolean>()
                        {
                            @Override public void call(Boolean aBoolean)
                            {
                                // Nothing to do
                            }
                        },
                        new TimberOnErrorAction("Failed to listen to end of animation")));

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

        onDestroyOptionsMenuSubscriptions.add(Observable.combineLatest(
                getSecurityObservable().startWith(securityCompactDTO).observeOn(AndroidSchedulers.mainThread()),
                getWatchlistObservable().startWith(watchedList).observeOn(AndroidSchedulers.mainThread()),
                getAlertsObservable().startWith(mappedAlerts).observeOn(AndroidSchedulers.mainThread()),
                new Func3<SecurityCompactDTO, WatchlistPositionDTOList, Map<SecurityId, AlertCompactDTO>, Boolean>()
                {
                    @Override public Boolean call(
                            @Nullable SecurityCompactDTO securityCompactDTO,
                            @Nullable WatchlistPositionDTOList watchlistPositionDTOs,
                            @Nullable Map<SecurityId, AlertCompactDTO> alertMap)
                    {
                        actionBarLayout.display(new StockDetailActionBarRelativeLayout.Requisite(
                                requisite.securityId,
                                securityCompactDTO,
                                watchlistPositionDTOs,
                                alertMap));
                        return true;
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Boolean>()
                        {
                            @Override public void call(Boolean displayedActionBar)
                            {
                                // Nothing to do
                            }
                        },
                        new TimberOnErrorAction("Failed to fetch list of watch list items")));

        actionBar.setCustomView(actionBarLayout);
    }

    @Override public void onLiveTradingChanged(boolean isLive)
    {
        super.onLiveTradingChanged(isLive);
        LiveFragmentUtil.setDarkBackgroundColor(isLive, mSlidingTabLayout);
        LiveFragmentUtil.setBackgroundColor(isLive, stockDetailHeader);
        LiveFragmentUtil.setSelectableBackground(isLive, buyBtn, sellBtn);
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

    @NonNull @Override protected Observable<PortfolioCompactDTO> getDefaultPortfolio()
    {
        return portfolioCompactListCache.get(currentUserId.toUserBaseKey())
                .map(new Func1<Pair<UserBaseKey, PortfolioCompactDTOList>, PortfolioCompactDTO>()
                {
                    @Override public PortfolioCompactDTO call(Pair<UserBaseKey, PortfolioCompactDTOList> userBaseKeyPortfolioCompactDTOListPair)
                    {
                        PortfolioCompactDTO found = userBaseKeyPortfolioCompactDTOListPair.second.getDefaultPortfolio();
                        defaultPortfolio = found;
                        return found;
                    }
                })
                .share();
    }

    @Override protected void linkWith(@NonNull PortfolioCompactDTOList portfolioCompactDTOs)
    {
        // Nothing to do
    }

    @NonNull @Override protected Requisite createRequisite()
    {
        if (exchange != null && symbol != null)
        {
            return new Requisite(new SecurityId(exchange, symbol), getArguments());
        }
        return super.createRequisite();
    }

    @NonNull protected Observable<Map<SecurityId, AlertCompactDTO>> getAlertsObservable()
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

    @NonNull protected Observable<WatchlistPositionDTOList> getWatchlistObservable()
    {
        return userWatchlistPositionCache.getOne(currentUserId.toUserBaseKey())
                .map(new Func1<Pair<UserBaseKey, WatchlistPositionDTOList>, WatchlistPositionDTOList>()
                {
                    @Override
                    public WatchlistPositionDTOList call(@NonNull Pair<UserBaseKey, WatchlistPositionDTOList> pair)
                    {
                        watchedList = pair.second;
                        return pair.second;
                    }
                })
                .share();
    }

    @Override protected void linkWith(@NonNull OwnedPortfolioId purchaseApplicablePortfolioId)
    {
        if (bottomViewPagerAdapter == null)
        {
            bottomViewPagerAdapter = new BuySellBottomStockPagerAdapter(
                    getActivity(),
                    this.getChildFragmentManager(),
                    purchaseApplicablePortfolioId,
                    requisite.securityId,
                    currentUserId.toUserBaseKey());
            mBottomViewPager.setAdapter(bottomViewPagerAdapter);
            mSlidingTabLayout.setViewPager(mBottomViewPager);
        }
    }

    @Override public void displayBuySellPrice(@NonNull SecurityCompactDTO securityCompactDTO, @NonNull QuoteDTO quoteDTO)
    {
        if (mBuyPrice != null)
        {
            String display = securityCompactDTO.currencyDisplay;
            String bPrice;
            String sPrice;
            THSignedNumber bthSignedNumber;
            THSignedNumber sthSignedNumber;
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
            String buyPriceText = getString(R.string.buy_sell_button_buy, display, bPrice);
            String sellPriceText = getString(R.string.buy_sell_button_sell, display, sPrice);
            mBuyPrice.setText(buyPriceText);
            mSellPrice.setText(sellPriceText);
        }

        displayStockRoi();
    }

    public void displayAsOf(@NonNull SecurityCompactDTO securityCompactDTO, @NonNull QuoteDTO quoteDTO)
    {
        if (mVPriceAsOf != null)
        {
            String text;
            if (quoteDTO.asOfUtc != null)
            {
                text = DateUtils.getFormattedDate(getResources(), quoteDTO.asOfUtc);
            }
            else if (securityCompactDTO.lastPriceDateAndTimeUtc != null)
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

    @NonNull @Override protected Observable<Boolean> getSupportSell()
    {
        return Observable.combineLatest(
                getPortfolioCompactObservable(),
                getQuoteObservable(),
                getCloseablePositionObservable(),
                new Func3<PortfolioCompactDTO, QuoteDTO, PositionDTO, Boolean>()
                {
                    @Override public Boolean call(@NonNull PortfolioCompactDTO portfolioCompactDTO,
                            @NonNull QuoteDTO quoteDTO,
                            @Nullable PositionDTO closeablePositionDTO)
                    {
                        Integer max = getMaxSellableShares(portfolioCompactDTO, quoteDTO, closeablePositionDTO);
                        return max != null && max > 0;
                    }
                }
        );
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
        boolean isTransactionTypeBuy;
        switch (view.getId())
        {
            case R.id.btn_buy:
                isTransactionTypeBuy = true;
                break;
            case R.id.btn_sell:
                isTransactionTypeBuy = false;
                break;
            default:
                throw new IllegalArgumentException("Unhandled button " + view.getId());
        }
        analytics.fireEvent(new BuySellEvent(isTransactionTypeBuy, requisite.securityId));
        super.handleBuySellButtonsClicked(view);
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
