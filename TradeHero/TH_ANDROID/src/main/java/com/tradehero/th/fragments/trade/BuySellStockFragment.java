package com.tradehero.th.fragments.trade;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.InjectView;
import com.android.common.SlidingTabLayout;
import com.squareup.picasso.Picasso;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.CircleProgressBar;
import com.tradehero.metrics.Analytics;
import com.tradehero.route.Routable;
import com.tradehero.th.R;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.compact.WarrantDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import com.tradehero.th.fragments.alert.AlertCreateFragment;
import com.tradehero.th.fragments.alert.AlertEditFragment;
import com.tradehero.th.fragments.security.BuySellBottomStockPagerAdapter;
import com.tradehero.th.fragments.security.WatchlistEditFragment;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.models.portfolio.MenuOwnedPortfolioId;
import com.tradehero.th.persistence.alert.AlertCompactListCacheRx;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCacheRx;
import com.tradehero.th.utils.DateUtils;
import com.tradehero.th.utils.StringUtils;
import com.tradehero.th.utils.metrics.events.BuySellEvent;
import com.tradehero.th.utils.metrics.events.ChartTimeEvent;
import java.util.Map;
import javax.inject.Inject;
import rx.Observer;
import rx.android.app.AppObservable;
import timber.log.Timber;

@Routable("security/:securityRawInfo")
public class BuySellStockFragment extends BuySellFragment
{
    @InjectView(R.id.buy_price) protected TextView mBuyPrice;
    @InjectView(R.id.sell_price) protected TextView mSellPrice;
    @InjectView(R.id.vprice_as_of) protected TextView mVPriceAsOf;
    @InjectView(R.id.tabs) protected SlidingTabLayout mSlidingTabLayout;

    @InjectView(R.id.chart_frame) protected RelativeLayout mInfoFrame;
    @InjectView(R.id.trade_bottom_pager) protected ViewPager mBottomViewPager;

    @Inject UserWatchlistPositionCacheRx userWatchlistPositionCache;
    @Inject AlertCompactListCacheRx alertCompactListCache;

    private PortfolioCompactDTO defaultPortfolio;

    @Nullable protected WatchlistPositionDTOList watchedList;

    private BuySellBottomStockPagerAdapter bottomViewPagerAdapter;
    @Nullable private Map<SecurityId, AlertId> mappedAlerts;

    @Inject Analytics analytics;

    protected TextView mTvStockTitle;
    protected TextView mTvStockSubTitle;
    protected CircleProgressBar circleProgressBar;
    protected Button btnWatched;
    protected Button btnAlerted;

    @InjectView(R.id.tv_stock_roi) protected TextView tvStockRoi;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_stock_buy_sell, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        bottomViewPagerAdapter =
                new BuySellBottomStockPagerAdapter(getActivity(), this.getChildFragmentManager());
        bottomViewPagerAdapter.linkWith(securityId);

        mBottomViewPager.setAdapter(bottomViewPagerAdapter);

        mSlidingTabLayout.setCustomTabView(R.layout.th_tab_indicator, android.R.id.title);
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.tradehero_tab_indicator_color));
        mSlidingTabLayout.setViewPager(mBottomViewPager);

        fetchAlertCompactList();
    }

    @Override public void onStart()
    {
        super.onStart();
        analytics.fireEvent(new ChartTimeEvent(securityId, BuySellBottomStockPagerAdapter.getDefaultChartTimeSpan()));
        fetchWatchlist();
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setCustomActionBar();
        initStockProgressbar();
    }

    public void setCustomActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.stock_detail_custom_actionbar, null);
        mTvStockTitle = (TextView) v.findViewById(R.id.tvStockTitle);
        mTvStockSubTitle = (TextView) v.findViewById(R.id.tvStockSubTitle);
        circleProgressBar = (CircleProgressBar) v.findViewById(R.id.circleProgressbar);
        btnWatched = (Button) v.findViewById(R.id.btnWatched);
        btnAlerted = (Button) v.findViewById(R.id.btnAlerted);

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
    }

    private void initStockProgressbar()
    {
        progressAnimation = new Animation()
        {
            @Override protected void applyTransformation(float interpolatedTime, android.view.animation.Transformation t)
            {
                super.applyTransformation(interpolatedTime, t);
                circleProgressBar.setProgress((int) (getMillisecondQuoteRefresh() * (/*1 -*/ interpolatedTime)));
            }
        };
        progressAnimation.setDuration(getMillisecondQuoteRefresh());
        circleProgressBar.setMaxProgress((int) getMillisecondQuoteRefresh());
        circleProgressBar.setProgress((int) getMillisecondQuoteRefresh());
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
                .subscribe(new Observer<Map<SecurityId, AlertId>>()
                {
                    @Override public void onCompleted()
                    {
                    }

                    @Override public void onError(Throwable e)
                    {
                        Timber.e(e, "There was an error getting the alert ids");
                        displayTriggerButton();
                    }

                    @Override public void onNext(Map<SecurityId, AlertId> securityIdAlertIdMap)
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
                .subscribe(createUserWatchlistCacheObserver()));
    }

    @NonNull protected Observer<Pair<UserBaseKey, WatchlistPositionDTOList>> createUserWatchlistCacheObserver()
    {
        return new BuySellUserWatchlistCacheObserver();
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

    protected class BuySellUserWatchlistCacheObserver
            implements Observer<Pair<UserBaseKey, WatchlistPositionDTOList>>
    {
        @Override public void onNext(Pair<UserBaseKey, WatchlistPositionDTOList> pair)
        {
            linkWith(pair.second);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            Timber.e(e, "Failed to fetch list of watch list items");
            THToast.show(R.string.error_fetch_portfolio_list_info);
        }
    }

    protected void linkWith(WatchlistPositionDTOList watchedList)
    {
        this.watchedList = watchedList;
        displayWatchlistButton();
    }

    @Override public void linkWith(SecurityCompactDTO securityCompactDTO, boolean andDisplay)
    {
        super.linkWith(securityCompactDTO, andDisplay);
        addDefaultMainPortfolioIfShould();
        if (andDisplay)
        {
            //loadStockLogo();
            displayAsOf();
        }

        if (securityCompactDTO != null)
        {
            if (!StringUtils.isNullOrEmpty(securityCompactDTO.name))
            {
                mTvStockTitle.setText(securityCompactDTO.name);
                mTvStockSubTitle.setText(securityCompactDTO.getExchangeSymbol());
            }
            else
            {
                mTvStockTitle.setText(securityCompactDTO.getExchangeSymbol());
                mTvStockSubTitle.setText(null);
            }

            Picasso.with(getActivity()).load(securityCompactDTO.imageBlobUrl).resize((int) (circleProgressBar.getWidth() * 0.60),
                    (int) (circleProgressBar.getHeight() * 0.60)).centerInside().into(mTarget);
        }
    }

    @Override protected void linkWith(QuoteDTO quoteDTO)
    {
        super.linkWith(quoteDTO);
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
        return quoteDTO != null && positionDTOCompactList != null && applicableOwnedPortfolioIds != null;
    }

    public void displayTriggerButton()
    {
        if (btnAlerted != null)
        {
            btnAlerted.setVisibility(mappedAlerts != null ? View.VISIBLE : View.GONE);
            btnAlerted.setAlpha(mappedAlerts.get(securityId) != null ? 1.0f : 0.50f);
        }
    }

    public void displayWatchlistButton()
    {
        if (btnWatched != null)
        {
            if (securityId == null || watchedList == null)
            {
                // TODO show disabled
                btnWatched.setVisibility(View.GONE);
            }
            else
            {
                btnWatched.setVisibility(View.VISIBLE);
                btnWatched.setAlpha(watchedList.contains(securityId) ?
                        1.0f :
                        0.50f);
            }
        }
    }

    @Override protected boolean getSupportSell()
    {
        boolean supportSell;
        if (positionDTOCompactList == null
                || positionDTOCompactList.size() == 0
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
            Bundle args = new Bundle();
            AlertId alertId = mappedAlerts.get(securityId);
            if (alertId != null)
            {
                AlertEditFragment.putAlertId(args, alertId);
                navigator.get().pushFragment(AlertEditFragment.class, args);
            }
            else
            {
                AlertCreateFragment.putSecurityId(args, securityId);
                navigator.get().pushFragment(AlertCreateFragment.class, args);
            }
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

    private com.squareup.picasso.Target mTarget = new com.squareup.picasso.Target()
    {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
        {
            // Do whatever you want with the Bitmap
            if (circleProgressBar != null)
            {
                circleProgressBar.setBitmapBg(bitmap);
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable)
        {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable)
        {
        }
    };

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
