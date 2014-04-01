package com.tradehero.th.fragments.trade;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.milestone.Milestone;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.market.Exchange;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.position.PositionDTOCompactList;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.security.WarrantDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THPurchaseReporter;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.fragments.alert.AlertCreateFragment;
import com.tradehero.th.fragments.alert.AlertEditFragment;
import com.tradehero.th.fragments.alert.BaseAlertEditFragment;
import com.tradehero.th.fragments.security.BuySellBottomStockPagerAdapter;
import com.tradehero.th.fragments.security.StockInfoFragment;
import com.tradehero.th.fragments.security.WatchlistEditFragment;
import com.tradehero.th.fragments.trade.view.PricingBidAskView;
import com.tradehero.th.fragments.trade.view.QuickPriceButtonSet;
import com.tradehero.th.fragments.trade.view.TradeQuantityView;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.models.alert.SecurityAlertAssistant;
import com.tradehero.th.models.graphics.ForSecurityItemBackground;
import com.tradehero.th.models.graphics.ForSecurityItemForeground;
import com.tradehero.th.utils.metrics.localytics.THLocalyticsSession;
import com.tradehero.th.models.portfolio.MenuOwnedPortfolioId;
import com.tradehero.th.models.provider.ProviderSpecificResourcesDTO;
import com.tradehero.th.models.provider.ProviderSpecificResourcesFactory;
import com.tradehero.th.models.security.WarrantSpecificKnowledgeFactory;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import com.tradehero.th.utils.metrics.localytics.LocalyticsConstants;
import com.viewpagerindicator.PageIndicator;

import java.util.*;
import javax.inject.Inject;
import timber.log.Timber;

public class BuySellFragment extends AbstractBuySellFragment
    implements SecurityAlertAssistant.OnPopulatedListener, WithTutorial
{
    public static final String EVENT_CHART_IMAGE_CLICKED = BuySellFragment.class.getName() + ".chartButtonClicked";

    public final static int ADD_ALERT_RES_ID = R.drawable.add_alert;
    public final static int EDIT_ALERT_RES_ID = R.drawable.active_alert;
    public final static int INACTIVE_ALERT_RES_ID = R.drawable.alert_inactive;
    public final static float BUY_BUTTON_DISABLED_ALPHA = 0.5f;
    public static final int MS_DELAY_FOR_BG_IMAGE  = 200;

    private ToggleButton mBuySellSwitch;

    @InjectView(R.id.stock_bg_logo) protected ImageView mStockBgLogo;
    @InjectView(R.id.stock_logo) protected ImageView mStockLogo;

    @InjectView(R.id.portfolio_selector_container) protected View mSelectedPortfolioContainer;
    @InjectView(R.id.portfolio_selected) protected TextView mSelectedPortfolio;

    @InjectView(R.id.stock_name) protected TextView mStockName;

    @InjectView(R.id.quote_refresh_countdown) protected ProgressBar mQuoteRefreshProgressBar;
    @InjectView(R.id.chart_frame) protected FrameLayout mInfoFrame;
    @InjectView(R.id.pricing_bid_ask_view) protected PricingBidAskView mPricingBidAskView;
    @InjectView(R.id.trade_quantity_view) protected TradeQuantityView mTradeQuantityView;
    @InjectView(R.id.quick_price_button_set) protected QuickPriceButtonSet mQuickPriceButtonSet;
    @InjectView(R.id.trade_bottom_pager) protected ViewPager mBottomViewPager;

    @InjectView(R.id.btn_buy) protected Button mBuyBtn;
    @InjectView(R.id.seekBar) protected SeekBar mSlider;
    @InjectView(R.id.btn_add_cash) protected ImageButton mBtnAddCash;
    @InjectView(R.id.btn_add_trigger) protected ImageButton mBtnAddTrigger;
    @InjectView(R.id.btn_watch_list) protected ImageView mBtnWatchlist;

    @Inject PortfolioCache portfolioCache;
    @Inject PortfolioCompactCache portfolioCompactCache;
    @Inject THLocalyticsSession localyticsSession;

    @Inject UserWatchlistPositionCache userWatchlistPositionCache;
    @Inject WatchlistPositionCache watchlistPositionCache;
    @Inject ProviderSpecificResourcesFactory providerSpecificResourcesFactory;
    @Inject WarrantSpecificKnowledgeFactory warrantSpecificKnowledgeFactory;
    @Inject Picasso picasso;

    private PopupMenu mPortfolioSelectorMenu;
    private Set<MenuOwnedPortfolioId> usedMenuOwnedPortfolioIds;

    protected SecurityAlertAssistant securityAlertAssistant;
    protected PageIndicator mBottomPagerIndicator;
    protected DTOCache.Listener<UserBaseKey, SecurityIdList> userWatchlistPositionCacheListener;
    protected DTOCache.GetOrFetchTask<UserBaseKey, SecurityIdList> userWatchlistPositionCacheFetchTask;

    int mQuantity = 0;
    int volume = 0;
    int avgDailyVolume = 0;

    private Bundle desiredArguments;

    protected SecurityIdList watchedList;

    @Inject @ForSecurityItemForeground protected Transformation foregroundTransformation;
    @Inject @ForSecurityItemBackground protected Transformation backgroundTransformation;
    private BuySellBottomStockPagerAdapter bottomViewPagerAdapter;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        securityAlertAssistant = new SecurityAlertAssistant();
        this.userWatchlistPositionCacheListener = new BuySellUserWatchlistCacheListener();
    }

    @Override protected Milestone.OnCompleteListener createPortfolioCompactListRetrievedListener()
    {
        return new BuySellPortfolioCompactListMilestoneListener();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //THLog.d(TAG, "onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);

        if (desiredArguments == null)
        {
            desiredArguments = getArguments();
        }

        View view = null;
        view = inflater.inflate(R.layout.fragment_buy_sell, container, false);
        initViews(view);
        return view;
    }

    @Override protected void initViews(View view)
    {
        super.initViews(view);

        ButterKnife.inject(this, view);

        if (mQuoteRefreshProgressBar != null)
        {
            mQuoteRefreshProgressBar.setMax((int) (MILLISEC_QUOTE_REFRESH / MILLISEC_QUOTE_COUNTDOWN_PRECISION));
            mQuoteRefreshProgressBar.setProgress(mQuoteRefreshProgressBar.getMax());
        }

        if (mSelectedPortfolioContainer != null)
        {
            mSelectedPortfolioContainer.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    showPortfolioSelector();
                }
            });
        }

        if (mSelectedPortfolio != null)
        {
            mPortfolioSelectorMenu = new PopupMenu(getActivity(), mSelectedPortfolio);
            mPortfolioSelectorMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
            {
                @Override public boolean onMenuItemClick(android.view.MenuItem menuItem)
                {
                    return selectDifferentPortfolio(menuItem);
                }
            });
        }

        if (mBtnWatchlist != null)
        {
            mBtnWatchlist.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    handleBtnWatchlistClicked();
                }
            });
        }

        if (mQuickPriceButtonSet != null)
        {
            mQuickPriceButtonSet.setListener(createQuickButtonSetListener());
            mQuickPriceButtonSet.addButton(R.id.toggle5k);
            mQuickPriceButtonSet.addButton(R.id.toggle10k);
            mQuickPriceButtonSet.addButton(R.id.toggle25k);
            mQuickPriceButtonSet.addButton(R.id.toggle50k);
        }

        if (mSlider != null)
        {
            mSlider.setOnSeekBarChangeListener(createSeekBarListener());
        }

        if (mBtnAddCash != null)
        {
            mBtnAddCash.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    handleBtnAddCashPressed();
                }
            });
        }

        if (mBtnAddTrigger != null)
        {
            mBtnAddTrigger.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    handleBtnAddTriggerClicked();
                }
            });
        }

        if (mBuyBtn != null)
        {
            mBuyBtn.setOnClickListener(createBuyButtonListener());
        }
        displayBuyButton(); // Just to have it disabled while we are collecting data

        if (bottomViewPagerAdapter == null)
        {
            bottomViewPagerAdapter = new BuySellBottomStockPagerAdapter(getActivity(), getFragmentManager());
        }
        if (mBottomViewPager != null)
        {
            mBottomViewPager.setAdapter(bottomViewPagerAdapter);
        }

        mBottomPagerIndicator = (PageIndicator) view.findViewById(R.id.trade_bottom_pager_indicator);
        if (mBottomPagerIndicator != null && mBottomViewPager != null)
        {
            mBottomPagerIndicator.setViewPager(mBottomViewPager, 0);
        }
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        securityAlertAssistant.setOnPopulatedListener(this);
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.buy_sell_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.buy_sell_menu_toggle, menu);
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                | ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE);
        displayExchangeSymbol(actionBar);
    }

    @Override public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);

        com.actionbarsherlock.view.MenuItem menuElements = menu.findItem(R.id.menu_elements_buy_sell);

        mBuySellSwitch = (ToggleButton) menuElements.getActionView().findViewById(R.id.trade_menu_toggle_mode);
        if (mBuySellSwitch != null)
        {
            mBuySellSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override public void onCheckedChanged(CompoundButton compoundButton, boolean checked)
                {
                    setTransactionTypeBuy(checked);
                }
            });
        }
        displayActionBarElements();
    }

    @Override public void onDestroyOptionsMenu()
    {
        if (mBuySellSwitch != null)
        {
            mBuySellSwitch.setOnCheckedChangeListener(null);
        }
        mBuySellSwitch = null;
        super.onDestroyOptionsMenu();
    }

    @Override public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.buy_sell_menu_market_status:
                handleMarketCloseClicked();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //</editor-fold>

    @Override public void onStart()
    {
        super.onStart();
        localyticsSession.tagEvent(LocalyticsConstants.BuySellPanel_Chart, BuySellBottomStockPagerAdapter.getDefaultChartTimeSpan(), securityId);
    }

    @Override public void onResume()
    {
        super.onResume();

        if (mPricingBidAskView != null)
        {
            mPricingBidAskView.setBuy(isTransactionTypeBuy);
        }

        if (mTradeQuantityView != null)
        {
            mTradeQuantityView.setBuy(isTransactionTypeBuy);
        }

        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(chartImageButtonClickReceiver, new IntentFilter(EVENT_CHART_IMAGE_CLICKED));

        securityAlertAssistant.setUserBaseKey(currentUserId.toUserBaseKey());
        securityAlertAssistant.populate();

        displayButtonAddCash();
    }

    @Override public void onPause()
    {
        LocalBroadcastManager.getInstance(getActivity())
                .unregisterReceiver(chartImageButtonClickReceiver);
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        detachWatchlistFetchTask();

        securityAlertAssistant.setOnPopulatedListener(null);

        if (mSelectedPortfolioContainer != null)
        {
            mSelectedPortfolioContainer.setOnClickListener(null);
        }
        mSelectedPortfolioContainer = null;
        mSelectedPortfolio = null;

        if (mPortfolioSelectorMenu != null)
        {
            mPortfolioSelectorMenu.setOnMenuItemClickListener(null);
        }
        mPortfolioSelectorMenu = null;

        if (mQuickPriceButtonSet != null)
        {
            mQuickPriceButtonSet.setListener(null);
        }
        mQuickPriceButtonSet = null;

        if (mSlider != null)
        {
            mSlider.setOnSeekBarChangeListener(null);
        }
        mSlider = null;

        if (mBtnAddCash != null)
        {
            mBtnAddCash.setOnClickListener(null);
        }
        mBtnAddCash = null;

        if (mBtnAddTrigger != null)
        {
            mBtnAddTrigger.setEnabled(false);
            mBtnAddTrigger.setOnClickListener(null);
        }
        mBtnAddTrigger = null;

        if (mBtnWatchlist != null)
        {
            mBtnWatchlist.setEnabled(false);
            mBtnWatchlist.setOnClickListener(null);
        }
        mBtnWatchlist = null;

        if (mBuyBtn != null)
        {
            mBuyBtn.setOnClickListener(null);
        }
        mBuyBtn = null;

        bottomViewPagerAdapter = null;
        mBottomViewPager = null;
        mBottomPagerIndicator = null;

        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        this.userWatchlistPositionCacheListener = null;
        securityAlertAssistant = null;
        super.onDestroy();
    }

    protected void detachWatchlistFetchTask()
    {
        if (this.userWatchlistPositionCacheFetchTask != null)
        {
            this.userWatchlistPositionCacheFetchTask.setListener(null);
        }
        this.userWatchlistPositionCacheFetchTask = null;
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
        this.userWatchlistPositionCacheFetchTask = userWatchlistPositionCache.getOrFetch(currentUserId.toUserBaseKey(), userWatchlistPositionCacheListener);
        this.userWatchlistPositionCacheFetchTask.execute();
    }

    @Override public void linkWith(SecurityCompactDTO securityCompactDTO, boolean andDisplay)
    {
        super.linkWith(securityCompactDTO, andDisplay);
        buildUsedMenuPortfolios();
        if (mTradeQuantityView != null)
        {
            mTradeQuantityView.linkWith(securityCompactDTO, andDisplay);
        }
        if (mPricingBidAskView != null)
        {
            mPricingBidAskView.linkWith(securityCompactDTO, andDisplay);
        }
        if (andDisplay)
        {
            displayMarketClose();
            displaySelectedPortfolioContainer();
            displayPortfolioSelectorMenu();
            displaySelectedPortfolio();
            displayPricingBidAskView();
            displayStockName();
            displayBottomViewPager();
            loadStockLogo();
        }
    }

    @Override public void linkWith(final SecurityPositionDetailDTO securityPositionDetailDTO, boolean andDisplay)
    {
        super.linkWith(securityPositionDetailDTO, andDisplay);

        setInitialSellQuantityIfCan();

        if (andDisplay)
        {
            displaySelectedPortfolioContainer();
            displayPortfolioSelectorMenu();
            displaySelectedPortfolio();
            displayPricingBidAskView();
            displayQuickPriceButtonSet();
            displaySlider();
            displayBuySellSwitch();
        }
    }

    @Override public void linkWith(final PositionDTOCompactList positionDTOCompacts, boolean andDisplay)
    {
        super.linkWith(positionDTOCompacts, andDisplay);
        if (mTradeQuantityView != null)
        {
            mTradeQuantityView.linkWith(positionDTOCompacts, andDisplay);
        }
        if (andDisplay)
        {

        }
    }

    @Override public void linkWith(UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        super.linkWith(userProfileDTO, andDisplay);
        setInitialBuyQuantityIfCan();
        setInitialSellQuantityIfCan();
        if (mTradeQuantityView != null)
        {
            mTradeQuantityView.linkWith(userProfileDTO, andDisplay);
        }
        if (andDisplay)
        {
            displayQuickPriceButtonSet();
            displaySlider();
        }
    }

    @Override protected void linkWith(QuoteDTO quoteDTO, boolean andDisplay)
    {
        super.linkWith(quoteDTO, andDisplay);
        setInitialBuyQuantityIfCan();
        setInitialSellQuantityIfCan();
        if (mTradeQuantityView != null)
        {
            mTradeQuantityView.linkWith(quoteDTO, andDisplay);
        }
        if (mPricingBidAskView != null)
        {
            mPricingBidAskView.linkWith(quoteDTO, andDisplay);
        }
        if (andDisplay)
        {
            displayPricingBidAskView();
            displayTradeQuantityView();
            displayQuickPriceButtonSet();
            displaySlider();
        }
    }

    @Override protected void linkWithApplicable(OwnedPortfolioId purchaseApplicablePortfolioId, boolean andDisplay)
    {
        super.linkWithApplicable(purchaseApplicablePortfolioId, andDisplay);
        if (purchaseApplicablePortfolioId != null)
        {
            linkWith(portfolioCompactCache.get(purchaseApplicablePortfolioId.getPortfolioIdKey()), andDisplay);
        }
        else
        {
            linkWith((PortfolioCompactDTO) null, andDisplay);
        }
        if (andDisplay)
        {
            displaySelectedPortfolio();
            displayButtonAddCash();
        }
    }

    @Override protected void linkWith(PortfolioCompactDTO portfolioCompactDTO, boolean andDisplay)
    {
        super.linkWith(portfolioCompactDTO, andDisplay);
        if (mTradeQuantityView != null)
        {
            mTradeQuantityView.linkWith(portfolioCompactDTO, andDisplay);
        }
        if (mPricingBidAskView != null)
        {
            mPricingBidAskView.linkWith(portfolioCompactDTO, andDisplay);
        }
        clampBuyQuantity(andDisplay);
        clampSellQuantity(andDisplay);
        if (andDisplay)
        {
            // TODO max purchasable shares
            displaySlider();
        }
    }

    protected void linkWithWatchlist(SecurityIdList watchedList, boolean andDisplay)
    {
        this.watchedList = watchedList;
        if (andDisplay)
        {
            displayWatchlistButton();
        }
    }

    @Override protected void linkWithBuyQuantity(Integer buyQuantity, boolean andDisplay)
    {
        super.linkWithBuyQuantity(buyQuantity, andDisplay);
        if (mTradeQuantityView != null && isTransactionTypeBuy)
        {
            // We use the mBuyQuantity field, not the buyQuantity variable because it has been clamped
            mTradeQuantityView.setShareQuantity(mBuyQuantity);
        }
    }

    @Override protected void linkWithSellQuantity(Integer sellQuantity, boolean andDisplay)
    {
        super.linkWithSellQuantity(sellQuantity, andDisplay);
        if (mTradeQuantityView != null && !isTransactionTypeBuy)
        {
            // We use the mBuyQuantity field, not the buyQuantity variable because it has been clamped
            mTradeQuantityView.setShareQuantity(mSellQuantity);
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
                    if (mBuySellSwitch != null)
                    {
                        mBuySellSwitch.setVisibility(View.GONE);
                    }
                }
                else
                {
                    if (mBuySellSwitch != null)
                    {
                        mBuySellSwitch.setVisibility(View.VISIBLE);
                    }
                }
            }
            else
            {
                if (mBuySellSwitch != null)
                {
                    mBuySellSwitch.setVisibility(View.GONE);
                }
            }

            if (mBuySellSwitch != null)
            {
                mBuySellSwitch.setChecked(isTransactionTypeBuy);
            }
        }
    }

    //<editor-fold desc="Display Methods">
    protected void buildUsedMenuPortfolios()
    {
        OwnedPortfolioId defaultOwnedPortfolioId = portfolioCompactListCache.getDefaultPortfolio(currentUserId.toUserBaseKey());

        if (defaultOwnedPortfolioId != null && securityCompactDTO != null)
        {
            Set<MenuOwnedPortfolioId> newMenus = new TreeSet<>();

            PortfolioCompactDTO defaultPortfolioCompactDTO = portfolioCompactCache.get(defaultOwnedPortfolioId.getPortfolioIdKey());
            newMenus.add(new MenuOwnedPortfolioId(defaultOwnedPortfolioId, defaultPortfolioCompactDTO));

            TreeSet<OwnedPortfolioId> otherPortfolioIds = new TreeSet<>();
            // HACK
            {
                if (securityCompactDTO instanceof WarrantDTO)
                {
                    for (Map.Entry<ProviderId, OwnedPortfolioId> entry: warrantSpecificKnowledgeFactory.getWarrantApplicablePortfolios().entrySet())
                    {
                        if (providerId == null)
                        {
                            providerId = entry.getKey();
                        }
                        otherPortfolioIds.add(entry.getValue());
                        break; // Keep only the first
                    }
                }
            }

            ProviderSpecificResourcesDTO providerSpecificResourcesDTO = providerSpecificResourcesFactory.createResourcesDTO(providerId);

            Bundle ownedPortfolioArgs = getArguments().getBundle(BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE);
            if (ownedPortfolioArgs != null)
            {
                OwnedPortfolioId ownedPortfolioId = new OwnedPortfolioId(ownedPortfolioArgs);
                if (!ownedPortfolioId.equals(defaultOwnedPortfolioId))
                {
                    otherPortfolioIds.add(ownedPortfolioId);
                }
            }

            Iterator<OwnedPortfolioId> iterator = otherPortfolioIds.iterator();
            while (iterator.hasNext())
            {
                OwnedPortfolioId ownedPortfolioId = iterator.next();
                PortfolioCompactDTO portfolioCompactDTO = portfolioCompactCache.get(ownedPortfolioId.getPortfolioIdKey());
                if (portfolioCompactDTO == null)
                {
                    Timber.e(new NullPointerException("Missing portfolioCompact for " + ownedPortfolioId), "");
                }
                else if (portfolioCompactDTO.providerId != null && providerId != null &&
                        providerId.key.equals(portfolioCompactDTO.providerId) &&
                        providerSpecificResourcesDTO != null && providerSpecificResourcesDTO.competitionPortfolioTitleResId > 0)
                {

                    newMenus.add(new MenuOwnedPortfolioId(ownedPortfolioId, getString(providerSpecificResourcesDTO.competitionPortfolioTitleResId)));
                }
                else
                {
                    newMenus.add(new MenuOwnedPortfolioId(ownedPortfolioId, portfolioCompactDTO));
                }
            }

            usedMenuOwnedPortfolioIds = newMenus;
        }
    }

    public void display()
    {
        displayActionBarElements();
        displayPageElements();

        if (securityCompactDTO == null || securityCompactDTO.averageDailyVolume == null)
        {
            avgDailyVolume = 0;
        }
        else
        {
            avgDailyVolume = (int) Math.ceil(securityCompactDTO.averageDailyVolume);
        }

        if (securityCompactDTO == null || securityCompactDTO.volume == null)
        {
            volume = 0;
        }
        else
        {
            volume = (int) Math.ceil(securityCompactDTO.volume);
        }
    }

    public void displayPageElements()
    {
        displaySelectedPortfolioContainer();
        displayPortfolioSelectorMenu();
        displaySelectedPortfolio();
        displayPricingBidAskView();
        displayTradeQuantityView();
        displayButtonAddCash();
        displayBuyButton();
        displayBottomViewPager();
        displayStockName();
        displayQuickPriceButtonSet();
        displaySlider();
        displayTriggerButton();
        loadStockLogo();
    }

    public void displaySelectedPortfolioContainer()
    {
        if (mSelectedPortfolioContainer != null)
        {
            mSelectedPortfolioContainer.setVisibility(
                    usedMenuOwnedPortfolioIds != null && usedMenuOwnedPortfolioIds.size() > 1 ? View.VISIBLE : View.GONE);
        }
    }

    public void displayPortfolioSelectorMenu()
    {
        if (mPortfolioSelectorMenu != null)
        {
            mPortfolioSelectorMenu.getMenu().clear();
            if (usedMenuOwnedPortfolioIds != null)
            {
                for (MenuOwnedPortfolioId menuOwnedPortfolioId: usedMenuOwnedPortfolioIds)
                {
                    if (menuOwnedPortfolioId != null)
                    {
                        mPortfolioSelectorMenu.getMenu().add(Menu.NONE, Menu.NONE, Menu.NONE, menuOwnedPortfolioId);
                    }
                    else
                    {
                        Timber.e(new NullPointerException("menuOwnedPortfolioId should not be null"), "A portfolio id menu was not added to the list");
                    }
                }
            }
        }
    }

    public void displaySelectedPortfolio()
    {
        TextView selectedPortfolio = mSelectedPortfolio;
        if (selectedPortfolio != null)
        {
            Set<MenuOwnedPortfolioId> usedMenuLink = usedMenuOwnedPortfolioIds;
            if (usedMenuLink != null)
            {
                // Being paranoid about stuff become null
                Set<MenuOwnedPortfolioId> userMenuCopy = new TreeSet<>(usedMenuLink);
                if (userMenuCopy.size() > 0 && purchaseApplicableOwnedPortfolioId != null)
                {
                    MenuOwnedPortfolioId chosen = null;

                    final Iterator<MenuOwnedPortfolioId> iterator = userMenuCopy.iterator();
                    MenuOwnedPortfolioId lastElement = null;
                    while (iterator.hasNext())
                    {
                        lastElement = iterator.next();
                        if (purchaseApplicableOwnedPortfolioId.equals(lastElement))
                        {
                            chosen = lastElement;
                        }
                    }
                    if (chosen == null)
                    {
                        chosen = lastElement;
                    }

                    if (chosen == null)
                    {
                        Timber.e(new NullPointerException("chosen is null userMenuCopy size " + userMenuCopy.size()), "chosen is null userMenuCopy size " + userMenuCopy.size());
                    }
                    if (selectedPortfolio == null)
                    {
                        Timber.e(new NullPointerException("selectedPortfolio is null"), "selectedPortfolio is null");
                    }
                    selectedPortfolio.setText(chosen);
                }
            }
        }
    }

    public void displayPricingBidAskView()
    {
        if (mPricingBidAskView != null)
        {
            mPricingBidAskView.display();
        }
    }

    public void displayTradeQuantityView()
    {
        if (mTradeQuantityView != null)
        {
            mTradeQuantityView.display();
        }
    }

    public void displayButtonAddCash()
    {
        if (mBtnAddCash != null)
        {
            mBtnAddCash.setEnabled(purchaseApplicableOwnedPortfolioId != null && purchaseApplicableOwnedPortfolioId.isValid());
        }
    }

    public void displayBuyButton()
    {
        if (mBuyBtn != null)
        {
            if (isTransactionTypeBuy)
            {
                boolean isOk = mBuyQuantity != null && mBuyQuantity > 0;
                mBuyBtn.setEnabled(isOk);
                mBuyBtn.setText(isOk ? R.string.buy_sell_button_buy : R.string.buy_sell_button_cannot_buy);
            }
            else
            {
                boolean isOk = mSellQuantity != null && mSellQuantity > 0;
                mBuyBtn.setEnabled(isOk);
                mBuyBtn.setText(isOk ? R.string.buy_sell_button_sell : R.string.buy_sell_button_cannot_sell);
            }
            mBuyBtn.setAlpha(mBuyBtn.isEnabled() ? 1 : BUY_BUTTON_DISABLED_ALPHA);
        }
    }

    public void displayBottomViewPager()
    {
        BuySellBottomStockPagerAdapter adapter = bottomViewPagerAdapter;
        if (adapter != null)
        {
            SecurityCompactDTO adapterDTO = adapter.getSecurityCompactDTO();
            if (securityId != null && (adapterDTO == null || !securityId.equals(adapterDTO.getSecurityId())))
            {
                adapter.linkWith(providerId);
                adapter.linkWith(securityCompactDTO);

                ViewPager viewPager = mBottomViewPager;
                if (viewPager != null)
                {
                    viewPager.post(new Runnable()
                    {
                        @Override public void run()
                        {
                            // We need to do it in a later frame otherwise the pager adapter crashes with IllegalStateException
                            BuySellBottomStockPagerAdapter adapter = bottomViewPagerAdapter;
                            if (adapter != null)
                            {
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }
        }
    }

    public void displayStockName()
    {
        if (mStockName != null)
        {
            if (securityCompactDTO != null)
            {
                mStockName.setText(securityCompactDTO.name);
            }
            else
            {
                mStockName.setText("");
            }
        }
    }

    public void displayQuickPriceButtonSet()
    {
        QuickPriceButtonSet buttonSetCopy = mQuickPriceButtonSet;
        if (buttonSetCopy != null)
        {
            if (quoteDTO == null)
            {
                buttonSetCopy.setEnabled(false);
            }
            else if (isTransactionTypeBuy && quoteDTO.ask == null)
            {
                buttonSetCopy.setEnabled(false);
            }
            else if (isTransactionTypeBuy)
            {
                buttonSetCopy.setEnabled(true);
                if (this.userProfileDTO != null && userProfileDTO.portfolio != null)
                {
                    buttonSetCopy.setMaxPrice(userProfileDTO.portfolio.cashBalance);
                }
            }
            else if (!isTransactionTypeBuy && (quoteDTO.bid == null || quoteDTO.toUSDRate == null))
            {
                buttonSetCopy.setEnabled(false);
            }
            else if (!isTransactionTypeBuy)
            {
                buttonSetCopy.setEnabled(true);
                Integer maxSellableShares = getMaxSellableShares();
                if (maxSellableShares != null)
                {
                    // TODO see other currencies
                    buttonSetCopy.setMaxPrice(maxSellableShares * quoteDTO.bid * quoteDTO.toUSDRate);
                }
            }
        }
    }

    public void displaySlider()
    {
        if (mSlider != null)
        {
            if (isTransactionTypeBuy)
            {
                Integer maxPurchasableShares = getMaxPurchasableShares();
                if (maxPurchasableShares != null)
                {
                    mSlider.setMax(maxPurchasableShares);
                    mSlider.setEnabled(maxPurchasableShares > 0);
                    if (mBuyQuantity != null)
                    {
                        mSlider.setProgress(mBuyQuantity);
                    }
                }
            }
            else
            {
                Integer maxSellableShares = getMaxSellableShares();
                if (maxSellableShares != null)
                {
                    if (maxSellableShares != 0)
                    {
                        if (mBuySellSwitch != null)
                        {
                            mBuySellSwitch.setVisibility(View.VISIBLE);
                        }
                    }
                    mSlider.setMax(maxSellableShares);
                    mSlider.setEnabled(maxSellableShares > 0);
                    if (mSellQuantity != null)
                    {
                        mSlider.setProgress(mSellQuantity);
                    }
                }
            }
        }
    }

    public void displayActionBarElements()
    {
        displayMarketClose();
        displayBuySellSwitch();
    }

    public void displayBuySellSwitch()
    {
        if (mBuySellSwitch != null)
        {
            if (positionDTOCompactList == null || positionDTOCompactList.size() == 0 || purchaseApplicableOwnedPortfolioId == null)
            {
                mBuySellSwitch.setVisibility(View.GONE);
            }
            else
            {
                Integer maxSellableShares = getMaxSellableShares();
                if (maxSellableShares == null || maxSellableShares == 0)
                {
                    mBuySellSwitch.setVisibility(View.GONE);
                }
                else
                {
                    mBuySellSwitch.setVisibility(View.VISIBLE);
                }
            }
            mBuySellSwitch.setChecked(isTransactionTypeBuy);
        }
    }

    public void displayTriggerButton()
    {
        if (mBtnAddTrigger != null)
        {
            if (securityAlertAssistant.isPopulated() && securityAlertAssistant.getAlertId(securityId) != null)
            {
                mBtnAddTrigger.setEnabled(true);
                mBtnAddTrigger.setImageResource(EDIT_ALERT_RES_ID);
            }
            else if (securityAlertAssistant.isPopulated() && securityAlertAssistant.getAlertId(securityId) == null)
            {
                mBtnAddTrigger.setEnabled(true);
                mBtnAddTrigger.setImageResource(ADD_ALERT_RES_ID);
            }
            else // TODO check if failed
            {
                mBtnAddTrigger.setEnabled(false);
                mBtnAddTrigger.setImageResource(INACTIVE_ALERT_RES_ID);
            }
        }
    }

    public void displayWatchlistButton()
    {
        if (mBtnWatchlist != null)
        {
            if (securityId == null || watchedList == null)
            {
                // TODO show disabled
                mBtnWatchlist.setEnabled(false);
            }
            else
            {
                mBtnWatchlist.setEnabled(true);
                mBtnWatchlist.setImageResource(watchedList.contains(securityId) ? R.drawable.active_watchlist : R.drawable.add_watchlist);
            }
        }
    }

    public void loadStockLogo()
    {
        if (mStockLogo != null)
        {
            if (mStockBgLogo != null)
            {
                mStockBgLogo.setVisibility(View.GONE);
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
                }
                catch (IllegalArgumentException e)
                {
                    Timber.e("Unknown Exchange %s", securityCompactDTO.exchange, e);
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
                resizeBackground(requestCreator, mStockBgLogo,new Callback()
                {
                    @Override public void onSuccess()
                    {
                        mStockBgLogo.setVisibility(View.VISIBLE);
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
                }
                catch (IllegalArgumentException e)
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

    protected void resizeBackground(RequestCreator requestCreator, ImageView imageView, Callback callback)
    {
        int width = mInfoFrame.getWidth();
        int height = mInfoFrame.getHeight();
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
        if (mTradeQuantityView != null)
        {
            mTradeQuantityView.setBuy(transactionTypeBuy);
            if (isTransactionTypeBuy)
            {
                mTradeQuantityView.setShareQuantity(mBuyQuantity);
            }
            else
            {
                mTradeQuantityView.setShareQuantity(mSellQuantity);
            }
        }
        if (mPricingBidAskView != null)
        {
            mPricingBidAskView.setBuy(transactionTypeBuy);
        }
        displaySlider();
        displayBuyButton();
        displayQuickPriceButtonSet();
        displayBuySellSwitch();
    }

    @Override protected void setRefreshingQuote(boolean refreshingQuote)
    {
        super.setRefreshingQuote(refreshingQuote);
        if (mPricingBidAskView != null)
        {
            mPricingBidAskView.setRefreshingQuote(refreshingQuote);
        }
        if (mTradeQuantityView != null)
        {
            mTradeQuantityView.setRefreshingQuote(refreshingQuote);
        }
    }

    @Override protected void prepareFreshQuoteHolder()
    {
        super.prepareFreshQuoteHolder();
        freshQuoteHolder.identifier = "BuySellFragment";
    }

    private void handleBtnAddCashPressed()
    {
        cancelOthersAndShowProductDetailList(ProductIdentifierDomain.DOMAIN_VIRTUAL_DOLLAR);
    }

    @Override public THUIBillingRequest getShowProductDetailRequest(ProductIdentifierDomain domain)
    {
        THUIBillingRequest billingRequest = super.getShowProductDetailRequest(domain);
        billingRequest.purchaseReportedListener = new THPurchaseReporter.OnPurchaseReportedListener()
        {
            @Override public void onPurchaseReported(int requestCode, ProductPurchase reportedPurchase, UserProfileDTO updatedUserProfile)
            {
                linkWith(updatedUserProfile, true);
                waitForPortfolioCompactListFetched(updatedUserProfile.getBaseKey());
            }

            @Override public void onPurchaseReportFailed(int requestCode, ProductPurchase reportedPurchase, BillingException error)
            {
            }
        };
        return billingRequest;
    }

    private void handleBtnAddTriggerClicked()
    {
        if (securityAlertAssistant.isPopulated())
        {
            Bundle args = new Bundle();
            args.putBundle(BaseAlertEditFragment.BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE, getApplicablePortfolioId().getArgs());
            AlertId alertId = securityAlertAssistant.getAlertId(securityId);
            if (alertId != null)
            {
                args.putBundle(AlertEditFragment.BUNDLE_KEY_ALERT_ID_BUNDLE, alertId.getArgs());
                getNavigator().pushFragment(AlertEditFragment.class, args);
            }
            else
            {
                args.putBundle(AlertCreateFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
                getNavigator().pushFragment(AlertCreateFragment.class, args);
            }
        }
        else if (securityAlertAssistant.isFailed())
        {
            THToast.show("We do not know if you already have an alert on it");
        }
        else
        {
            THToast.show("Try again in a moment");
        }
    }

    private void handleBtnWatchlistClicked()
    {
        if (securityId != null)
        {
            Bundle args = new Bundle();
            args.putBundle(WatchlistEditFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
            getNavigator().pushFragment(WatchlistEditFragment.class, args);
        }
        else
        {
            THToast.show(R.string.watchlist_not_enough_info);
        }
    }

    private void pushBuySellConfirmFragmentIn()
    {
        Bundle args = new Bundle();
        args.putBoolean(BuySellConfirmFragment.BUNDLE_KEY_IS_BUY, isTransactionTypeBuy);
        if (isTransactionTypeBuy)
        {
            if (mBuyQuantity == null)
            {
                Timber.e(new NullPointerException("mBuyQuantity cannot be null"), "");
                return;
            }
            args.putInt(BuySellConfirmFragment.BUNDLE_KEY_QUANTITY_BUY, mBuyQuantity);
        }
        else
        {
            if (mSellQuantity == null)
            {
                Timber.e(new NullPointerException("mSellQuantity cannot be null"), "");
                return;
            }
            args.putInt(BuySellConfirmFragment.BUNDLE_KEY_QUANTITY_SELL, mSellQuantity);
        }
        if (securityId == null)
        {
            Timber.e(new NullPointerException("securityId cannot be null"), "");
            return;
        }
        args.putBundle(BuySellConfirmFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());

        OwnedPortfolioId applicablePortfolioId = getApplicablePortfolioId();
        if (applicablePortfolioId != null)
        {
            args.putBundle(BuySellConfirmFragment.BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE, applicablePortfolioId.getArgs());
        }

        getNavigator().pushFragment(BuySellConfirmFragment.class, args);
    }

    private void showPortfolioSelector()
    {
        if (mPortfolioSelectorMenu != null)
        {
            mPortfolioSelectorMenu.show();
        }
    }

    private boolean selectDifferentPortfolio(MenuItem menuItem)
    {
        if (mSelectedPortfolio != null)
        {
            mSelectedPortfolio.setText(menuItem.getTitle());
        }

        linkWithApplicable((MenuOwnedPortfolioId) menuItem.getTitle(), true);
        return true;
    }

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>

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

            public void loadBg ()
            {
                if (mStockBgLogo != null && BuySellFragment.isUrlOk((String) mStockBgLogo.getTag(R.string.image_url)))
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

    private OnSeekBarChangeListener createSeekBarListener()
    {
        return new OnSeekBarChangeListener()
        {
            @Override public void onStopTrackingTouch(SeekBar seekBar)
            {
                if (mTradeQuantityView != null)
                {
                    mTradeQuantityView.setHighlightQuantity(false);
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar)
            {
                if (mTradeQuantityView != null)
                {
                    mTradeQuantityView.setHighlightQuantity(true);
                }
            }

            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if (fromUser)
                {
                    mQuantity = progress;

                    if (mTradeQuantityView != null)
                    {
                        mTradeQuantityView.setShareQuantity(mQuantity);
                    }

                    if (isTransactionTypeBuy)
                    {
                        linkWithBuyQuantity(progress, true);
                    }
                    else
                    {
                        linkWithSellQuantity(progress, true);
                    }
                }
                displayBuyButton();
            }
        };
    }

    private OnClickListener createBuyButtonListener()
    {
        return new OnClickListener()
        {
            @Override public void onClick(View v)
            {
                trackBuyClickEvent();
                pushBuySellConfirmFragmentIn();
            }
        };
    }

    private void trackBuyClickEvent()
    {
        localyticsSession.tagEvent(
                isTransactionTypeBuy ? LocalyticsConstants.Trade_Buy : LocalyticsConstants.Trade_Sell,
                securityId);
    }

    private QuickPriceButtonSet.OnQuickPriceButtonSelectedListener createQuickButtonSetListener()
    {
        return new QuickPriceButtonSet.OnQuickPriceButtonSelectedListener()
        {
            @Override public void onQuickPriceButtonSelected(double priceSelected)
            {
                if (quoteDTO == null)
                {
                    // Nothing to do
                }
                else
                {
                    Double priceRefCcy = quoteDTO.getPriceRefCcy(portfolioCompactDTO, isTransactionTypeBuy);
                    if (priceRefCcy == null || priceRefCcy == 0)
                    {
                        // Nothing to do
                    }
                    else
                    {
                        linkWithBuyOrSellQuantity((int) Math.floor(priceSelected / priceRefCcy),
                                true);
                    }
                }
                displaySlider();
                displayTradeQuantityView();
            }
        };
    }

    private void pushStockInfoFragmentIn()
    {
        Bundle args = new Bundle();
        args.putBundle(StockInfoFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, this.securityId.getArgs());
        if (providerId != null)
        {
            args.putBundle(StockInfoFragment.BUNDLE_KEY_PROVIDER_ID_BUNDLE, this.providerId.getArgs());
        }
        getNavigator().pushFragment(StockInfoFragment.class, args);
    }

    private BroadcastReceiver chartImageButtonClickReceiver = new BroadcastReceiver()
    {
        @Override public void onReceive(Context context, Intent intent)
        {
            pushStockInfoFragmentIn();
        }
    };
    //</editor-fold>

    //<editor-fold desc="SecurityAlertAssistant.OnPopulatedListener">
    @Override public void onPopulateFailed(SecurityAlertAssistant securityAlertAssistant, Throwable error)
    {
        Timber.e("There was an error getting the alert ids", error);
        displayTriggerButton();
    }

    @Override public void onPopulated(SecurityAlertAssistant securityAlertAssistant)
    {
        displayTriggerButton();
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

    protected class BuySellFreshQuoteListener extends AbstractBuySellFreshQuoteListener
    {
        @Override public void onMilliSecToRefreshQuote(long milliSecToRefresh)
        {
            if (mQuoteRefreshProgressBar != null)
            {
                mQuoteRefreshProgressBar.setProgress((int) (milliSecToRefresh / MILLISEC_QUOTE_COUNTDOWN_PRECISION));
            }
        }
    }

    protected class BuySellPortfolioCompactListMilestoneListener extends BasePurchaseManagementPortfolioCompactListRetrievedListener
    {
        @Override public void onComplete(Milestone milestone)
        {
            super.onComplete(milestone);
            buildUsedMenuPortfolios();
            setInitialSellQuantityIfCan();
            displayQuickPriceButtonSet();
            displaySlider();
            displayTradeQuantityView();
        }

        @Override public void onFailed(Milestone milestone, Throwable throwable)
        {
            Timber.e("Failed to fetch list of compact portfolio", throwable);
            THToast.show(R.string.error_fetch_portfolio_list_info);
        }
    }

    protected class BuySellUserWatchlistCacheListener implements DTOCache.Listener<UserBaseKey, SecurityIdList>
    {
        public BuySellUserWatchlistCacheListener()
        {
        }

        @Override public void onDTOReceived(UserBaseKey key, SecurityIdList value, boolean fromCache)
        {
            linkWithWatchlist(value, true);
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            Timber.e("Failed to fetch list of watch list items", error);
            THToast.show(R.string.error_fetch_portfolio_list_info);
        }
    }
}
