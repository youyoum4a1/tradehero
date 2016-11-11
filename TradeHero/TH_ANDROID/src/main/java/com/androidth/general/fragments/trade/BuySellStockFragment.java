package com.androidth.general.fragments.trade;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.common.SlidingTabLayout;
import com.android.internal.util.Predicate;
import com.androidth.general.R;
import com.androidth.general.api.alert.AlertCompactDTO;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import com.androidth.general.api.portfolio.PortfolioCompactDTO;
import com.androidth.general.api.portfolio.PortfolioCompactDTOList;
import com.androidth.general.api.position.PositionDTO;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.api.security.SecurityId;
import com.androidth.general.api.security.compact.FxSecurityCompactDTO;
import com.androidth.general.api.security.key.FxPairSecurityId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.watchlist.WatchlistPositionDTO;
import com.androidth.general.api.watchlist.WatchlistPositionDTOList;
import com.androidth.general.fragments.alert.AlertCreateDialogFragment;
import com.androidth.general.fragments.alert.AlertEditDialogFragment;
import com.androidth.general.fragments.alert.BaseAlertEditDialogFragment;
import com.androidth.general.fragments.base.ActionBarOwnerMixin;
import com.androidth.general.fragments.security.BuySellBottomStockPagerAdapter;
import com.androidth.general.fragments.security.LiveQuoteDTO;
import com.androidth.general.fragments.security.WatchlistEditFragment;
import com.androidth.general.models.number.THSignedNumber;
import com.androidth.general.models.number.THSignedPercentage;
import com.androidth.general.persistence.alert.AlertCompactListCacheRx;
import com.androidth.general.persistence.live.Live1BResponseDTO;
import com.androidth.general.persistence.watchlist.UserWatchlistPositionCacheRx;
import com.androidth.general.rx.TimberOnErrorAction1;
import com.androidth.general.utils.StringUtils;
import com.androidth.general.utils.broadcast.GAnalyticsProvider;
import com.squareup.picasso.Picasso;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;

import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

@Routable({
        "stock-security/:exchange/:symbol"
})
public class  BuySellStockFragment extends AbstractBuySellFragment {
    @Bind(R.id.tabs) protected SlidingTabLayout mSlidingTabLayout;
    @Bind(R.id.stock_details_header) ViewGroup stockDetailHeader;

    @Bind(R.id.chart_frame) protected RelativeLayout mInfoFrame;
    @Bind(R.id.trade_bottom_pager) protected ViewPager mBottomViewPager;

    @Bind(R.id.stock_name) TextView stockName;
    @Bind(R.id.exchange_name) TextView exchangeName;
    @Bind(R.id.buy_price) TextView buyPrice;
    @Bind(R.id.sell_price) TextView sellPrice;
    @Bind(R.id.last_price) TextView lastPrice;

    @Inject UserWatchlistPositionCacheRx userWatchlistPositionCache;
    @Inject AlertCompactListCacheRx alertCompactListCache;

    @Bind(R.id.stock_image) ImageView stockImage;
    @Bind(R.id.tv_stock_roi) TextView stockRoi;

    private static final float ALPHA_INACTIVE = 0.5f;
    private static final float ALPHA_ACTIVE = 1f;
    protected SecurityId securityId;
    //TODO Change Analytics
    //@Inject Analytics analytics;

    @RouteProperty("exchange") String exchange;
    @RouteProperty("symbol") String symbol;

    @Bind(R.id.btn_watched) @Nullable protected ImageView btnWatched;
    @Bind(R.id.btn_alerted) @Nullable protected View btnAlerted;


    @Nullable private Map<SecurityId, AlertCompactDTO> mappedAlerts;
    @Nullable protected WatchlistPositionDTOList watchedList;




    @ColorRes private static final int COLOR_RES_UNWATCHED = R.color.darker_grey;
    @ColorRes private static final int COLOR_RES_WATCHED = R.color.watchlist_button_color;

    String actionbarText;

    private BuySellBottomStockPagerAdapter bottomViewPagerAdapter;


    //protected StockDetailActionBarRelativeLayout actionBarLayout;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_stock_buy_sell, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mSlidingTabLayout.setCustomTabView(R.layout.th_page_indicator, android.R.id.title);
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.general_tab_indicator_color));

    }


    @SuppressWarnings("unused")
    @Nullable @OnClick(R.id.btn_watched)
    protected void onButtonWatchedClicked(View view)
    {
        if(btnWatched!=null && btnWatched.getAlpha()==ALPHA_INACTIVE){
            //Google Analytics
            GAnalyticsProvider.sendGAActionEvent("Global", GAnalyticsProvider.ACTION_ADD_TO_FAVE);
        }

        if (watchedList != null && securityId!=null)
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

    @SuppressWarnings("unused")
    @Nullable @OnClick(R.id.btn_alerted)
    protected void onButtonAlertedClicked(View view)
    {
        if(btnAlerted!=null && btnAlerted.getAlpha()==ALPHA_INACTIVE){
            //Google Analytics
            GAnalyticsProvider.sendGAActionEvent("Global", GAnalyticsProvider.ACTION_ADD_ALERT);
        }

        if (mappedAlerts != null && securityId!=null)
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
                    getResources().getColor(watched ? COLOR_RES_WATCHED
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

    @Override public void onStart()
    {
        super.onStart();
        onDestroyViewSubscriptions.add(
                securityObservable.startWith(securityCompactDTO)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<SecurityCompactDTO>()
                        {
                            @Override public void call(SecurityCompactDTO securityCompactDTO)
                            {
                                //actionBarLayout.display9);
                                StockDetailActionBarRelativeLayout.Requisite dto = new StockDetailActionBarRelativeLayout.Requisite(
                                        requisite.securityId,
                                        securityCompactDTO,
                                        null, null);
                                BuySellStockFragment.this.securityId = requisite.securityId;
                                Subscription checkAlertAndWatchList = Observable.combineLatest(createAlertsObservable(), createWatchlistObservable(),
                                        (securityIdAlertCompactDTOMap, watchlistPositionDTOs) -> android.support.v4.util.Pair.create(securityIdAlertCompactDTOMap, watchlistPositionDTOs))
                                        .subscribe(
                                                pair -> {
                                                    BuySellStockFragment.this.stockIsWatched(pair.second);
                                                    BuySellStockFragment.this.stockIsOnAlert(pair.first);
                                                },
                                                new TimberOnErrorAction1("Failed to listen to alerts and watchlist on chart fragment"));
                                onDestroySubscriptions.add(checkAlertAndWatchList);
                                if (dto.securityCompactDTO != null)
                                {
                                    FxPairSecurityId fxPairSecurityId = null;
                                    if (dto.securityCompactDTO instanceof FxSecurityCompactDTO)
                                    {
                                        fxPairSecurityId = ((FxSecurityCompactDTO) dto.securityCompactDTO).getFxPair();
                                    }

                                    if (fxPairSecurityId != null)
                                    {
                                        actionbarText = String.format("%s/%s", fxPairSecurityId.left, fxPairSecurityId.right);
                                        //stockSubTitle.setText(null);
                                    }
                                    else
                                    {
                                        String actionTitle;
                                        if (!StringUtils.isNullOrEmpty(dto.securityCompactDTO.name))
                                        {
                                            actionTitle = dto.securityCompactDTO.getExchangeSymbol();

                                        }
                                        else
                                        {
                                            actionTitle = (dto.securityCompactDTO.getExchangeSymbol());

                                        }
                                        actionbarText = actionTitle;
                                        getSupportActionBar().setTitle(actionbarText);
                                    }

                                    SecurityCompactDTO secDto = dto.securityCompactDTO;
                                    Picasso.with(getContext()).load(secDto.imageBlobUrl).into(stockImage);
                                    stockName.setText(secDto.name);
                                    exchangeName.setText(secDto.getExchangeSymbol());
                                }
                            }
                        }, new TimberOnErrorAction1("Failed to fetch list of watch list items")));
        //analytics.fireEvent(new ChartTimeEvent(requisite.securityId, BuySellBottomStockPagerAdapter.getDefaultChartTimeSpan()));

        onStopSubscriptions.add(
                Live1BResponseDTO.getLiveQuoteObservable()
                        .distinctUntilChanged()
                        .doOnNext(new Action1<LiveQuoteDTO>() {
                            @Override
                            public void call(LiveQuoteDTO liveQuoteDTO) {
                                // update live prices
                                if(!liveQuoteDTO.n.toLowerCase().contains("outright")) {
                                    Log.v("LiveQuoteObservable", "displaying updated live price... " + liveQuoteDTO);
                                    displayBuySellPrice(liveQuoteDTO.getAskPrice(), liveQuoteDTO.getBidPrice());
                                }
                            }
                        }).subscribe());
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        actionBar.setTitle(actionbarText);

        /*final StockDetailActionBarRelativeLayout actionBarLayout =
                (StockDetailActionBarRelativeLayout) LayoutInflater.from(actionBar.getThemedContext())
                        .inflate(R.layout.stock_detail_custom_actionbar, null);
        this.actionBarLayout = actionBarLayout;*/

        /*onDestroyOptionsMenuSubscriptions.add(quoteObservable.observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<QuoteDTO, Observable<Boolean>>()
                {
                    @Override public Observable<Boolean> call(@NonNull QuoteDTO quoteDTO)
                    {

                        return Observable.just(true).delay(getMillisecondQuoteRefresh(), TimeUnit.MILLISECONDS);
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
                        new TimberOnErrorAction1("Failed to listen to end of animation")));

        //actionBar.setCustomView(actionBarLayout);*/
    }

    @Override public void onDestroyOptionsMenu()
    {
        //actionBarLayout = null;
        super.onDestroyOptionsMenu();
    }

    @Override public void onDestroyView()
    {
        bottomViewPagerAdapter = null;
        defaultPortfolio = null;
        super.onDestroyView();
    }
    @Override public void onDestroy(){
        watchedList = null;
        mappedAlerts = null;
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (bottomViewPagerAdapter != null)
        {
            bottomViewPagerAdapter.onActivityResult(requestCode, resultCode, data);
        }
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
            return new Requisite(new SecurityId(exchange, symbol, portfolioCompactDTO.userId), getArguments(), portfolioCompactListCache, currentUserId);
        }
        return super.createRequisite();
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

//    read http://stackoverflow.com/questions/21342700/proguard-causing-runtimeexception-unmarshalling-unknown-type-code-in-parcelabl

    @NonNull @Override protected Observable<Boolean> getSupportSell()
    {
        return getPortfolioCompactObservable()
                .flatMap(new Func1<PortfolioCompactDTO, Observable<Boolean>>()
                {
                    @Override public Observable<Boolean> call(final PortfolioCompactDTO portfolioCompactDTO)
                    {
                        return Observable.combineLatest(
                                quoteObservable,
                                getCloseablePositionObservable()
                                        .filter(new Func1<PositionDTO, Boolean>()
                                        {
                                            @Override public Boolean call(PositionDTO positionDTO)
                                            {
                                                return positionDTO == null || positionDTO.portfolioId.equals(portfolioCompactDTO.id);
                                            }
                                        }),
                                new Func2<LiveQuoteDTO, PositionDTO, Boolean>()
                                {
                                    @Override public Boolean call(
                                            @NonNull LiveQuoteDTO quoteDTO,
                                            @Nullable PositionDTO closeablePositionDTO)
                                    {
                                        Integer max = getMaxSellableShares(portfolioCompactDTO, quoteDTO, closeablePositionDTO);
                                        return max != null && max > 0;
                                    }
                                });
                    }
                });
    }

    public void displayBuySellPrice(@Nullable Double ask, @Nullable Double bid)
    {
        if (buyPrice != null && sellPrice != null && lastPrice != null)
        {
            String display = securityCompactDTO.currencyDisplay;
            String bPrice;
            String sPrice;
            String lPrice;
            THSignedNumber thSignedNumber;
//            THSignedNumber sthSignedNumber;
            if (ask == null)
            {
                bPrice = getString(R.string.buy_sell_ask_price_not_available);
            }
            else
            {
                thSignedNumber = THSignedNumber.builder(ask)
                        .withOutSign()
                        .build();
                bPrice = thSignedNumber.toString();
            }

            if (bid == null)
            {
                sPrice = getString(R.string.buy_sell_bid_price_not_available);
            }
            else
            {
                thSignedNumber = THSignedNumber.builder(bid)
                        .withOutSign()
                        .build();
                sPrice = thSignedNumber.toString();
            }

            if(securityCompactDTO.lastPrice!=null){
                thSignedNumber = THSignedNumber.builder(securityCompactDTO.lastPrice)
                        .withOutSign()
                        .build();
                lPrice = thSignedNumber.toString();
            }else{
                lPrice = "--";
            }

            String buyPriceText = getString(R.string.buy_sell_button_buy, display, bPrice);
            String sellPriceText = getString(R.string.buy_sell_button_sell, display, sPrice);
            String lastPriceText = getString(R.string.buy_sell_button_last_price, display, lPrice);

            buyPrice.setText(buyPriceText);
            sellPrice.setText(sellPriceText);
            lastPrice.setText(lastPriceText);
        }
    }

    public void displayBuySellPrice(@NonNull SecurityCompactDTO securityCompactDTO, @Nullable Double ask, @Nullable Double bid)
    {
        displayBuySellPrice(ask, bid);

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
        //TODO Change Analytics
        //analytics.fireEvent(new BuySellEvent(isTransactionTypeBuy, requisite.securityId));
        super.handleBuySellButtonsClicked(view);
    }
    @Override public void onResume(){
        super.onResume();
    }
}
