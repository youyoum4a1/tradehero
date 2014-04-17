package com.tradehero.th.fragments.trade;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.tradehero.th.api.security.TransactionFormDTO;
import com.tradehero.th.api.security.WarrantDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.PurchaseReporter;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.alert.AlertCreateFragment;
import com.tradehero.th.fragments.alert.AlertEditFragment;
import com.tradehero.th.fragments.alert.BaseAlertEditFragment;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.security.BuySellBottomStockPagerAdapter;
import com.tradehero.th.fragments.security.StockInfoFragment;
import com.tradehero.th.fragments.security.WatchlistEditFragment;
import com.tradehero.th.fragments.trade.view.QuickPriceButtonSet;
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
import com.tradehero.th.utils.DateUtils;
import com.tradehero.th.utils.ForWeChat;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.SocialSharer;
import com.tradehero.th.utils.THSignedNumber;
import com.tradehero.th.wxapi.WXMessageType;
import com.viewpagerindicator.PageIndicator;
import java.util.*;
import dagger.Lazy;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.inject.Inject;
import timber.log.Timber;

public class BuySellFragment extends AbstractBuySellFragment
    implements SecurityAlertAssistant.OnPopulatedListener, WithTutorial,
        ViewPager.OnPageChangeListener
{
    public static final String EVENT_CHART_IMAGE_CLICKED = BuySellFragment.class.getName() + ".chartButtonClicked";

    public static final int MS_DELAY_FOR_BG_IMAGE  = 200;

    @InjectView(R.id.stock_bg_logo) protected ImageView mStockBgLogo;
    @InjectView(R.id.stock_logo) protected ImageView mStockLogo;

    @InjectView(R.id.portfolio_selector_container) protected View mSelectedPortfolioContainer;
    @InjectView(R.id.portfolio_selected) protected TextView mSelectedPortfolio;

    @InjectView(R.id.stock_name) protected TextView mStockName;
    @InjectView(R.id.exchange_symbol) protected TextView mExchangeSymbol;
    @InjectView(R.id.market_icon) protected ImageView mMarketIcon;
    @InjectView(R.id.buy_price) protected TextView mBuyPrice;
    @InjectView(R.id.sell_price) protected TextView mSellPrice;
    @InjectView(R.id.vprice_as_of) protected TextView mVpriceAsOf;
    @InjectView(R.id.info) protected TextView mInfoTextView;
    @InjectView(R.id.discussions) protected TextView mDiscussTextView;
    @InjectView(R.id.news) protected TextView mNewsTextView;

    //for dialog
    AlertDialog mBuySellDialog;
    TextView mTradeValueTextView;
    TextView mCashLeftValueTextView;
    TextView mQuantityTextView;
    TextView mCashLeftTextView;
    SeekBar mSlider;
    QuickPriceButtonSet mQuickPriceButtonSet;
    EditText mCommentsEditText;
    ToggleButton mBtnShareFacebook;
    ToggleButton mBtnShareTwitter;
    ToggleButton mBtnShareLinkedIn;
    ToggleButton mBtnShareWeChat;
    ToggleButton mBtnLocation;
    ToggleButton mBtnSharePublic;
    Button mConfirmButton;
    private PushPortfolioFragmentRunnable pushPortfolioFragmentRunnable = null;
    private BaseBuySellAsyncTask buySellTask;
    private boolean isBuying = false;
    private boolean isSelling = false;
    private boolean publishToFb = false;
    private boolean publishToTw = false;
    private boolean publishToLi = false;
    private boolean publishToWe = false;
    private boolean shareLocation = false;
    private boolean sharePublic = false;

    @InjectView(R.id.quote_refresh_countdown) protected ProgressBar mQuoteRefreshProgressBar;
    @InjectView(R.id.chart_frame) protected RelativeLayout mInfoFrame;
    @InjectView(R.id.trade_bottom_pager) protected ViewPager mBottomViewPager;

    @InjectView(R.id.btn_buy) protected Button mBuyBtn;
    @InjectView(R.id.btn_sell) protected Button mSellBtn;
    @InjectView(R.id.btn_add_trigger) protected Button mBtnAddTrigger;
    @InjectView(R.id.btn_add_watch_list) protected Button mBtnAddWatchlist;

    @Inject PortfolioCache portfolioCache;
    @Inject PortfolioCompactCache portfolioCompactCache;
    @Inject THLocalyticsSession localyticsSession;
    @Inject ProgressDialogUtil progressDialogUtil;

    @Inject UserWatchlistPositionCache userWatchlistPositionCache;
    @Inject WatchlistPositionCache watchlistPositionCache;
    @Inject ProviderSpecificResourcesFactory providerSpecificResourcesFactory;
    @Inject WarrantSpecificKnowledgeFactory warrantSpecificKnowledgeFactory;
    @Inject Picasso picasso;
    @Inject @ForWeChat Lazy<SocialSharer> wechatSharerLazy;

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

        if (mBtnAddWatchlist != null)
        {
            mBtnAddWatchlist.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    handleBtnWatchlistClicked();
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
            mBuyBtn.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    trackBuyClickEvent();
                    //pushBuySellConfirmFragmentIn();
                    isTransactionTypeBuy = true;
                    showBuySellDialog();
                }
            });
        }
        if (mSellBtn != null)
        {
            mSellBtn.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    trackBuyClickEvent();
                    //pushBuySellConfirmFragmentIn();
                    isTransactionTypeBuy = false;
                    showBuySellDialog();
                }
            });
        }

        if (bottomViewPagerAdapter == null)
        {
            bottomViewPagerAdapter = new BuySellBottomStockPagerAdapter(getActivity(), getChildFragmentManager());
        }
        if (mBottomViewPager != null)
        {
            mBottomViewPager.setAdapter(bottomViewPagerAdapter);
            mBottomViewPager.setOnPageChangeListener(this);
        }

        selectPage(0);
        if (mInfoTextView != null)
        {
            mInfoTextView.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    if (mBottomViewPager != null)
                    {
                        mBottomViewPager.setCurrentItem(0, true);
                    }
                }
            });
        }
        if (mDiscussTextView != null)
        {
            mDiscussTextView.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    if (mBottomViewPager != null)
                    {
                        mBottomViewPager.setCurrentItem(1, true);
                    }
                }
            });
        }
        if (mNewsTextView != null)
        {
            mNewsTextView.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    if (mBottomViewPager != null)
                    {
                        mBottomViewPager.setCurrentItem(2, true);
                    }
                }
            });
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
        //inflater.inflate(R.menu.buy_sell_menu_toggle, menu);
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                | ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE);
        displayExchangeSymbol(actionBar);
    }

    @Override public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);

        displayActionBarElements();
    }

    @Override public void onDestroyOptionsMenu()
    {
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

        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(chartImageButtonClickReceiver, new IntentFilter(EVENT_CHART_IMAGE_CLICKED));

        securityAlertAssistant.setUserBaseKey(currentUserId.toUserBaseKey());
        securityAlertAssistant.populate();

        DashboardNavigator dn = getDashboardNavigator();
        if (dn != null)
        {
            dn.hideTabBar();
        }
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

        if (mBtnAddTrigger != null)
        {
            mBtnAddTrigger.setEnabled(false);
            mBtnAddTrigger.setOnClickListener(null);
        }
        mBtnAddTrigger = null;

        if (mBtnAddWatchlist != null)
        {
            mBtnAddWatchlist.setEnabled(false);
            mBtnAddWatchlist.setOnClickListener(null);
        }
        mBtnAddWatchlist = null;

        if (mBuyBtn != null)
        {
            mBuyBtn.setOnClickListener(null);
        }
        mBuyBtn = null;

        bottomViewPagerAdapter = null;
        mBottomViewPager = null;
        mBottomPagerIndicator = null;

        pushPortfolioFragmentRunnable = null;
        if (mBuySellDialog != null)
        {
            mBuySellDialog.dismiss();
            mBuySellDialog = null;
        }
        mTradeValueTextView = null;
        mCashLeftValueTextView = null;
        mQuantityTextView = null;
        mCashLeftTextView = null;
        if (mSlider != null)
        {
            mSlider.setOnSeekBarChangeListener(null);
        }
        mSlider = null;
        if (mQuickPriceButtonSet != null)
        {
            mQuickPriceButtonSet.setListener(null);
        }
        mQuickPriceButtonSet = null;
        mCommentsEditText = null;
        if (mBtnShareFacebook != null)
        {
            mBtnShareFacebook.setOnClickListener(null);
        }
        mBtnShareFacebook = null;
        if (mBtnShareTwitter != null)
        {
            mBtnShareTwitter.setOnClickListener(null);
        }
        mBtnShareTwitter = null;
        if (mBtnShareLinkedIn != null)
        {
            mBtnShareLinkedIn.setOnClickListener(null);
        }
        mBtnShareLinkedIn = null;
        if (mBtnLocation != null)
        {
            mBtnLocation.setOnClickListener(null);
        }
        mBtnLocation = null;
        if (mBtnSharePublic != null)
        {
            mBtnSharePublic.setOnCheckedChangeListener(null);
        }
        mBtnSharePublic = null;
        if (mConfirmButton != null)
        {
            mConfirmButton.setOnClickListener(null);
        }
        mConfirmButton = null;
        if (buySellTask != null)
        {
            buySellTask.cancel(false);
        }
        buySellTask = null;
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

        if (andDisplay)
        {
            //displayMarketClose();
            displaySelectedPortfolioContainer();
            displayPortfolioSelectorMenu();
            displaySelectedPortfolio();
            displayStockName();
            displayBottomViewPager();
            loadStockLogo();
            displayBuySellPrice();
            displayAsOf();
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
            displayQuickPriceButtonSet();
            displayBuySellSwitch();
            displayBuySellPrice();
        }
    }

    @Override public void linkWith(final PositionDTOCompactList positionDTOCompacts, boolean andDisplay)
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
            displayQuickPriceButtonSet();
        }
    }

    @Override protected void linkWith(QuoteDTO quoteDTO, boolean andDisplay)
    {
        super.linkWith(quoteDTO, andDisplay);
        setInitialBuyQuantityIfCan();
        setInitialSellQuantityIfCan();
        if (andDisplay)
        {
            displayBuySellPrice();
            displayAsOf();
            displayQuickPriceButtonSet();
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
    }

    @Override protected void linkWithSellQuantity(Integer sellQuantity, boolean andDisplay)
    {
        super.linkWithSellQuantity(sellQuantity, andDisplay);
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
    protected void buildUsedMenuPortfolios()
    {
        OwnedPortfolioId defaultOwnedPortfolioId = portfolioCompactListCache.getDefaultPortfolio(currentUserId.toUserBaseKey());

        if (defaultOwnedPortfolioId != null && securityCompactDTO != null)
        {
            Timber.d("lyl portfolioId=%s", defaultOwnedPortfolioId.portfolioId.toString());
            Set<MenuOwnedPortfolioId> newMenus = new TreeSet<>();

            PortfolioCompactDTO defaultPortfolioCompactDTO = portfolioCompactCache.get(defaultOwnedPortfolioId.getPortfolioIdKey());
            Timber.d("lyl %s", defaultPortfolioCompactDTO.toString());
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
                        Timber.d("lyl providerId=%s", providerId.toString());
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
                Timber.d("lyl ownedPortfolioId=%s", ownedPortfolioId.portfolioId.toString());
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
                else if (portfolioCompactDTO != null && portfolioCompactDTO.providerId != null && providerId != null &&
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
        displayBuySellPrice();
        displayBottomViewPager();
        displayStockName();
        displayQuickPriceButtonSet();
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
                    mPortfolioSelectorMenu.getMenu().add(Menu.NONE, Menu.NONE, Menu.NONE, menuOwnedPortfolioId);
                }
            }
        }
    }

    public void displaySelectedPortfolio()
    {
        TextView selectedPortfolio = mSelectedPortfolio;
        if (selectedPortfolio != null)
        {
            if (usedMenuOwnedPortfolioIds != null)
            {
                Timber.d("lyl usedMenuOwnedPortfolioIds.size()=%d", usedMenuOwnedPortfolioIds.size());
            }
            if (usedMenuOwnedPortfolioIds != null && usedMenuOwnedPortfolioIds.size() > 0 && purchaseApplicableOwnedPortfolioId != null)
            {
                MenuOwnedPortfolioId chosen = null;

                final Iterator<MenuOwnedPortfolioId> iterator = usedMenuOwnedPortfolioIds.iterator();
                MenuOwnedPortfolioId lastElement = null;
                while (iterator.hasNext())
                {
                    lastElement = iterator.next();
                    Timber.d("lyl lastElement.title=%s", lastElement.title);
                    if (purchaseApplicableOwnedPortfolioId.equals(lastElement))
                    {
                        chosen = lastElement;
                    }
                }
                if (chosen == null)
                {
                    chosen = lastElement;
                }

                selectedPortfolio.setText(chosen);
            }
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
                //adaper of new versioned ViewPager must call notifyDataSetChanged when data changes
                adapter.notifyDataSetChanged();

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

        if (mExchangeSymbol != null)
        {
            if (securityCompactDTO != null)
            {
                mExchangeSymbol.setText(securityCompactDTO.getExchangeSymbol());
            }
            else
            {
                mExchangeSymbol.setText("");
            }
        }

        boolean marketIsOpen = securityCompactDTO == null || securityCompactDTO.marketOpen == null || securityCompactDTO.marketOpen;
        if (mMarketIcon != null)
        {
            mMarketIcon.setVisibility(marketIsOpen ? View.INVISIBLE : View.VISIBLE);
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
                bPrice =  "-";
                sPrice =  "-";
            }
            else if (quoteDTO.ask == null)
            {
                bPrice =  getResources().getString(R.string.buy_sell_ask_price_not_available);
                sPrice =  getResources().getString(R.string.buy_sell_ask_price_not_available);
            }
            else
            {
                bthSignedNumber = new THSignedNumber(THSignedNumber.TYPE_MONEY, quoteDTO.ask, false, "");
                sthSignedNumber = new THSignedNumber(THSignedNumber.TYPE_MONEY, quoteDTO.bid, false, "");
                bPrice = bthSignedNumber.toString();
                sPrice = sthSignedNumber.toString();
            }
            String buyPrice = String.format("Buy @ %s %s", display, bPrice);
            String suyPrice = String.format("Sell @ %s %s", display, sPrice);
            mBuyPrice.setText(buyPrice);
            mSellPrice.setText(suyPrice);
        }
    }

    public void displayAsOf()
    {
        if (mVpriceAsOf != null)
        {
            String text;
            if (quoteDTO != null && quoteDTO.asOfUtc != null)
            {
                text = DateUtils.getFormattedDate(quoteDTO.asOfUtc);
            }
            else if (securityCompactDTO != null && securityCompactDTO.lastPriceDateAndTimeUtc != null)
            {
                text = DateUtils.getFormattedDate(securityCompactDTO.lastPriceDateAndTimeUtc);
            }
            else
            {
                text = "";
            }
            mVpriceAsOf.setText(getResources().getString(R.string.buy_sell_price_as_of) + " " + text);
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

    public void displayActionBarElements()
    {
        displayBuySellSwitch();
    }

    public void displayBuySellSwitch()
    {
        boolean supportSell = false;
        if (positionDTOCompactList == null || positionDTOCompactList.size() == 0 || purchaseApplicableOwnedPortfolioId == null)
        {
            supportSell = false;
        }
        else
        {
            Integer maxSellableShares = getMaxSellableShares();
            supportSell = maxSellableShares == null || maxSellableShares == 0;
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
            if (securityAlertAssistant.isPopulated() && securityAlertAssistant.getAlertId(securityId) != null)
            {
                mBtnAddTrigger.setEnabled(true);
            }
            else if (securityAlertAssistant.isPopulated() && securityAlertAssistant.getAlertId(securityId) == null)
            {
                mBtnAddTrigger.setEnabled(true);
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
        displayQuickPriceButtonSet();
        displayBuySellSwitch();
    }

    @Override protected void setRefreshingQuote(boolean refreshingQuote)
    {
        super.setRefreshingQuote(refreshingQuote);
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
        billingRequest.purchaseReportedListener = new BuySellPurchaseReportedListener();
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
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar)
            {
            }

            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if (fromUser)
                {
                    mQuantity = progress;
                    mQuantityTextView.setText(String.valueOf(progress));
                    updateBuySellDialog();
                }
            }
        };
    }

    public void showBuySellDialog()
    {
        publishToFb = false;
        publishToLi = false;
        publishToTw = false;
        shareLocation = true;
        sharePublic = false;
        pushPortfolioFragmentRunnable = null;
        pushPortfolioFragmentRunnable = new PushPortfolioFragmentRunnable()
        {
            @Override public void pushPortfolioFragment(SecurityPositionDetailDTO securityPositionDetailDTO)
            {
                BuySellFragment.this.pushPortfolioFragment(securityPositionDetailDTO);
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.security_buy_sell_dialog, null);
        builder.setView(view);
        builder.setCancelable(true);
        TextView stockNameTextView = (TextView)view.findViewById(R.id.dialog_stock_name);
        if (stockNameTextView != null)
        {
            stockNameTextView.setText(securityCompactDTO.name);
        }
        TextView priceTextView = (TextView)view.findViewById(R.id.dialog_price);
        if (priceTextView != null)
        {
            priceTextView.setText(isTransactionTypeBuy ? mBuyPrice.getText() : mSellPrice.getText());
        }
        ImageButton btnAddCash = (ImageButton)view.findViewById(R.id.dialog_btn_add_cash);
        if (btnAddCash != null)
        {
            btnAddCash.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    handleBtnAddCashPressed();
                }
            });
        }

        mTradeValueTextView = null;
        mTradeValueTextView = (TextView)view.findViewById(R.id.vtrade_value);
        mQuantityTextView = null;
        mQuantityTextView = (TextView)view.findViewById(R.id.vquantity);
        if (mQuantityTextView != null)
        {
            mQuantityTextView.setText(String.valueOf(mQuantity));
        }
        mCashLeftValueTextView = null;
        mCashLeftValueTextView = (TextView)view.findViewById(R.id.vcash_left);

        mCashLeftTextView = (TextView)view.findViewById(R.id.dialog_cash_left);
        mCashLeftTextView.setText(isTransactionTypeBuy ? R.string.buy_sell_cash_left : R.string.buy_sell_share_left);

        mSlider = null;
        mSlider = (SeekBar)view.findViewById(R.id.seekBar);
        if (mSlider != null)
        {
            mSlider.setOnSeekBarChangeListener(createSeekBarListener());
            if (isTransactionTypeBuy)
            {
                Integer maxPurchasableShares = getMaxPurchasableShares();
                if (maxPurchasableShares != null)
                {
                    mSlider.setMax(maxPurchasableShares);
                    mSlider.setEnabled(maxPurchasableShares > 0);
                }
            }
            else
            {
                Integer maxSellableShares = getMaxSellableShares();
                if (maxSellableShares != null)
                {
                    mSlider.setMax(maxSellableShares);
                    mSlider.setEnabled(maxSellableShares > 0);
                }
            }
        }

        mQuickPriceButtonSet = null;
        mQuickPriceButtonSet = (QuickPriceButtonSet)view.findViewById(R.id.quick_price_button_set);
        if (mQuickPriceButtonSet != null)
        {
            mQuickPriceButtonSet.setListener(createQuickButtonSetListener());
            mQuickPriceButtonSet.addButton(R.id.toggle5k);
            mQuickPriceButtonSet.addButton(R.id.toggle10k);
            mQuickPriceButtonSet.addButton(R.id.toggle25k);
            mQuickPriceButtonSet.addButton(R.id.toggle50k);
        }
        displayQuickPriceButtonSet();

        //comments
        mCommentsEditText = null;
        mCommentsEditText = (EditText)view.findViewById(R.id.comments);

        //share
        mBtnShareFacebook = null;
        mBtnShareFacebook = (ToggleButton)view.findViewById(R.id.btn_share_fb);
        mBtnShareFacebook.setChecked(publishToFb);
        mBtnShareFacebook.setOnClickListener(new OnClickListener()
        {
            @Override public void onClick(View view)
            {
                publishToFb = !publishToFb;
            }
        });
        mBtnShareTwitter = null;
        mBtnShareTwitter = (ToggleButton)view.findViewById(R.id.btn_share_tw);
        mBtnShareTwitter.setChecked(publishToTw);
        mBtnShareTwitter.setOnClickListener(new OnClickListener()
        {
            @Override public void onClick(View view)
            {
                publishToTw = !publishToTw;
            }
        });
        mBtnShareLinkedIn = null;
        mBtnShareLinkedIn = (ToggleButton)view.findViewById(R.id.btn_share_li);
        mBtnShareLinkedIn.setChecked(publishToLi);
        mBtnShareLinkedIn.setOnClickListener(new OnClickListener()
        {
            @Override public void onClick(View view)
            {
                publishToLi = !publishToLi;
            }
        });
        mBtnShareWeChat = null;
        mBtnShareWeChat = (ToggleButton)view.findViewById(R.id.btn_wechat);
        mBtnShareWeChat.setChecked(publishToWe);
        mBtnShareWeChat.setOnClickListener(new OnClickListener()
        {
            @Override public void onClick(View view)
            {
                publishToWe = !publishToWe;
                if (publishToWe)
                {
                    Timber.d("lyl %s", securityCompactDTO.toString());
                    wechatSharerLazy.get().share(getActivity(), securityCompactDTO);
                }
            }
        });
        mBtnLocation = null;
        mBtnLocation = (ToggleButton)view.findViewById(R.id.btn_location);
        mBtnLocation.setChecked(shareLocation);
        mBtnLocation.setOnClickListener(new OnClickListener()
        {
            @Override public void onClick(View view)
            {
                shareLocation = !shareLocation;
            }
        });
        mBtnSharePublic = null;
        mBtnSharePublic = (ToggleButton)view.findViewById(R.id.switch_share_public);
        mBtnSharePublic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                sharePublic = b;
            }
        });
        //cancel button
        Button cancelButton = (Button)view.findViewById(R.id.dialog_btn_cancel);
        if (cancelButton != null)
        {
            cancelButton.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    if (mBuySellDialog != null)
                    {
                        mBuySellDialog.dismiss();
                    }
                }
            });
        }
        //confirm
        mConfirmButton = null;
        mConfirmButton = (Button)view.findViewById(R.id.dialog_btn_confirm);
        if (mConfirmButton != null)
        {
            mConfirmButton.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    if (mBuySellDialog != null)
                    {
                        mBuySellDialog.dismiss();
                    }
                    launchBuySell();
                }
            });
        }

        if (mBuySellDialog != null)
        {
            mBuySellDialog.dismiss();
            mBuySellDialog = null;
        }

        updateBuySellDialog();
        mBuySellDialog = builder.create();
        mBuySellDialog.show();
    }

    public void updateBuySellDialog()
    {
        String valueText = "-";
        String cashLeftText = getResources().getString(R.string.na);
        if (quoteDTO != null)
        {
            Double priceRefCcy = quoteDTO.getPriceRefCcy(portfolioCompactDTO, true);
            if (priceRefCcy != null && portfolioCompactDTO != null)
            {
                double value = mQuantity * priceRefCcy;
                THSignedNumber thTradeValue = new THSignedNumber(THSignedNumber.TYPE_MONEY, value, false, portfolioCompactDTO.currencyDisplay);
                valueText = thTradeValue.toString();

                if (isTransactionTypeBuy)
                {
                    double cashAvailable = portfolioCompactDTO.cashBalance;
                    THSignedNumber thSignedNumber = new THSignedNumber(THSignedNumber.TYPE_MONEY, cashAvailable - value, false, portfolioCompactDTO.currencyDisplay);
                    cashLeftText = thSignedNumber.toString();
                }
            }
            if (!isTransactionTypeBuy && positionDTOCompactList != null && portfolioCompactDTO != null)
            {
                Integer maxSellableShares = getMaxSellableShares();
                if (maxSellableShares != null && maxSellableShares != 0)
                {
                    cashLeftText = String.valueOf(maxSellableShares - mQuantity);//share left
                }
            }
        }
        if (mTradeValueTextView != null)
        {
            mTradeValueTextView.setText(valueText);
        }
        if (mQuantityTextView != null)
        {
            mQuantityTextView.setText(String.valueOf(mQuantity));
        }
        if (mCashLeftValueTextView != null)
        {
            mCashLeftValueTextView.setText(cashLeftText);
        }
        if (mSlider != null)
        {
            mSlider.setProgress(mQuantity);
        }
        if (mConfirmButton != null)
        {
            mConfirmButton.setEnabled(mQuantity != 0 && (
                    (!isBuying && isTransactionTypeBuy && hasValidInfoForBuy()) ||
                    (!isSelling &&!isTransactionTypeBuy && hasValidInfoForSell())
                    ));
        }
    }

    protected boolean hasValidInfoForBuy()
    {
        return securityId != null && securityCompactDTO != null && quoteDTO != null && quoteDTO.ask != null;
    }

    protected boolean hasValidInfoForSell()
    {
        return securityId != null && securityCompactDTO != null && quoteDTO != null && quoteDTO.bid != null;
    }

    private void launchBuySell()
    {
        if (buySellTask != null)
        {
            buySellTask.cancel(false);
        }
        buySellTask = new BuySellAsyncTask(BuySellFragment.this.getActivity(), isTransactionTypeBuy, securityId);
        if (isTransactionTypeBuy)
        {
            isBuying = true;
        }
        else
        {
            isSelling = true;
        }
        buySellTask.execute();
    }

    public class BuySellAsyncTask extends BaseBuySellAsyncTask
    {
        private ProgressDialog transactionDialog;

        public BuySellAsyncTask(Context context, boolean isBuy, SecurityId securityId)
        {
            super(context, isBuy, securityId);
        }

        @Override protected void onPreExecute()
        {
            transactionDialog = progressDialogUtil.show(BuySellFragment.this.getActivity(),
                   R.string.processing, R.string.alert_dialog_please_wait);
            super.onPreExecute();
        }

        @Override TransactionFormDTO getBuySellOrder()
        {
            return BuySellFragment.this.getBuySellOrder(isBuy);
        }

        @Override protected void onPostExecute(SecurityPositionDetailDTO securityPositionDetailDTO)
        {
            super.onPostExecute(securityPositionDetailDTO);
            if (transactionDialog != null)
            {
                transactionDialog.dismiss();
            }

            if (isCancelled())
            {
                return;
            }

            if (isBuy)
            {
                isBuying = false;
            }
            else
            {
                isSelling = false;
            }
            //displayConfirmMenuItem(buySellConfirmItem);
            if (!isDetached() && errorCode == CODE_OK && pushPortfolioFragmentRunnable != null)
            {
                pushPortfolioFragmentRunnable.pushPortfolioFragment(securityPositionDetailDTO);
            }
        }
    }

    private TransactionFormDTO getBuySellOrder(boolean isBuy)
    {
        if (quoteDTO == null)
        {
            return null;
        }
        if (getApplicablePortfolioId() == null || getApplicablePortfolioId().portfolioId == null)
        {
            Timber.e("No portfolioId to apply to", new IllegalStateException());
            return null;
        }
        //Timber.d("fb=%b tw=%b li=%b location=%b public=%b quantity=%d", publishToFb,
        //        publishToTw, publishToLi, shareLocation, sharePublic,
        //        isBuy ? mBuyQuantity : mSellQuantity);
        return new TransactionFormDTO(
                publishToFb,
                publishToTw,
                publishToLi,
                shareLocation ? null : null, // TODO implement location
                shareLocation ? null : null,
                shareLocation ? null : null,
                sharePublic,
                mCommentsEditText == null ? null : mCommentsEditText.getText().toString(),
                quoteDTO.rawResponse,
                mQuantity,
                getApplicablePortfolioId().portfolioId
        );
    }

    private void pushPortfolioFragment(SecurityPositionDetailDTO securityPositionDetailDTO)
    {
        if (securityPositionDetailDTO != null && securityPositionDetailDTO.portfolio != null)
        {
            pushPortfolioFragment(new OwnedPortfolioId(currentUserId.toUserBaseKey(), securityPositionDetailDTO.portfolio.getPortfolioId()));
        }
        else
        {
            pushPortfolioFragment();
        }
    }

    private void pushPortfolioFragment()
    {
        pushPortfolioFragment(getApplicablePortfolioId());
    }

    protected interface PushPortfolioFragmentRunnable
    {
        void pushPortfolioFragment(SecurityPositionDetailDTO securityPositionDetailDTO);
    }

    private void pushPortfolioFragment(OwnedPortfolioId ownedPortfolioId)
    {
        // TODO find a better way to remove this fragment from the stack
        getNavigator().popFragment();

        Bundle args = new Bundle();
        args.putBundle(PositionListFragment.BUNDLE_KEY_SHOW_PORTFOLIO_ID_BUNDLE, ownedPortfolioId.getArgs());
        getNavigator().pushFragment(PositionListFragment.class, args);
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

                mQuantity = isTransactionTypeBuy ? mBuyQuantity : mSellQuantity;
                updateBuySellDialog();
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
        mInfoTextView.setEnabled(position != 0);
        mDiscussTextView.setEnabled(position != 1);
        mNewsTextView.setEnabled(position != 2);
        mInfoTextView.setTextColor(getResources().getColor(position == 0 ? R.color.white : R.color.btn_twitter_color_end));
        mDiscussTextView.setTextColor(getResources().getColor(position == 1 ? R.color.white : R.color.btn_twitter_color_end));
        mNewsTextView.setTextColor(getResources().getColor(position == 2 ? R.color.white : R.color.btn_twitter_color_end));
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

    protected class BuySellPurchaseReportedListener implements PurchaseReporter.OnPurchaseReportedListener
    {
        @Override public void onPurchaseReported(int requestCode, ProductPurchase reportedPurchase, UserProfileDTO updatedUserProfile)
        {
            linkWith(updatedUserProfile, true);
            waitForPortfolioCompactListFetched(updatedUserProfile.getBaseKey());
            updateBuySellDialog();
        }

        @Override public void onPurchaseReportFailed(int requestCode, ProductPurchase reportedPurchase, BillingException error)
        {
        }
    }
}
