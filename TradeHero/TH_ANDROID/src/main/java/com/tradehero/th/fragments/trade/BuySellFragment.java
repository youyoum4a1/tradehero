package com.tradehero.th.fragments.trade;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
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
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.squareup.picasso.Cache;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;
import com.squareup.picasso.UrlConnectionDownloader;
import com.tradehero.common.cache.LruMemFileCache;
import com.tradehero.common.graphics.AbstractSequentialTransformation;
import com.tradehero.common.graphics.FastBlurTransformation;
import com.tradehero.common.graphics.GrayscaleTransformation;
import com.tradehero.common.graphics.RoundedCornerTransformation;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.common.milestone.Milestone;
import com.tradehero.common.thread.KnownExecutorServices;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.ImageViewThreadSafe;
import com.tradehero.th.R;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.WarrantDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.alert.AlertCreateFragment;
import com.tradehero.th.fragments.alert.AlertEditFragment;
import com.tradehero.th.fragments.alert.BaseAlertEditFragment;
import com.tradehero.th.fragments.billing.THIABUserInteractor;
import com.tradehero.th.fragments.security.BuySellBottomStockPagerAdapter;
import com.tradehero.th.fragments.security.StockInfoFragment;
import com.tradehero.th.fragments.trade.view.PricingBidAskView;
import com.tradehero.th.fragments.trade.view.QuickPriceButtonSet;
import com.tradehero.th.fragments.trade.view.TradeQuantityView;
import com.tradehero.th.models.alert.SecurityAlertAssistant;
import com.tradehero.th.models.portfolio.MenuOwnedPortfolioId;
import com.tradehero.th.models.provider.ProviderSpecificResourcesDTO;
import com.tradehero.th.models.provider.ProviderSpecificResourcesFactory;
import com.tradehero.th.models.security.WarrantSpecificKnowledgeFactory;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListRetrievedMilestone;
import com.viewpagerindicator.PageIndicator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.inject.Inject;

public class BuySellFragment extends AbstractBuySellFragment
    implements SecurityAlertAssistant.OnPopulatedListener
{
    private final static String TAG = BuySellFragment.class.getSimpleName();
    public static final String EVENT_CHART_IMAGE_CLICKED = BuySellFragment.class.getName() + ".chartButtonClicked";

    public final static int ADD_ALERT_RES_ID = R.drawable.add_alert;
    public final static int EDIT_ALERT_RES_ID = R.drawable.active_alert;
    public final static int INACTIVE_ALERT_RES_ID = R.drawable.alert_inactive;
    public final static float BUY_BUTTON_DISABLED_ALPHA = 0.5f;

    private ToggleButton mBuySellSwitch;

    private ImageViewThreadSafe mStockBgLogo;
    private ImageViewThreadSafe mStockLogo;

    private View mSelectedPortfolioContainer;
    private TextView mSelectedPortfolio;
    private PopupMenu mPortfolioSelectorMenu;
    private Set<MenuOwnedPortfolioId> usedMenuOwnedPortfolioIds;

    private TextView mStockName;

    private ProgressBar mQuoteRefreshProgressBar;
    private FrameLayout mInfoFrame;
    private PricingBidAskView mPricingBidAskView;
    private TradeQuantityView mTradeQuantityView;
    private QuickPriceButtonSet mQuickPriceButtonSet;
    private PageIndicator mBottomPagerIndicator;
    private ViewPager mBottomViewPager;

    private Button mBuyBtn;
    private SeekBar mSlider;
    private ImageButton mBtnAddCash;
    private ImageButton mBtnAddTrigger;
    private ImageView mBtnWatchlist;

    protected SecurityAlertAssistant securityAlertAssistant;
    @Inject protected PortfolioCache portfolioCache;
    @Inject protected PortfolioCompactListCache portfolioCompactListCache;
    @Inject protected PortfolioCompactListRetrievedMilestone portfolioCompactListRetrievedMilestone;
    @Inject protected PortfolioCompactCache portfolioCompactCache;
    @Inject protected ProviderSpecificResourcesFactory providerSpecificResourcesFactory;
    @Inject protected WarrantSpecificKnowledgeFactory warrantSpecificKnowledgeFactory;
    protected Milestone.OnCompleteListener portfolioCompactListMilestoneListener;

    int mQuantity = 0;
    int volume = 0;
    int avgDailyVolume = 0;

    private Bundle desiredArguments;

    private Picasso mPicasso;
    private Transformation foregroundTransformation;
    private Transformation backgroundTransformation;
    private BuySellBottomStockPagerAdapter bottomViewPagerAdapter;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        securityAlertAssistant = new SecurityAlertAssistant();
        portfolioCompactListMilestoneListener = new BuySellPortfolioCompactListMilestoneListener();
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

        mQuoteRefreshProgressBar = (ProgressBar) view.findViewById(R.id.quote_refresh_countdown);
        if (mQuoteRefreshProgressBar != null)
        {
            mQuoteRefreshProgressBar.setMax((int) (MILLISEC_QUOTE_REFRESH / MILLISEC_QUOTE_COUNTDOWN_PRECISION));
            mQuoteRefreshProgressBar.setProgress(mQuoteRefreshProgressBar.getMax());
        }

        mStockBgLogo = (ImageViewThreadSafe) view.findViewById(R.id.stock_bg_logo);
        mStockLogo = (ImageViewThreadSafe) view.findViewById(R.id.stock_logo);
        //mStockChart = (ImageView) view.findViewById(R.id.stock_chart);


        mSelectedPortfolioContainer = view.findViewById(R.id.portfolio_selector_container);
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

        mSelectedPortfolio = (TextView) view.findViewById(R.id.portfolio_selected);
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

        mStockName = (TextView) view.findViewById(R.id.stock_name);

        mInfoFrame = (FrameLayout) view.findViewById(R.id.chart_frame);

        mBtnWatchlist = (ImageView) view.findViewById(R.id.btn_watch_list);
        if (mBtnWatchlist != null)
        {
            mBtnWatchlist.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    //TODO
                }
            });
        }

        mPricingBidAskView = (PricingBidAskView) view.findViewById(R.id.pricing_bid_ask_view);
        mTradeQuantityView = (TradeQuantityView) view.findViewById(R.id.trade_quantity_view);

        mQuickPriceButtonSet = (QuickPriceButtonSet) view.findViewById(R.id.quick_price_button_set);
        if (mQuickPriceButtonSet != null)
        {
            mQuickPriceButtonSet.setListener(createQuickButtonSetListener());
            mQuickPriceButtonSet.addButton(R.id.toggle5k);
            mQuickPriceButtonSet.addButton(R.id.toggle10k);
            mQuickPriceButtonSet.addButton(R.id.toggle25k);
            mQuickPriceButtonSet.addButton(R.id.toggle50k);
        }

        mSlider = (SeekBar) view.findViewById(R.id.seekBar);
        if (mSlider != null)
        {
            mSlider.setOnSeekBarChangeListener(createSeekBarListener());
        }

        mBtnAddCash = (ImageButton) view.findViewById(R.id.btn_add_cash);
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

        mBtnAddTrigger = (ImageButton) view.findViewById(R.id.btn_add_trigger);
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

        mBuyBtn = (Button) view.findViewById(R.id.btn_buy);
        if (mBuyBtn != null)
        {
            mBuyBtn.setOnClickListener(createBuyButtonListener());
        }

        mBottomViewPager = (ViewPager) view.findViewById(R.id.trade_bottom_pager);
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

        if (foregroundTransformation == null)
        {
            foregroundTransformation = new WhiteToTransparentTransformation();
        }
        if (backgroundTransformation == null)
        {
            backgroundTransformation = new AbstractSequentialTransformation()
            {
                @Override public String key()
                {
                    return "toRoundedGaussianGrayscale11";
                }
            };
            ((AbstractSequentialTransformation) backgroundTransformation).add(new GrayscaleTransformation());
            ((AbstractSequentialTransformation) backgroundTransformation).add(new FastBlurTransformation(10));
            ((AbstractSequentialTransformation) backgroundTransformation).add(new RoundedCornerTransformation(
                    getResources().getDimensionPixelSize(R.dimen.trending_grid_item_corner_radius),
                    getResources().getColor(R.color.black)));
        }
        if (mPicasso == null)
        {
            Cache lruFileCache = null;
            try
            {
                lruFileCache = new LruMemFileCache(getActivity());
            }
            catch (Exception e)
            {
                THLog.e(TAG, "Failed to create LRU", e);
            }

            mPicasso = new Picasso.Builder(getActivity())
                    .downloader(new UrlConnectionDownloader(getActivity()))
                    .memoryCache(lruFileCache)
                    .build();
            //mPicasso.setDebugging(true);
        }
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        securityAlertAssistant.setOnPopulatedListener(this);
    }

    @Override protected void createUserInteractor()
    {
        userInteractor = new BuySellTHIABUserInteractor();
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.buy_sell_menu, menu);
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        displayExchangeSymbol(actionBar);
        //displayMarketClose(menu);

        //mBuySellSwitch = (ToggleButton) menu.findItem(R.id.trade_menu_toggle_mode).getActionView();
        // TODO do styling in styles.xml
        //mBuySellSwitch.setTextOn(getString(R.string.switch_buy));
        //mBuySellSwitch.setTextOff(getString(R.string.switch_sell));
        //mBuySellSwitch.setTextColor(getResources().getColor(R.color.white));
        //mBuySellSwitch.setOnCheckedChangeListener(createBuySellListener());
        //displayBuySellSwitch();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);

        com.actionbarsherlock.view.MenuItem menuElements = menu.findItem(R.id.menu_elements_buy_sell);

        marketCloseIcon = (ImageView) menuElements.getActionView().findViewById(R.id.market_status);
        if (marketCloseIcon != null)
        {
            marketCloseIcon.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    handleMarketCloseClicked();
                }
            });
        }

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

    @Override public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.buy_sell_info:
                showInfoDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onDestroyOptionsMenu()
    {
        //THLog.d(TAG, "onDestroyOptionsMenu");
        if (marketCloseIcon != null)
        {
            marketCloseIcon.setOnClickListener(null);
        }
        marketCloseIcon = null;

        if (mBuySellSwitch != null)
        {
            mBuySellSwitch.setOnCheckedChangeListener(null);
        }
        mBuySellSwitch = null;
        super.onDestroyOptionsMenu();
    }
    //</editor-fold>

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

        userInteractor.waitForSkuDetailsMilestoneComplete(new Runnable()
        {
            @Override public void run()
            {
                display();
            }
        });

        detachPortfolioCompactMilestone();
        portfolioCompactListRetrievedMilestone = new PortfolioCompactListRetrievedMilestone(currentUserId);
        portfolioCompactListRetrievedMilestone.setOnCompleteListener(portfolioCompactListMilestoneListener);
        portfolioCompactListRetrievedMilestone.launch();
    }

    @Override public void onPause()
    {
        LocalBroadcastManager.getInstance(getActivity())
                .unregisterReceiver(chartImageButtonClickReceiver);
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        //THLog.d(TAG, "onDestroyView");
        detachPortfolioCompactMilestone();

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

        if (mBuyBtn != null)
        {
            mBuyBtn.setOnClickListener(null);
        }
        mBuyBtn = null;

        bottomViewPagerAdapter = null;
        mBottomViewPager = null;

        mBottomPagerIndicator = null;
        mBottomViewPager = null;
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        portfolioCompactListMilestoneListener = null;
        securityAlertAssistant = null;
        super.onDestroy();
    }

    protected void detachPortfolioCompactMilestone()
    {
        if (portfolioCompactListRetrievedMilestone != null)
        {
            portfolioCompactListRetrievedMilestone.setOnCompleteListener(null);
        }
        portfolioCompactListRetrievedMilestone = null;
    }

    @Override public void linkWith(SecurityId securityId, boolean andDisplay)
    {
        super.linkWith(securityId, andDisplay);
    }

    @Override public void linkWith(SecurityCompactDTO securityCompactDTO, boolean andDisplay)
    {
        super.linkWith(securityCompactDTO, andDisplay);
        buildUsedMenuPortfolios();
        if (andDisplay)
        {
            displayMarketClose();
            displaySelectedPortfolioContainer();
            displayPortfolioSelectorMenu();
            displaySelectedPortfolio();
            displayPricingBidAskView();
            displayTradeQuantityView();
            displayStockName();
            displayBottomViewPager();
            storeImageUrlInImageViews();
            loadImages();
        }
    }

    @Override public void linkWith(final SecurityPositionDetailDTO securityPositionDetailDTO, boolean andDisplay)
    {
        super.linkWith(securityPositionDetailDTO, andDisplay);

        setInitialSellQuantityIfCan();
        flipToBuyIfCannotSell();

        if (andDisplay)
        {
            displaySelectedPortfolioContainer();
            displayPortfolioSelectorMenu();
            displaySelectedPortfolio();
            displayPricingBidAskView();
            displayTradeQuantityView();
            displayQuickPriceButtonSet();
            displaySlider();
            displayBuySellSwitch();
        }
    }

    @Override public void linkWith(UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        super.linkWith(userProfileDTO, andDisplay);
        setInitialBuyQuantityIfCan();
        setInitialSellQuantityIfCan();
        flipToBuyIfCannotSell();
        if (andDisplay)
        {
            displayTradeQuantityView();
            displayQuickPriceButtonSet();
            displaySlider();
        }
    }

    @Override protected void linkWith(QuoteDTO quoteDTO, boolean andDisplay)
    {
        super.linkWith(quoteDTO, andDisplay);
        setInitialBuyQuantityIfCan();
        setInitialSellQuantityIfCan();
        flipToBuyIfCannotSell();
        if (andDisplay)
        {
            displayPricingBidAskView();
            displayTradeQuantityView();
            displayQuickPriceButtonSet();
            displaySlider();
        }
    }

    protected void setInitialBuyQuantityIfCan()
    {
        if (mBuyQuantity == null)
        {
            Integer maxPurchasableShares = getMaxPurchasableShares();
            if (maxPurchasableShares != null)
            {
                mBuyQuantity = (int) Math.ceil(((double) maxPurchasableShares) / 2);
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
                mSellQuantity = maxSellableShares;
            }
        }
    }

    protected void flipToBuyIfCannotSell()
    {
        Integer maxSellableShares = getMaxSellableShares();
        if (maxSellableShares != null && maxSellableShares == 0)
        {
            // Nothing to sell
            setTransactionTypeBuy(true);
        }
    }

    //<editor-fold desc="Display Methods">
    protected void buildUsedMenuPortfolios()
    {
        OwnedPortfolioId defaultOwnedPortfolioId = portfolioCompactListCache.getDefaultPortfolio(currentUserId.toUserBaseKey());

        if (defaultOwnedPortfolioId != null && securityCompactDTO != null)
        {
            Set<MenuOwnedPortfolioId> newMenus = new TreeSet<>();

            PortfolioCompactDTO defaultPortfolioCompactDTO = portfolioCompactCache.get(defaultOwnedPortfolioId.getPortfolioId());
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
                PortfolioCompactDTO portfolioCompactDTO = portfolioCompactCache.get(ownedPortfolioId.getPortfolioId());
                if (portfolioCompactDTO != null && portfolioCompactDTO.providerId != null && providerId != null &&
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

        if (securityCompactDTO != null && !TextUtils.isEmpty(securityCompactDTO.yahooSymbol))
        {
            //mImageLoader.DisplayImage(String.format(Config.getTrendingChartUrl(), trend.getYahooSymbol()),
            //        mStockChart);
        }

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
        displayBuyButton();
        displayBottomViewPager();
        displayStockName();
        displayQuickPriceButtonSet();
        displaySlider();
        displayTriggerButton();
        storeImageUrlInImageViews();
        loadImages();
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
                    mPortfolioSelectorMenu.getMenu().add(Menu.NONE, Menu.NONE, Menu.NONE, menuOwnedPortfolioId);
                }
            }
        }
    }

    public void displaySelectedPortfolio()
    {
        if (mSelectedPortfolio != null)
        {
            if (usedMenuOwnedPortfolioIds != null && usedMenuOwnedPortfolioIds.size() > 0)
            {
                OwnedPortfolioId currentOwnedPortfolioId = getApplicablePortfolioId();
                MenuOwnedPortfolioId chosen = null;

                final Iterator<MenuOwnedPortfolioId> iterator = usedMenuOwnedPortfolioIds.iterator();
                MenuOwnedPortfolioId lastElement = null;
                while (iterator.hasNext())
                {
                    lastElement = iterator.next();
                    if (currentOwnedPortfolioId.equals(lastElement))
                    {
                        chosen = lastElement;
                    }
                }
                if (chosen == null)
                {
                    chosen = lastElement;
                }

                mSelectedPortfolio.setText(chosen);
            }
        }
    }

    public void displayPricingBidAskView()
    {
        if (mPricingBidAskView != null)
        {
            if (securityPositionDetailDTO != null)
            {
                mPricingBidAskView.display(securityPositionDetailDTO);
            }
            else
            {
                mPricingBidAskView.display(securityCompactDTO);
            }

            mPricingBidAskView.display(quoteDTO);
        }
    }

    public void displayTradeQuantityView()
    {
        if (mTradeQuantityView != null)
        {
            OwnedPortfolioId applicablePortfolioId = getApplicablePortfolioId();
            if (applicablePortfolioId != null && applicablePortfolioId.portfolioId != null)
            {
                mTradeQuantityView.linkWith(applicablePortfolioId.getPortfolioId(), true);
            }
            if (securityPositionDetailDTO != null)
            {
                mTradeQuantityView.linkWith(securityPositionDetailDTO, true);
            }
            else
            {
                mTradeQuantityView.linkWith(securityCompactDTO, true);
            }

            mTradeQuantityView.linkWith(quoteDTO, true);
            mTradeQuantityView.linkWith(userProfileDTO, true);

            if (isTransactionTypeBuy)
            {
                mTradeQuantityView.setShareQuantity(mBuyQuantity);
            }
            else
            {
                mTradeQuantityView.setShareQuantity(mSellQuantity);
            }
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
                mBuyBtn.setText(isOk ? R.string.button_buy : R.string.button_cannot_buy);
            }
            else
            {
                boolean isOk = mSellQuantity != null && mSellQuantity > 0;
                mBuyBtn.setEnabled(isOk);
                mBuyBtn.setText(isOk ? R.string.button_sell : R.string.button_cannot_sell);
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
        if (mQuickPriceButtonSet != null)
        {
            if (quoteDTO == null)
            {
                mQuickPriceButtonSet.setEnabled(false);
            }
            else if (isTransactionTypeBuy && quoteDTO.ask == null)
            {
                mQuickPriceButtonSet.setEnabled(false);
            }
            else if (isTransactionTypeBuy)
            {
                mQuickPriceButtonSet.setEnabled(true);
                if (this.userProfileDTO != null && userProfileDTO.portfolio != null)
                {
                    mQuickPriceButtonSet.setMaxPrice(userProfileDTO.portfolio.cashBalance);
                }
            }
            else if (!isTransactionTypeBuy && (quoteDTO.bid == null || quoteDTO.toUSDRate == null))
            {
                mQuickPriceButtonSet.setEnabled(false);
            }
            else if (!isTransactionTypeBuy)
            {
                mQuickPriceButtonSet.setEnabled(true);
                Integer maxSellableShares = getMaxSellableShares();
                if (maxSellableShares != null)
                {
                    mQuickPriceButtonSet.setMaxPrice(maxSellableShares * quoteDTO.ask * quoteDTO.toUSDRate);
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
            if (securityPositionDetailDTO == null || securityPositionDetailDTO.positions == null || securityPositionDetailDTO.positions.size() == 0 ||
                    getApplicablePortfolioId() == null)
            {
                mBuySellSwitch.setVisibility(View.GONE);
            }
            else
            {
                // TODO handle the case when we have move than 1 position
                Integer shareCount = securityPositionDetailDTO.positions.getMaxSellableShares(this.quoteDTO,
                        getApplicablePortfolioId().getPortfolioId(), this.userProfileDTO);
                if (shareCount == null || shareCount == 0)
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

    private void storeImageUrlInImageViews()
    {
        if (mStockLogo != null && securityCompactDTO != null)
        {
            mStockLogo.setTag(R.string.image_url, this.securityCompactDTO.imageBlobUrl);
        }
        if (mStockBgLogo != null && securityCompactDTO!= null)
        {
            mStockBgLogo.setTag(R.string.image_url, this.securityCompactDTO.imageBlobUrl);
        }
    }

    public void loadImages ()
    {
        final Callback loadIntoBg = createLogoReadyCallback();

        if (isMyUrlOk())
        {
            loadImageInTarget(mStockLogo, foregroundTransformation);
            // Launching the bg like this will result in double downloading the file.
            if (mInfoFrame != null)
            {
                loadImageInTarget(mStockBgLogo, backgroundTransformation, mInfoFrame.getMeasuredWidth(), mInfoFrame.getMeasuredHeight());
            }
        }
        else
        {
            // These ensure that views with a missing image do not receive images from elsewhere
            if (mStockLogo != null && this.securityCompactDTO != null)
            {
                int logoId = this.securityCompactDTO.getExchangeLogoId();
                if (logoId != 0)
                {
                    mStockLogo.setImageResource(logoId);
                }
            }
            else if (mStockLogo != null)
            {
                mPicasso.load((String) null)
                        .placeholder(R.drawable.default_image)
                        .error(R.drawable.default_image)
                        .into(mStockLogo);
            }

            if (mStockLogo != null)
            {
                mPicasso.load((String) null)
                        .placeholder(R.drawable.default_image)
                        .error(R.drawable.default_image)
                        .into(mStockLogo);
            }
        }
    }

    private void loadImageInTarget(final ImageView target, final Transformation t)
    {
        loadImageInTarget(target, t, 0, 0);
    }

    private void loadImageInTarget(final ImageView target, final Transformation t, final int resizeToWidth, final int resizeToHeight)
    {
        KnownExecutorServices.getCacheExecutor().submit(new Runnable()
        {
            @Override public void run()
            {
                if (target != null && target.getTag(R.string.image_url) != null)
                {
                    RequestCreator requestCreator = mPicasso.load(target.getTag(R.string.image_url).toString())
                            .placeholder(R.drawable.default_image)
                            .error(R.drawable.default_image);

                    if (resizeToWidth > 0 && resizeToHeight > 0)
                    {
                        requestCreator = requestCreator.resize(resizeToWidth, resizeToHeight).centerCrop();
                    }

                    requestCreator.transform(t)
                            .into(target);
                }
            }
        });
    }
    //</editor-fold>

    public boolean isMyUrlOk()
    {
        return (securityPositionDetailDTO != null) && securityPositionDetailDTO.security != null &&
                isUrlOk(securityPositionDetailDTO.security.imageBlobUrl);
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
            displayTradeQuantityView();
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
        userInteractor.conditionalPopBuyVirtualDollars();
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
                navigator.pushFragment(AlertEditFragment.class, args);
            }
            else
            {
                args.putBundle(AlertCreateFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
                navigator.pushFragment(AlertCreateFragment.class, args);
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

    private void pushBuySellConfirmFragmentIn()
    {
        Bundle args = new Bundle();
        args.putBoolean(BuySellConfirmFragment.BUNDLE_KEY_IS_BUY, isTransactionTypeBuy);
        if (isTransactionTypeBuy)
        {
            args.putInt(BuySellConfirmFragment.BUNDLE_KEY_QUANTITY_BUY, mBuyQuantity);
        }
        else
        {
            args.putInt(BuySellConfirmFragment.BUNDLE_KEY_QUANTITY_SELL, mSellQuantity);
        }
        args.putBundle(BuySellConfirmFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());

        OwnedPortfolioId applicablePortfolioId = getApplicablePortfolioId();
        if (applicablePortfolioId != null)
        {
            args.putBundle(BuySellConfirmFragment.BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE, applicablePortfolioId.getArgs());
        }

        navigator.pushFragment(BuySellConfirmFragment.class, args);
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

        OwnedPortfolioId applicableOwnedPortfolioId = (MenuOwnedPortfolioId) menuItem.getTitle();
        userInteractor.setApplicablePortfolioId(applicableOwnedPortfolioId);
        return true;
    }

    private void showInfoDialog()
    {
        alertDialogUtil.popCustom(getActivity(), R.layout.tutorial_buy_sell);
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
                    THLog.i(TAG, "Loading Bg for " + mStockBgLogo.getTag(R.string.image_url));
                    mPicasso.load((String) mStockBgLogo.getTag(R.string.image_url))
                            .placeholder(R.drawable.default_image)
                            .error(R.drawable.default_image)
                            .resize(mStockBgLogo.getWidth(), mStockBgLogo.getHeight())
                            .centerInside()
                            .transform(foregroundTransformation)
                            .into(mStockBgLogo);
                }
                else if (mStockBgLogo != null && securityPositionDetailDTO != null && securityPositionDetailDTO.security != null)
                {
                    int logoId = securityPositionDetailDTO.security.getExchangeLogoId();
                    if (logoId != 0)
                    {
                        mPicasso.load(logoId)
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
                        mBuyQuantity = progress;
                    }
                    else
                    {
                        mSellQuantity = progress;
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
            @Override
            public void onClick(View v)
            {
                pushBuySellConfirmFragmentIn();
                return;
            }
        };
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
                else if (isTransactionTypeBuy && quoteDTO.ask != null && quoteDTO.toUSDRate != null)
                {
                    mBuyQuantity = (int) Math.floor(priceSelected / (quoteDTO.ask * quoteDTO.toUSDRate));
                }
                else if (!isTransactionTypeBuy && quoteDTO.bid != null && quoteDTO.toUSDRate != null)
                {
                    mSellQuantity = (int) Math.floor(priceSelected / (quoteDTO.bid * quoteDTO.toUSDRate));
                }
                else
                {
                    // Nothing to do
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
        navigator.pushFragment(StockInfoFragment.class, args);
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
        THLog.e(TAG, "There was an error getting the alert ids", error);
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

    public class BuySellTHIABUserInteractor extends THIABUserInteractor
    {
        public BuySellTHIABUserInteractor()
        {
            super();
        }

        @Override protected void handleShowSkuDetailsMilestoneComplete()
        {
            super.handleShowSkuDetailsMilestoneComplete();
            // Like this we retest for the possibility to buy and sell
            if (securityPositionDetailDTO != null)
            {
                BuySellFragment.this.linkWith(securityPositionDetailDTO, true);
            }
        }

        @Override protected void handleShowSkuDetailsMilestoneFailed(Throwable throwable)
        {
            super.handleShowSkuDetailsMilestoneFailed(throwable);
            THLog.e(TAG, "Failed to load the sku details", throwable);
        }
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

    protected class BuySellPortfolioCompactListMilestoneListener implements Milestone.OnCompleteListener
    {
        @Override public void onComplete(Milestone milestone)
        {
            buildUsedMenuPortfolios();
            setInitialSellQuantityIfCan();
            displayQuickPriceButtonSet();
            displaySlider();
            displayTradeQuantityView();
        }

        @Override public void onFailed(Milestone milestone, Throwable throwable)
        {
            THLog.e(TAG, "Failed to fetch list of compact portfolio", throwable);
            THToast.show(R.string.error_fetch_portfolio_list_info);
        }
    }
}
