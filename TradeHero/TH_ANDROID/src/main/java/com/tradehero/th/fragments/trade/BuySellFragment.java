package com.tradehero.th.fragments.trade;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.special.residemenu.ResideMenu;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.route.Routable;
import com.tradehero.th.BottomTabs;
import com.tradehero.th.R;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderDTOList;
import com.tradehero.th.api.market.Exchange;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.position.PositionDTOCompactList;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.position.SecurityPositionTransactionDTO;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.share.wechat.WeChatDTO;
import com.tradehero.th.api.share.wechat.WeChatMessageType;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import com.tradehero.th.fragments.DashboardTabHost;
import com.tradehero.th.fragments.alert.AlertCreateFragment;
import com.tradehero.th.fragments.alert.AlertEditFragment;
import com.tradehero.th.fragments.alert.BaseAlertEditFragment;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.security.BuySellBottomStockPagerAdapter;
import com.tradehero.th.fragments.security.StockInfoFragment;
import com.tradehero.th.fragments.security.WatchlistEditFragment;
import com.tradehero.th.fragments.settings.AskForInviteDialogFragment;
import com.tradehero.th.fragments.settings.SendLoveBroadcastSignal;
import com.tradehero.th.fragments.trade.view.PortfolioSelectorView;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.graphics.ForSecurityItemBackground;
import com.tradehero.th.models.graphics.ForSecurityItemForeground;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.portfolio.MenuOwnedPortfolioId;
import com.tradehero.th.models.share.preference.SocialSharePreferenceHelperNew;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.share.SocialSharer;
import com.tradehero.th.persistence.alert.AlertCompactListCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;
import com.tradehero.th.persistence.prefs.ShowAskForInviteDialog;
import com.tradehero.th.persistence.prefs.ShowAskForReviewDialog;
import com.tradehero.th.persistence.timing.TimingIntervalPreference;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.DateUtils;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.broadcast.BroadcastUtils;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.events.BuySellEvent;
import com.tradehero.th.utils.metrics.events.ChartTimeEvent;
import dagger.Lazy;
import java.util.Map;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.observers.EmptyObserver;
import timber.log.Timber;

@Routable("security/:securityRawInfo")
public class BuySellFragment extends AbstractBuySellFragment
        implements ViewPager.OnPageChangeListener,
        WithTutorial
{
    public static final String EVENT_CHART_IMAGE_CLICKED = BuySellFragment.class.getName() + ".chartButtonClicked";
    private static final String BUNDLE_KEY_SELECTED_PAGE_INDEX = ".selectedPage";

    public static final int MS_DELAY_FOR_BG_IMAGE = 200;

    private static final boolean DEFAULT_IS_SHARED_TO_WECHAT = false;

    @InjectView(R.id.stock_bg_logo) protected ImageView mStockBgLogo;
    @InjectView(R.id.stock_logo) protected ImageView mStockLogo;
    @InjectView(R.id.portfolio_selector_container) PortfolioSelectorView mSelectedPortfolioContainer;
    @Nullable Subscription portfolioMenuSubscription;
    @InjectView(R.id.market_closed_icon) protected ImageView mMarketClosedIcon;
    @InjectView(R.id.buy_price) protected TextView mBuyPrice;
    @InjectView(R.id.sell_price) protected TextView mSellPrice;
    @InjectView(R.id.vprice_as_of) protected TextView mVpriceAsOf;
    @InjectView(R.id.info) protected TextView mInfoTextView;
    @InjectView(R.id.discussions) protected TextView mDiscussTextView;
    @InjectView(R.id.news) protected TextView mNewsTextView;

    @Inject ResideMenu resideMenu;
    @Inject @ShowAskForReviewDialog TimingIntervalPreference mShowAskForReviewDialogPreference;
    @Inject @ShowAskForInviteDialog TimingIntervalPreference mShowAskForInviteDialogPreference;
    @Inject BroadcastUtils broadcastUtils;

    @InjectView(R.id.quote_refresh_countdown) protected ProgressBar mQuoteRefreshProgressBar;
    @InjectView(R.id.chart_frame) protected RelativeLayout mInfoFrame;
    @InjectView(R.id.trade_bottom_pager) protected ViewPager mBottomViewPager;

    @InjectView(R.id.bottom_button) protected ViewGroup mBuySellBtnContainer;
    @InjectView(R.id.btn_buy) protected Button mBuyBtn;
    @InjectView(R.id.btn_sell) protected Button mSellBtn;
    @InjectView(R.id.btn_add_trigger) protected Button mBtnAddTrigger;
    @InjectView(R.id.btn_add_watch_list) protected Button mBtnAddWatchlist;

    @Inject PortfolioCacheRx portfolioCache;
    @Inject ProgressDialogUtil progressDialogUtil;

    @Inject UserWatchlistPositionCache userWatchlistPositionCache;
    @Inject AlertCompactListCacheRx alertCompactListCache;
    @Inject Picasso picasso;
    @Inject Lazy<SocialSharer> socialSharerLazy;
    @Inject @ForSecurityItemForeground protected Transformation foregroundTransformation;
    @Inject @ForSecurityItemBackground protected Transformation backgroundTransformation;

    @Inject AlertDialogUtil alertDialogUtil;
    @Inject SocialSharePreferenceHelperNew socialSharePreferenceHelperNew;

    protected DTOCacheNew.Listener<UserBaseKey, WatchlistPositionDTOList> userWatchlistPositionCacheFetchListener;

    private Bundle desiredArguments;

    protected WatchlistPositionDTOList watchedList;

    private BuySellBottomStockPagerAdapter bottomViewPagerAdapter;
    private int selectedPageIndex;
    private MiddleCallback<SecurityPositionDetailDTO> buySellMiddleCallback;
    private BroadcastReceiver chartImageButtonClickReceiver;

    @Nullable private Map<SecurityId, AlertId> mappedAlerts;

    @Inject Analytics analytics;
    private AbstractTransactionDialogFragment abstractTransactionDialogFragment;
    @Inject @BottomTabs Lazy<DashboardTabHost> dashboardTabHost;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        chartImageButtonClickReceiver = createImageButtonClickBroadcastReceiver();
        userWatchlistPositionCacheFetchListener = createUserWatchlistCacheListener();
    }

    @Override protected Observer<Pair<UserBaseKey, PortfolioCompactDTOList>> createPortfolioCompactListObserver()
    {
        return new BuySellPortfolioCompactListObserver();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        if (desiredArguments == null)
        {
            desiredArguments = getArguments();
        }
        if (savedInstanceState != null)
        {
            selectedPageIndex = savedInstanceState.getInt(BUNDLE_KEY_SELECTED_PAGE_INDEX);
        }

        View view = inflater.inflate(R.layout.fragment_buy_sell, container, false);
        initViews(view);
        resideMenu.addIgnoredView(mBottomViewPager);
        return view;
    }

    @Override protected void initViews(View view)
    {
        super.initViews(view);
        ButterKnife.inject(this, view);

        if (mQuoteRefreshProgressBar != null)
        {
            mQuoteRefreshProgressBar.setMax(
                    (int) (MILLISEC_QUOTE_REFRESH / MILLISEC_QUOTE_COUNTDOWN_PRECISION));
            mQuoteRefreshProgressBar.setProgress(mQuoteRefreshProgressBar.getMax());
        }

        if (bottomViewPagerAdapter == null)
        {
            bottomViewPagerAdapter =
                    new BuySellBottomStockPagerAdapter(((Fragment) this).getChildFragmentManager());
        }
        if (mBottomViewPager != null)
        {
            mBottomViewPager.setAdapter(bottomViewPagerAdapter);
            mBottomViewPager.setOnPageChangeListener(this);
        }

        mSelectedPortfolioContainer.setDefaultPortfolioId(getApplicablePortfolioId(getArguments()));

        selectPage(selectedPageIndex);

        mBuySellBtnContainer.setVisibility(View.GONE);
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.buy_sell_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);

        displayActionBarElements();
    }

    @Override public void onDestroyOptionsMenu()
    {
        setActionBarSubtitle(null);
        super.onDestroyOptionsMenu();
    }
    //</editor-fold>

    @Override public void onStart()
    {
        super.onStart();
        analytics.fireEvent(new ChartTimeEvent(securityId, BuySellBottomStockPagerAdapter.getDefaultChartTimeSpan()));
    }

    @Override public void onResume()
    {
        super.onResume();

        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(chartImageButtonClickReceiver,
                        new IntentFilter(EVENT_CHART_IMAGE_CLICKED));

        mBottomViewPager.setCurrentItem(selectedPageIndex);
        AndroidObservable.bindFragment(
                this,
                alertCompactListCache.getSecurityMappedAlerts(currentUserId.toUserBaseKey()))
                .subscribe(new Observer<Map<SecurityId, AlertId>>()
                {
                    @Override public void onCompleted()
                    {
                        displayTriggerButton();
                    }

                    @Override public void onError(Throwable e)
                    {
                        Timber.e(e, "There was an error getting the alert ids");
                        displayTriggerButton();
                    }

                    @Override public void onNext(Map<SecurityId, AlertId> securityIdAlertIdMap)
                    {
                        mappedAlerts = securityIdAlertIdMap;
                    }
                });

        if (abstractTransactionDialogFragment != null && abstractTransactionDialogFragment.getDialog() != null)
        {
            abstractTransactionDialogFragment.populateComment();
            abstractTransactionDialogFragment.getDialog().show();
        }

        dashboardTabHost.get().setOnTranslate(new DashboardTabHost.OnTranslateListener()
        {
            @Override public void onTranslate(float x, float y)
            {
                mBuySellBtnContainer.setTranslationY(y);
            }
        });
    }

    @Override public void onPause()
    {
        LocalBroadcastManager.getInstance(getActivity())
                .unregisterReceiver(chartImageButtonClickReceiver);
        selectedPageIndex = mBottomViewPager.getCurrentItem();
        dashboardTabHost.get().setOnTranslate(null);
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        detachWatchlistFetchTask();
        detachBuySellMiddleCallback();
        detachPortfolioMenuSubscription();
        detachBuySellDialog();

        bottomViewPagerAdapter = null;

        resideMenu.removeIgnoredView(mBottomViewPager);
        ButterKnife.reset(this);

        super.onDestroyView();
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        progressDialogUtil.dismiss(getActivity());
        detachWatchlistFetchTask();
        detachBuySellMiddleCallback();
        detachPortfolioMenuSubscription();
        detachBuySellDialog();

        outState.putInt(BUNDLE_KEY_SELECTED_PAGE_INDEX, selectedPageIndex);
    }

    @Override public void onDestroy()
    {
        userWatchlistPositionCacheFetchListener = null;
        chartImageButtonClickReceiver = null;
        super.onDestroy();
    }

    protected void detachWatchlistFetchTask()
    {
        userWatchlistPositionCache.unregister(userWatchlistPositionCacheFetchListener);
    }

    private void detachPortfolioMenuSubscription()
    {
        unsubscribe(portfolioMenuSubscription);
        portfolioMenuSubscription = null;
    }

    private void detachBuySellDialog()
    {
        AbstractTransactionDialogFragment dialogCopy = abstractTransactionDialogFragment;
        if (dialogCopy != null)
        {
            dialogCopy.setBuySellTransactionListener(null);
        }
        abstractTransactionDialogFragment = null;
    }

    @Override public void linkWith(SecurityId securityId, boolean andDisplay)
    {
        super.linkWith(securityId, andDisplay);

        fetchWatchlist();

        if (andDisplay)
        {
            displayWatchlistButton();
        }
    }

    public void fetchWatchlist()
    {
        detachWatchlistFetchTask();
        userWatchlistPositionCache.register(currentUserId.toUserBaseKey(), userWatchlistPositionCacheFetchListener);
        userWatchlistPositionCache.getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    @Override public void linkWith(SecurityCompactDTO securityCompactDTO, boolean andDisplay)
    {
        super.linkWith(securityCompactDTO, andDisplay);

        if (andDisplay)
        {
            displayStockName();
            displayBottomViewPager();
            loadStockLogo();
            displayBuySellPrice();
            displayAsOf();
        }
    }

    @Override public void linkWith(@NotNull final SecurityPositionDetailDTO securityPositionDetailDTO,
            boolean andDisplay)
    {
        super.linkWith(securityPositionDetailDTO, andDisplay);

        ProviderDTOList providerDTOs = securityPositionDetailDTO.providers;
        if (providerDTOs != null)
        {
            for (@NotNull ProviderDTO providerDTO : providerDTOs)
            {
                if (providerDTO.associatedPortfolio != null)
                {
                    mSelectedPortfolioContainer.addMenuOwnedPortfolioId(
                            new MenuOwnedPortfolioId(
                                    currentUserId.toUserBaseKey(),
                                    providerDTO.associatedPortfolio));
                }
            }
        }

        setInitialSellQuantityIfCan();

        if (andDisplay)
        {
            displayBuySellSwitch();
            displayBuySellPrice();
            displayBuySellContainer();
            conditionalDisplayPortfolioChanged();
        }
    }

    @Override
    public void linkWith(final PositionDTOCompactList positionDTOCompacts, boolean andDisplay)
    {
        super.linkWith(positionDTOCompacts, andDisplay);
        if (andDisplay)
        {

        }
    }

    @Override public void linkWith(UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        super.linkWith(userProfileDTO, andDisplay);
        setInitialBuyQuantityIfCan();
        setInitialSellQuantityIfCan();
        if (andDisplay)
        {
        }
    }

    @Override protected void linkWith(QuoteDTO quoteDTO, boolean andDisplay)
    {
        super.linkWith(quoteDTO, andDisplay);
        setInitialBuyQuantityIfCan();
        setInitialSellQuantityIfCan();
        if (andDisplay)
        {
            displayAsOf();
            displayBuySellPrice();
            displayBuySellContainer();
            displayBuySellSwitch();
        }
    }

    @Override protected void linkWithApplicable(OwnedPortfolioId purchaseApplicablePortfolioId,
            boolean andDisplay)
    {
        super.linkWithApplicable(purchaseApplicablePortfolioId, andDisplay);
        if (purchaseApplicablePortfolioId != null)
        {
            purchaseApplicableOwnedPortfolioId = purchaseApplicablePortfolioId;
            AndroidObservable.bindFragment(
                    this,
                    portfolioCache.get(purchaseApplicablePortfolioId))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Pair<OwnedPortfolioId, PortfolioDTO>>()
                    {
                        @Override public void onCompleted()
                        {
                        }

                        @Override public void onError(Throwable e)
                        {
                        }

                        @Override public void onNext(Pair<OwnedPortfolioId, PortfolioDTO> pair)
                        {
                            linkWith(pair.second, andDisplay);
                        }
                    });
        }
        else
        {
            linkWith((PortfolioCompactDTO) null, andDisplay);
        }
        if (andDisplay)
        {
            displayBuySellSwitch();
        }
    }

    @Override protected void linkWith(PortfolioCompactDTO portfolioCompactDTO, boolean andDisplay)
    {
        super.linkWith(portfolioCompactDTO, andDisplay);
        clampBuyQuantity(andDisplay);
        clampSellQuantity(andDisplay);
        if (andDisplay)
        {
            // TODO max purchasable shares
            displayBuySellPrice();
            conditionalDisplayPortfolioChanged();
        }
    }

    protected void linkWithWatchlist(WatchlistPositionDTOList watchedList, boolean andDisplay)
    {
        this.watchedList = watchedList;
        if (andDisplay)
        {
            displayWatchlistButton();
        }
    }

    protected void setInitialBuyQuantityIfCan()
    {
        if (mBuyQuantity == null)
        {
            Integer maxPurchasableShares = getMaxPurchasableShares();
            if (maxPurchasableShares != null)
            {
                linkWithBuyQuantity((int) Math.ceil(((double) maxPurchasableShares) / 2), true);
            }
        }
    }

    protected void setInitialSellQuantityIfCan()
    {
        if (mSellQuantity == null)
        {
            Integer maxSellableShares = getMaxSellableShares();
            if (maxSellableShares != null)
            {
                linkWithSellQuantity(maxSellableShares, true);
                if (maxSellableShares == 0)
                {
                    setTransactionTypeBuy(true);
                }
            }
        }
    }

    //<editor-fold desc="Display Methods"> //hide switch portfolios for temp
    public void display()
    {
        displayActionBarElements();
        displayPageElements();
    }

    public void displayPageElements()
    {
        displayBuySellPrice();
        displayBottomViewPager();
        displayStockName();
        displayTriggerButton();
        loadStockLogo();
    }

    public void displayBottomViewPager()
    {
        BuySellBottomStockPagerAdapter adapter = bottomViewPagerAdapter;
        if (adapter != null)
        {
            adapter.linkWith(securityId);
            adapter.notifyDataSetChanged();
        }
    }

    public void displayStockName()
    {
        if (securityCompactDTO != null)
        {
            setActionBarTitle(securityCompactDTO.name);
            setActionBarSubtitle(securityCompactDTO.getExchangeSymbol());
        }

        if (mMarketClosedIcon != null)
        {
            boolean marketIsOpen = securityCompactDTO == null
                    || securityCompactDTO.marketOpen == null
                    || securityCompactDTO.marketOpen;
            mMarketClosedIcon.setVisibility(marketIsOpen ? View.GONE : View.VISIBLE);
        }
    }

    public void displayBuySellPrice()
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
    }

    public void displayAsOf()
    {
        if (mVpriceAsOf != null)
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
            mVpriceAsOf.setText(
                    getResources().getString(R.string.buy_sell_price_as_of) + " " + text);
        }
    }

    public void displayActionBarElements()
    {
        displayBuySellSwitch();
    }

    public void displayBuySellContainer()
    {
        if (isBuySellReady() && mBuySellBtnContainer.getVisibility() == View.GONE)
        {
            Animation slideIn = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_from_bottom);
            slideIn.setFillAfter(true);
            mBuySellBtnContainer.setVisibility(View.VISIBLE);
            mBuySellBtnContainer.startAnimation(slideIn);
        }
    }

    public boolean isBuySellReady()
    {
        return quoteDTO != null && securityPositionDetailDTO != null;
    }

    public void conditionalDisplayPortfolioChanged()
    {
        if (securityPositionDetailDTO != null
                && portfolioCompactDTO != null
                && mSelectedPortfolioContainer != null
                && mSelectedPortfolioContainer.defaultMenuIsNotDefaultPortfolio())
        {
            alertDialogUtil.popWithNegativeButton(getActivity(),
                    R.string.buy_sell_portfolio_changed_title,
                    R.string.buy_sell_portfolio_changed_message,
                    R.string.ok);
        }
    }

    public void displayBuySellSwitch()
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
            supportSell = maxSellableShares != null && maxSellableShares.intValue() > 0;
        }
        if (mSellBtn != null)
        {
            mSellBtn.setVisibility(supportSell ? View.VISIBLE : View.GONE);
        }
    }

    public void displayTriggerButton()
    {
        if (mBtnAddTrigger != null)
        {
            if (mappedAlerts != null)
            {
                mBtnAddTrigger.setEnabled(true);
                if (mappedAlerts.get(securityId) != null)
                {
                    mBtnAddTrigger.setText(R.string.stock_alert_edit_alert);
                }
                else
                {
                    mBtnAddTrigger.setText(R.string.stock_alert_add_alert);
                }
            }
            else // TODO check if failed
            {
                mBtnAddTrigger.setEnabled(false);
            }
        }
    }

    public void displayWatchlistButton()
    {
        if (mBtnAddWatchlist != null)
        {
            if (securityId == null || watchedList == null)
            {
                // TODO show disabled
                mBtnAddWatchlist.setEnabled(false);
            }
            else
            {
                mBtnAddWatchlist.setEnabled(true);
                mBtnAddWatchlist.setText(watchedList.contains(securityId) ?
                        R.string.watchlist_edit_title :
                        R.string.watchlist_add_title);
            }
        }
    }

    public void loadStockLogo()
    {
        if (mStockLogo != null)
        {
            if (mStockBgLogo != null)
            {
                mStockBgLogo.setVisibility(View.INVISIBLE);
            }
            if (isMyUrlOk())
            {
                picasso.load(securityCompactDTO.imageBlobUrl)
                        .transform(foregroundTransformation)
                        .into(mStockLogo, new Callback()
                        {
                            @Override public void onSuccess()
                            {
                                loadStockBgLogoDelayed();
                            }

                            @Override public void onError()
                            {
                                loadStockLogoExchange();
                            }
                        });
            }
            else
            {
                loadStockLogoExchange();
            }
        }
        else
        {
            loadStockBgLogoDelayed();
        }
    }

    public void loadStockLogoExchange()
    {
        if (mStockLogo != null)
        {
            if (securityCompactDTO != null && securityCompactDTO.exchange != null)
            {
                try
                {
                    Exchange exchange = Exchange.valueOf(securityCompactDTO.exchange);
                    mStockLogo.setImageResource(exchange.logoId);
                    loadStockBgLogoDelayed();
                } catch (IllegalArgumentException e)
                {
                    Timber.e("Unknown Exchange %s", securityCompactDTO.exchange, e);
                    loadStockLogoDefault();
                } catch (OutOfMemoryError e)
                {
                    Timber.e(e, securityCompactDTO.exchange);
                    loadStockLogoDefault();
                }
            }
            else
            {
                loadStockLogoDefault();
            }
        }
        else
        {
            loadStockBgLogoDelayed();
        }
    }

    public void loadStockLogoDefault()
    {
        if (mStockLogo != null)
        {
            mStockLogo.setImageResource(R.drawable.default_image);
        }
        loadStockBgLogoDelayed();
    }

    public void loadStockBgLogoDelayed()
    {
        View rootView = getView();
        if (rootView != null)
        {
            rootView.postDelayed(new Runnable()
            {
                @Override public void run()
                {
                    loadStockBgLogo();
                }
            }, MS_DELAY_FOR_BG_IMAGE);
        }
    }

    public void loadStockBgLogo()
    {
        if (mStockBgLogo != null)
        {
            if (isMyUrlOk())
            {
                RequestCreator requestCreator = picasso.load(securityCompactDTO.imageBlobUrl)
                        .transform(backgroundTransformation);
                resizeBackground(requestCreator, mStockBgLogo, new Callback()
                {
                    @Override public void onSuccess()
                    {
                        if (mStockBgLogo != null)
                        {
                            mStockBgLogo.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override public void onError()
                    {
                        loadStockBgLogoExchange();
                    }
                });
            }
            else
            {
                loadStockBgLogoExchange();
            }
        }
    }

    public void loadStockBgLogoExchange()
    {
        if (mStockBgLogo != null)
        {
            if (securityCompactDTO != null && securityCompactDTO.exchange != null)
            {
                try
                {
                    Exchange exchange = Exchange.valueOf(securityCompactDTO.exchange);
                    RequestCreator requestCreator = picasso.load(exchange.logoId)
                            .transform(backgroundTransformation);
                    resizeBackground(requestCreator, mStockBgLogo, null);
                    mStockBgLogo.setVisibility(View.VISIBLE);
                } catch (IllegalArgumentException e)
                {
                    loadStockBgLogoDefault();
                }
            }
            else
            {
                loadStockBgLogoDefault();
            }
        }
    }

    public void loadStockBgLogoDefault()
    {
        if (mStockBgLogo != null)
        {
            mStockBgLogo.setImageResource(R.drawable.default_image);
        }
    }

    protected void resizeBackground(RequestCreator requestCreator, ImageView imageView,
            Callback callback)
    {
        //int width = mInfoFrame.getWidth();
        //int height = mInfoFrame.getHeight();
        int width = mStockBgLogo.getWidth();
        int height = mStockBgLogo.getHeight();
        if (width > 0 && height > 0)
        {
            requestCreator.resize(width, height)
                    .centerCrop()
                    .into(imageView, callback);
        }
    }
    //</editor-fold>

    public boolean isMyUrlOk()
    {
        return (securityCompactDTO != null) && isUrlOk(securityCompactDTO.imageBlobUrl);
    }

    public static boolean isUrlOk(String url)
    {
        return (url != null) && (url.length() > 0);
    }

    @Override public void setTransactionTypeBuy(boolean transactionTypeBuy)
    {
        super.setTransactionTypeBuy(transactionTypeBuy);
        displayBuySellSwitch();
    }

    @Override protected void prepareFreshQuoteHolder()
    {
        super.prepareFreshQuoteHolder();
        freshQuoteHolder.identifier = "BuySellFragment";
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.btn_add_trigger)
    protected void handleBtnAddTriggerClicked()
    {
        if (mappedAlerts != null)
        {
            Bundle args = new Bundle();
            OwnedPortfolioId applicablePortfolioId = getApplicablePortfolioId();
            if (applicablePortfolioId != null)
            {
                BaseAlertEditFragment.putApplicablePortfolioId(args, applicablePortfolioId);
            }
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
            THToast.show("Try again in a moment");
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.btn_add_watch_list)
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

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.portfolio_selector_container)
    protected void showPortfolioSelector()
    {
        detachPortfolioMenuSubscription();
        portfolioMenuSubscription = AndroidObservable.bindFragment(
                this,
                mSelectedPortfolioContainer.createMenuObservable())
                .subscribe(new EmptyObserver<MenuOwnedPortfolioId>()
                {
                    @Override public void onNext(MenuOwnedPortfolioId args)
                    {
                        super.onNext(args);
                        linkWithApplicable(args, true);
                    }
                });
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick({R.id.btn_buy, R.id.btn_sell})
    protected void handleBuySellButtonsClicked(View view)
    {
        trackBuyClickEvent();
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
        showBuySellDialog();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.market_closed_icon)
    protected void handleMarketClosedIconClicked()
    {
        notifyMarketClosed();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.info)
    public void handleTabInfoClicked()
    {
        if (mBottomViewPager != null)
        {
            mBottomViewPager.setCurrentItem(0, true);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.discussions)
    public void handleDiscussionsTabClicked()
    {
        if (mBottomViewPager != null)
        {
            mBottomViewPager.setCurrentItem(1, true);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.news)
    public void handleNewsTabClicked()
    {
        if (mBottomViewPager != null)
        {
            mBottomViewPager.setCurrentItem(2, true);
        }
    }

    //<editor-fold desc="Interface Creators">
    private Callback createLogoReadyCallback()
    {
        return new Callback()
        {
            @Override public void onError()
            {
                loadBg();
            }

            @Override public void onSuccess()
            {
                loadBg();
            }

            public void loadBg()
            {
                if (mStockBgLogo != null && BuySellFragment.isUrlOk(
                        (String) mStockBgLogo.getTag(R.string.image_url)))
                {
                    Timber.i("Loading Bg for %s", mStockBgLogo.getTag(R.string.image_url));
                    picasso.load((String) mStockBgLogo.getTag(R.string.image_url))
                            .placeholder(R.drawable.default_image)
                            .error(R.drawable.default_image)
                            .resize(mStockBgLogo.getWidth(), mStockBgLogo.getHeight())
                            .centerInside()
                            .transform(foregroundTransformation)
                            .into(mStockBgLogo);
                }
                else if (mStockBgLogo != null && securityCompactDTO != null)
                {
                    int logoId = securityCompactDTO.getExchangeLogoId();
                    if (logoId != 0)
                    {
                        picasso.load(logoId)
                                .resize(mStockBgLogo.getWidth(), mStockBgLogo.getHeight())
                                .centerCrop()
                                .transform(foregroundTransformation)
                                .into(mStockBgLogo);
                    }
                }
            }
        };
    }

    public void showBuySellDialog()
    {
        if (quoteDTO != null
                && BuyDialogFragment.canShowDialog(quoteDTO, isTransactionTypeBuy))
        {
            OwnedPortfolioId currentMenu = mSelectedPortfolioContainer.getCurrentMenu();
            if (currentMenu != null)
            {
                abstractTransactionDialogFragment = BuyDialogFragment.newInstance(
                        securityId,
                        currentMenu.getPortfolioIdKey(),
                        quoteDTO,
                        isTransactionTypeBuy);
                abstractTransactionDialogFragment.show(getActivity().getFragmentManager(), AbstractTransactionDialogFragment.class.getName());
                abstractTransactionDialogFragment.setBuySellTransactionListener(new AbstractTransactionDialogFragment.BuySellTransactionListener()
                {
                    @Override public void onTransactionSuccessful(boolean isBuy,
                            @NotNull SecurityPositionTransactionDTO securityPositionTransactionDTO)
                    {
                        showPrettyReviewAndInvite(isBuy);
                        pushPortfolioFragment(securityPositionTransactionDTO);
                    }

                    @Override public void onTransactionFailed(boolean isBuy, THException error)
                    {
                        // TODO Toast error buy?
                    }
                });
            }
            else
            {
                alertDialogUtil.popWithNegativeButton(
                        getActivity(),
                        R.string.buy_sell_no_portfolio_title,
                        R.string.buy_sell_no_portfolio_message,
                        R.string.buy_sell_no_portfolio_cancel);
            }
        }
        else
        {
            alertDialogUtil.popWithNegativeButton(
                    getActivity(),
                    R.string.buy_sell_no_quote_title,
                    R.string.buy_sell_no_quote_message,
                    R.string.buy_sell_no_quote_cancel);
        }
    }

    private void showPrettyReviewAndInvite(boolean isBuy)
    {
        Double profit = abstractTransactionDialogFragment.getProfitOrLossUsd();
        if (!isBuy && profit != null && profit > 0)
        {
            if (mShowAskForReviewDialogPreference.isItTime())
            {
                broadcastUtils.enqueue(new SendLoveBroadcastSignal());
            }
            else if (mShowAskForInviteDialogPreference.isItTime())
            {
                AskForInviteDialogFragment.showInviteDialog(getActivity().getFragmentManager());
            }
        }
    }

    public void shareToWeChat()
    {
        //TODO Move this!
        if (socialSharePreferenceHelperNew.isShareEnabled(SocialNetworkEnum.WECHAT, DEFAULT_IS_SHARED_TO_WECHAT))
        {
            WeChatDTO weChatDTO = new WeChatDTO();
            weChatDTO.id = securityCompactDTO.id;
            weChatDTO.type = WeChatMessageType.Trade;
            if (isMyUrlOk())
            {
                weChatDTO.imageURL = securityCompactDTO.imageBlobUrl;
            }
            if (isTransactionTypeBuy)
            {
                weChatDTO.title = getString(R.string.buy_sell_switch_buy) + " "
                        + securityCompactDTO.name + " " + abstractTransactionDialogFragment.getQuantityString() + getString(
                        R.string.buy_sell_share_count) + " @" + quoteDTO.ask;
            }
            else
            {
                weChatDTO.title = getString(R.string.buy_sell_switch_sell) + " "
                        + securityCompactDTO.name + " " + abstractTransactionDialogFragment.getQuantityString() + getString(
                        R.string.buy_sell_share_count) + " @" + quoteDTO.bid;
            }
            socialSharerLazy.get().share(weChatDTO); // TODO proper callback?
        }
    }

    private void detachBuySellMiddleCallback()
    {
        if (buySellMiddleCallback != null)
        {
            buySellMiddleCallback.setPrimaryCallback(null);
        }
        buySellMiddleCallback = null;
    }

    private void pushPortfolioFragment(@NotNull SecurityPositionTransactionDTO securityPositionTransactionDTO)
    {
        pushPortfolioFragment(new OwnedPortfolioId(
                currentUserId.get(),
                securityPositionTransactionDTO.portfolio.id));
    }

    private void pushPortfolioFragment(OwnedPortfolioId ownedPortfolioId)
    {
        shareToWeChat();
        if (isResumed())
        {
            // TODO find a better way to remove this fragment from the stack
            navigator.get().popFragment();

            Bundle args = new Bundle();
            OwnedPortfolioId applicablePortfolioId = getApplicablePortfolioId();
            if (applicablePortfolioId != null)
            {
                PositionListFragment.putApplicablePortfolioId(args, applicablePortfolioId);
            }
            PositionListFragment.putGetPositionsDTOKey(args, ownedPortfolioId);
            PositionListFragment.putShownUser(args, ownedPortfolioId.getUserBaseKey());
            navigator.get().pushFragment(PositionListFragment.class, args);
        }
    }

    private void trackBuyClickEvent()
    {
        analytics.fireEvent(new BuySellEvent(isTransactionTypeBuy, securityId));
    }

    private void pushStockInfoFragmentIn()
    {
        Bundle args = new Bundle();
        args.putBundle(StockInfoFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, this.securityId.getArgs());
        if (providerId != null)
        {
            args.putBundle(StockInfoFragment.BUNDLE_KEY_PROVIDER_ID_BUNDLE,
                    providerId.getArgs());
        }
        navigator.get().pushFragment(StockInfoFragment.class, args);
    }

    private BroadcastReceiver createImageButtonClickBroadcastReceiver()
    {
        return new BroadcastReceiver()
        {
            @Override public void onReceive(Context context, Intent intent)
            {
                pushStockInfoFragmentIn();
            }
        };
    }
    //</editor-fold>

    @Override protected FreshQuoteHolder.FreshQuoteListener createFreshQuoteListener()
    {
        return new BuySellFreshQuoteListener();
    }

    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_buy_sell;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
    {
    }

    @Override public void onPageSelected(int position)
    {
        selectPage(position);
    }

    @Override public void onPageScrollStateChanged(int state)
    {
    }

    public void selectPage(int position)
    {
        selectedPageIndex = position;
        mInfoTextView.setSelected(position == 0);
        mDiscussTextView.setSelected(position == 1);
        mNewsTextView.setSelected(position == 2);

        if (selectedPageIndex == 0)
        {
            resideMenu.clearIgnoredViewList();
        }
        else
        {
            resideMenu.addIgnoredView(mBottomViewPager);
        }
    }

    protected class BuySellFreshQuoteListener extends AbstractBuySellFreshQuoteListener
    {
        @Override public void onMilliSecToRefreshQuote(long milliSecToRefresh)
        {
            if (mQuoteRefreshProgressBar != null)
            {
                mQuoteRefreshProgressBar.setProgress(
                        (int) (milliSecToRefresh / MILLISEC_QUOTE_COUNTDOWN_PRECISION));
            }
        }
    }

    protected class BuySellPortfolioCompactListObserver extends BasePurchaseManagementPortfolioCompactListObserver
    {
        @Override public void onNext(Pair<UserBaseKey, PortfolioCompactDTOList> pair)
        {
            super.onNext(pair);
            PortfolioCompactDTO defaultPortfolio = pair.second.getDefaultPortfolio();
            if (defaultPortfolio != null)
            {
                mSelectedPortfolioContainer.addMenuOwnedPortfolioId(new MenuOwnedPortfolioId(currentUserId.toUserBaseKey(), defaultPortfolio));
            }
            setInitialSellQuantityIfCan();
        }
    }

    protected DTOCacheNew.Listener<UserBaseKey, WatchlistPositionDTOList> createUserWatchlistCacheListener()
    {
        return new BuySellUserWatchlistCacheListener();
    }

    protected class BuySellUserWatchlistCacheListener
            implements DTOCacheNew.Listener<UserBaseKey, WatchlistPositionDTOList>
    {
        @Override
        public void onDTOReceived(@NotNull UserBaseKey key, @NotNull WatchlistPositionDTOList value)
        {
            linkWithWatchlist(value, true);
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
            Timber.e("Failed to fetch list of watch list items", error);
            THToast.show(R.string.error_fetch_portfolio_list_info);
        }
    }
}
