package com.tradehero.th.fragments.trade;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
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
import com.tradehero.common.thread.KnownExecutorServices;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.ImageViewThreadSafe;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.googleplay.THIABActor;
import com.tradehero.th.fragments.billing.THIABUserInteractor;
import com.tradehero.th.fragments.security.BuySellBottomStockPagerAdapter;
import com.tradehero.th.fragments.security.StockInfoFragment;
import com.tradehero.th.models.alert.SecurityAlertAssistant;
import com.tradehero.th.network.service.SecurityService;
import com.viewpagerindicator.PageIndicator;
import dagger.Lazy;
import javax.inject.Inject;

public class BuySellFragment extends AbstractBuySellFragment
    implements SecurityAlertAssistant.OnPopulatedListener
{
    private final static String TAG = BuySellFragment.class.getSimpleName();
    public final static int TRANSACTION_COST = 10;

    public final static float BUY_BUTTON_DISABLED_ALPHA = 0.5f;

    private ToggleButton mBuySellSwitch;

    private ImageViewThreadSafe mStockBgLogo;
    private ImageViewThreadSafe mStockLogo;
    private ImageView mStockChart;

    private TextView mStockName;
    private ImageButton mStockChartButton;

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

    @Inject protected Lazy<SecurityService> securityService;

    protected SecurityAlertAssistant securityAlertAssistant;

    double lastPrice;
    int sliderIncrement = 0;
    int maxQuantity = 0;
    int mQuantity = 0;
    int sliderMaxValue = 0;

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
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        THLog.d(TAG, "onCreateView");
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

        mStockName = (TextView) view.findViewById(R.id.stock_name);
        mStockChartButton = (ImageButton) view.findViewById(R.id.stock_chart_button);
        if (mStockChartButton != null)
        {
            mStockChartButton.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    pushStockInfoFragmentIn();
                }
            });
        }

        mInfoFrame = (FrameLayout) view.findViewById(R.id.chart_frame);

        ImageView mInfoButton = (ImageView) view.findViewById(R.id.btn_trade_info);
        if (mInfoButton != null)
        {
            mInfoButton.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    showInfoDialog();
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

    @Override protected void createUserInteractor()
    {
        userInteractor = new BuySellTHIABUserInteractor(getActivity(), getBillingActor(), getView().getHandler());
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

        MenuItem menuElements = menu.findItem(R.id.menu_elements_buy_sell);

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

    @Override public void onDestroyOptionsMenu()
    {
        THLog.d(TAG, "onDestroyOptionsMenu");
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

        securityAlertAssistant.setOnPopulatedListener(this);
        securityAlertAssistant.setUserBaseKey(currentUserBaseKeyHolder.getCurrentUserBaseKey());
        securityAlertAssistant.populate();

        userInteractor.waitForSkuDetailsMilestoneComplete(new Runnable()
        {
            @Override public void run()
            {
                display();
            }
        });
    }

    @Override public void onPause()
    {
        securityAlertAssistant.setOnPopulatedListener(null);
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        THLog.d(TAG, "onDestroyView");
        if (mStockChartButton != null)
        {
            mStockChartButton.setOnClickListener(null);
        }
        mStockChartButton = null;

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
        securityAlertAssistant = null;
        super.onDestroy();
    }

    @Override public void onDetach()
    {
        THLog.d(TAG, "onDetach");
        super.onDetach();
    }

    @Override public void linkWith(SecurityId securityId, boolean andDisplay)
    {
        super.linkWith(securityId, andDisplay);
        if (andDisplay)
        {
            //displayExchangeSymbol();
            displayStockChartButton();
        }
    }

    @Override public void linkWith(SecurityCompactDTO securityCompactDTO, boolean andDisplay)
    {
        super.linkWith(securityCompactDTO, andDisplay);
        if (andDisplay)
        {
            displayMarketClose();
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
        Integer maxSellableShares = getMaxSellableShares();
        if (this.securityPositionDetailDTO == null) // This is the first update
        {
            if (maxSellableShares != null)
            {
                mSellQuantity = maxSellableShares;
                if (maxSellableShares == 0)
                {
                    setTransactionTypeBuy(true);
                }
            }
        }

        super.linkWith(securityPositionDetailDTO, andDisplay);

        if (this.securityPositionDetailDTO != null && maxSellableShares != null && maxSellableShares == 0)
        {
            // Nothing to sell
            setTransactionTypeBuy(true);
        }

        if (andDisplay)
        {
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
        if (andDisplay)
        {
            displayPricingBidAskView();
            displayTradeQuantityView();
            displayQuickPriceButtonSet();
            displaySlider();
        }
    }

    //<editor-fold desc="Display Methods">
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
        displayStockChartButton();
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

    public void displayStockChartButton()
    {
        if (mStockChartButton != null)
        {
            mStockChartButton.setEnabled(this.securityId != null);
            mStockChartButton.setAlpha(this.securityId != null ? 1 : 0.5f);
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
            if (getApplicablePortfolioId() != null)
            {
                mTradeQuantityView.linkWith(getApplicablePortfolioId().getPortfolioId(), true);
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
                mBuyBtn.setEnabled(mBuyQuantity > 0);
                mBuyBtn.setText(mBuyQuantity > 0 ? R.string.button_buy : R.string.button_cannot_buy);
            }
            else
            {
                mBuyBtn.setEnabled(mSellQuantity > 0);
                mBuyBtn.setText(mSellQuantity > 0 ? R.string.button_sell : R.string.button_cannot_sell);
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
                    mSlider.setProgress(mBuyQuantity);
                }
            }
            else
            {
                Integer maxSellableShares = getMaxSellableShares();
                if (maxSellableShares != null)
                {
                    mSlider.setMax(maxSellableShares);
                    mSlider.setEnabled(maxSellableShares > 0);
                    mSlider.setProgress(mSellQuantity);
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
                mBtnAddTrigger.setImageResource(R.drawable.buyscreen_txtnotice_bought);
            }
            else if (securityAlertAssistant.isPopulated() && securityAlertAssistant.getAlertId(securityId) == null)
            {
                mBtnAddTrigger.setEnabled(true);
                mBtnAddTrigger.setImageResource(R.drawable.buyscreen_txtnotice_buy);
            }
            else // TODO check if failed
            {
                mBtnAddTrigger.setEnabled(false);
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
        if (securityAlertAssistant.isPopulated() && securityAlertAssistant.getAlertId(securityId) != null)
        {
            THToast.show("Push fragment to display the alert");
        }
        else if (securityAlertAssistant.isPopulated() && securityAlertAssistant.getAlertId(securityId) == null)
        {
            THToast.show("Push fragment to display a new alert");
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
        args.putInt(BuySellConfirmFragment.BUNDLE_KEY_QUANTITY_BUY, mBuyQuantity);
        args.putInt(BuySellConfirmFragment.BUNDLE_KEY_QUANTITY_SELL, mSellQuantity);
        args.putBundle(BuySellConfirmFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());

        OwnedPortfolioId applicablePortfolioId = getApplicablePortfolioId();
        if (applicablePortfolioId != null)
        {
            args.putBundle(BuySellConfirmFragment.BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE, applicablePortfolioId.getArgs());
        };

        navigator.pushFragment(BuySellConfirmFragment.class, args);
    }

    private void showInfoDialog()
    {
        THToast.show("Nothing for now");
        // TODO
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
                //String buyDetail = String.format("Buy %s %s:%s @ %s %f\nTransaction fee: virtual US$ 10\nTotal cost: US$ %.2f",
                //        /*tvQuantity.getText()*/ "quantity", securityPositionDetailDTO.security.exchange, securityPositionDetailDTO.security.symbol, securityPositionDetailDTO.security.currencyDisplay,
                //        lastPrice, getTotalCostForBuy());
                //
                //Bundle b = new Bundle();
                //
                //b.putString(BuySellConfirmFragment.BUNDLE_KEY_BUY_DETAIL_STR, buyDetail);
                //b.putString(BuySellConfirmFragment.BUNDLE_KEY_LAST_PRICE, String.valueOf(lastPrice));
                //b.putString(BuySellConfirmFragment.BUNDLE_KEY_QUANTITY_BUY, /*tvQuantity.getText().toString().replace(",", "")*/ "quantity");
                //b.putString(BuySellConfirmFragment.BUNDLE_KEY_SYMBOL, securityPositionDetailDTO.security.symbol);
                //b.putString(BuySellConfirmFragment.BUNDLE_KEY_EXCHANGE, securityPositionDetailDTO.security.exchange);
                //
                //Fragment newFragment = Fragment.instantiate(getActivity(), BuySellConfirmFragment.class.getName(), b);
                //
                //// Add the fragment to the activity, pushing this transaction
                //// on to the back stack.
                //FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                //ft.replace(R.id.realtabcontent, newFragment, "trend_buy");
                //ft.addToBackStack("trend_buy");
                //ft.commit();
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
        public BuySellTHIABUserInteractor(Activity activity, THIABActor billingActor, Handler handler)
        {
            super(activity, billingActor, handler);
        }

        @Override protected void handleShowSkuDetailsMilestoneComplete()
        {
            super.handleShowSkuDetailsMilestoneComplete();
            displayTradeQuantityView();
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
}
