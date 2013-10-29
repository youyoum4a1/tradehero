/**
 * TradeFragment.java 
 * TradeHero
 *
 * Created by @author Siddesh Bingi on Jul 24, 2013
 */
package com.tradehero.th.fragments.trade;

import android.os.Bundle;
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
import com.tradehero.th.adapters.trade.TradeBottomStockPagerAdapter;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.network.service.SecurityService;
import com.tradehero.th.utills.Logger;
import com.tradehero.th.utills.Logger.LogLevel;
import com.tradehero.th.widget.trade.PricingBidAskView;
import com.tradehero.th.widget.trade.QuickPriceButtonSet;
import com.tradehero.th.widget.trade.TradeQuantityView;
import com.viewpagerindicator.PageIndicator;
import dagger.Lazy;
import javax.inject.Inject;

public class TradeFragment extends AbstractTradeFragment
{
    private final static String TAG = TradeFragment.class.getSimpleName();
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
    private ImageView mInfoButton;
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
    private TradeBottomStockPagerAdapter bottomViewPagerAdapter;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        THLog.d(TAG, "onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);

        if (desiredArguments == null)
        {
            desiredArguments = getArguments();
        }

        View view = null;
        view = inflater.inflate(R.layout.fragment_trade, container, false);
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
            mStockChartButton.setOnClickListener(createStockChartButtonClickListener());
        }

        mInfoFrame = (FrameLayout) view.findViewById(R.id.chart_frame);

        mInfoButton = (ImageView) view.findViewById(R.id.btn_trade_info);
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
                    THToast.show("Nothing yet");
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
                    THToast.show("Nothing yet");
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
            bottomViewPagerAdapter = new TradeBottomStockPagerAdapter(getActivity(), getFragmentManager());
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

        //mCashAvailableValue

        displayPageElements();
    }

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        //((TrendingDetailFragment) getActivity().getSupportFragmentManager()
        //        .findFragmentByTag("trending_detail")).setYahooQuoteUpdateListener(this);

        if (savedInstanceState != null)
        {
            securityId = new SecurityId(savedInstanceState);
            THLog.d(TAG, securityId.toString());
        }
        else
        {
            THLog.d(TAG, "SavedInstanceState null");
        }
    }

    //@Override public void onViewStateRestored(Bundle savedInstanceState)
    //{
    //    super.onViewStateRestored(savedInstanceState);
    //
    //    if (savedInstanceState != null)
    //    {
    //        securityId = new SecurityId(savedInstanceState);
    //        THLog.d(TAG, securityId.toString());
    //    }
    //    else
    //    {
    //        THLog.d(TAG, "SavedInstanceState null");
    //    }
    //}

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.trade_menu, menu);
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);

        displayExchangeSymbol(actionBar);
        displayMarketClose(menu);

        mBuySellSwitch = (ToggleButton) menu.findItem(R.id.trade_menu_toggle_mode).getActionView();
        // TODO do styling in styles.xml
        mBuySellSwitch.setTextOn(getString(R.string.switch_buy));
        mBuySellSwitch.setTextOff(getString(R.string.switch_sell));
        mBuySellSwitch.setTextColor(getResources().getColor(R.color.white));
        mBuySellSwitch.setOnCheckedChangeListener(createBuySellListener());
        displayBuySellSwitch();
        super.onCreateOptionsMenu(menu, inflater);
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

        display();
    }

    @Override public void onDestroyOptionsMenu()
    {
        THLog.d(TAG, "onDestroyOptionsMenu");
        if (mBuySellSwitch != null)
        {
            mBuySellSwitch.setOnCheckedChangeListener(null);
        }
        mBuySellSwitch = null;
        super.onDestroyOptionsMenu();
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

    @Override public void onSaveInstanceState(Bundle outState)
    {
        THLog.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override public void onDestroy()
    {
        //((TrendingDetailFragment) getActivity().getSupportFragmentManager()
        //        .findFragmentByTag("trending_detail")).setYahooQuoteUpdateListener(null);
        super.onDestroy();
    }

    @Override public void onDetach()
    {
        THLog.d(TAG, "onDetach");
        super.onDetach();
    }

    //@Override
    //public void onYahooQuoteUpdateStarted()
    //{
    //    mProgressBar.setVisibility(View.VISIBLE);
    //}
    //
    //@Override
    //public void onYahooQuoteUpdateListener(HashMap<String, String> yQuotes)
    //{
    //
    //    mProgressBar.setVisibility(View.GONE);
    //    enableFields(true);
    //
    //    double LastPrice = YUtils.parseQuoteValue(yQuotes.get("Last Trade (Price Only)"));
    //    if (!Double.isNaN(LastPrice))
    //    {
    //        lastPrice = LastPrice;
    //        tvLastPrice.setText(String.format("%s%.2f", trend.getCurrencyDisplay(), lastPrice));
    //    }
    //    else
    //    {
    //        Logger.log(TAG, "Unable to parse Last Trade (Price Only)", LogLevel.LOGGING_LEVEL_ERROR);
    //    }
    //
    //    //TODO Format date
    //    String lastPriceDatetimeUtc = yQuotes.get("Last Trade Date");
    //    if (TextUtils.isEmpty(lastPriceDatetimeUtc))
    //    {
    //        tvPriceAsOf.setText(lastPriceDatetimeUtc);
    //    }
    //
    //    double askPrice = YUtils.parseQuoteValue(yQuotes.get("Ask"));
    //    if (Double.isNaN(askPrice))
    //    {
    //        Logger.log(TAG, "Unable to parse Ask, will try using real-time data", LogLevel.LOGGING_LEVEL_ERROR);
    //
    //        askPrice = YUtils.parseQuoteValue(yQuotes.get("Ask (Real-time)"));
    //        if (Double.isNaN(askPrice))
    //        {
    //            Logger.log(TAG, "Unable to parse Ask (Real-time)", LogLevel.LOGGING_LEVEL_ERROR);
    //        }
    //    }
    //
    //    if (!Double.isNaN(askPrice))
    //    {
    //        lastPrice = askPrice;
    //    }
    //
    //    double bidPrice = YUtils.parseQuoteValue(yQuotes.get("Bid"));
    //    if (Double.isNaN(bidPrice))
    //    {
    //        Logger.log(TAG, "Unable to parse Bid, will try using real-time data", LogLevel.LOGGING_LEVEL_ERROR);
    //
    //        bidPrice = YUtils.parseQuoteValue(yQuotes.get("Bid (Real-time)"));
    //        if (Double.isNaN(bidPrice))
    //        {
    //            Logger.log(TAG, "Unable to parse Bid (Real-time)", LogLevel.LOGGING_LEVEL_ERROR);
    //        }
    //    }
    //
    //    // only update ask & bid if both are present.
    //    if (!Double.isNaN(askPrice) && (Double.compare(askPrice, 0.0) == 0) && !Double.isNaN(bidPrice)
    //            && (Double.compare(bidPrice, 0.0) == 0))
    //    {
    //        tvAskPrice.setText(String.format("%.2f%s", askPrice, getString(R.string.ask_with_bracket)));
    //        tvBidPrice.setText(String.format(" x %.2f%s", bidPrice, getString(R.string.bid_with_bracket)));
    //    }
    //    else
    //    {
    //        Logger.log(TAG, "Unable to parse Ask & Bid Price", LogLevel.LOGGING_LEVEL_ERROR);
    //    }
    //
    //    double avgDailyVol = YUtils.parseQuoteValue(yQuotes.get("Average Daily Volume"));
    //    if (!Double.isNaN(avgDailyVol))
    //    {
    //        avgDailyVolume = (int) Math.ceil(avgDailyVol);
    //    }
    //
    //    double vol = YUtils.parseQuoteValue(yQuotes.get("Volume"));
    //    if (!Double.isNaN(vol))
    //    {
    //        avgDailyVolume = (int) Math.ceil(vol);
    //    }
    //
    //    updateValues(mCashAvailable, false);
    //}



    //<editor-fold desc="Display methods">

    @Override public void linkWith(SecurityId securityId, boolean andDisplay)
    {
        super.linkWith(securityId, andDisplay);
        if (andDisplay)
        {
            //displayExchangeSymbol();
            displayBottomViewPager();
        }
    }

    @Override public void linkWith(SecurityCompactDTO securityCompactDTO, boolean andDisplay)
    {
        super.linkWith(securityCompactDTO, andDisplay);
        if (andDisplay)
        {
            //displayMarketClose();
            displayPricingBidAskView();
            displayTradeQuantityView();
            displayStockName();
            storeImageUrlInImageViews();
            loadImages();
        }
    }

    @Override public void linkWith(final SecurityPositionDetailDTO securityPositionDetailDTO, boolean andDisplay)
    {
        Integer maxSellableShares = getMaxSellableShares(securityPositionDetailDTO, mPositionIndex); // TODO use other position indices
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

        if (this.securityPositionDetailDTO != null && maxSellableShares != null && maxSellableShares.intValue() == 0)
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
        //displayActionBarElements();
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
        displayPricingBidAskView();
        displayTradeQuantityView();
        displayBuyButton();
        displayBottomViewPager();
        displayStockName();
        displayQuickPriceButtonSet();
        displaySlider();
        storeImageUrlInImageViews();
        loadImages();
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
        if (bottomViewPagerAdapter != null)
        {
            if (securityId != null && !securityId.equals(bottomViewPagerAdapter.getSecurityId()))
            {
                bottomViewPagerAdapter.linkWith(securityId);

                if (mBottomViewPager != null)
                {
                    mBottomViewPager.post(new Runnable()
                    {
                        @Override public void run()
                        {
                            // We need to do it in a later frame otherwise the pager adapter crashes with IllegalStateException
                            bottomViewPagerAdapter.notifyDataSetChanged();
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

    public void displayBuySellSwitch()
    {
        if (mBuySellSwitch != null)
        {
            if (securityPositionDetailDTO == null || securityPositionDetailDTO.positions == null || securityPositionDetailDTO.positions.size() == 0)
            {
                mBuySellSwitch.setEnabled(false);
            }
            else
            {
                // TODO handle the case when we have move than 1 position
                Integer shareCount = securityPositionDetailDTO.positions.get(0).shares;
                if (shareCount == null || shareCount.intValue() == 0)
                {
                    mBuySellSwitch.setEnabled(false);
                }
                else
                {
                    mBuySellSwitch.setEnabled(true);
                }
            }
            mBuySellSwitch.setChecked(isTransactionTypeBuy);
            mBuySellSwitch.setAlpha(mBuySellSwitch.isEnabled() ? 1 : 0.5f);
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
                mStockLogo.setImageResource(this.securityCompactDTO.getExchangeLogoId());
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

    private void updateValues(double cash, boolean isPriceSlot)
    {

        Logger.log(TAG, "Cash: " + cash, LogLevel.LOGGING_LEVEL_INFO);

        mQuantity = 0;

        double totalCashAvailable = cash - TRANSACTION_COST;

        mQuantity = (int) Math.floor(totalCashAvailable / lastPrice);

        int maxQuantity = avgDailyVolume;

        if (volume > avgDailyVolume)
        {
            maxQuantity = volume;
        }

        maxQuantity = (int) Math.floor(maxQuantity * 0.2);

        if (maxQuantity == 0)
        {
            maxQuantity = 1;
        }

        //TODO check is valid maxQuantity

        if (mQuantity > maxQuantity)
        {
            mQuantity = maxQuantity;
        }

        int defaultQuantity = 0;

        if (isTransactionTypeBuy)
        {
            defaultQuantity = (int) Math.floor((mQuantity / 3.0));
        }

        Logger.log(TAG, "defaultQuantity: " + defaultQuantity, LogLevel.LOGGING_LEVEL_INFO);

        if (isPriceSlot)
        {
            defaultQuantity = mQuantity;

            //int quantityForSlots = (int)Math.ceil(totalCashAvailable / lastPrice);

            //if the closest quantity is greater than the maximum the user can buy,
            //we set the quantity to the maximum he can buy
            //int slidervalue = (defaultQuantity/sliderIncrement);

            ///Math.min(defaultQuantity, sliderMaxValue);
            //mSlider.setProgress(slidervalue);
            //mSlider.setMax(sliderMaxValue);

        }
        //else {

        //Slider
        int sliderValue = 0; //mQuantity; //155

        //if(!isPriceSlot)
        sliderIncrement = (mQuantity > 1000) ? 100 : ((mQuantity > 100) ? 10 : 1);

        //Logger.log(TAG, "Slider Increment: "+sliderIncrement, LogLevel.LOGGING_LEVEL_INFO);

        int sliderMaxValue = mQuantity / sliderIncrement;

        //Logger.log(TAG, "Slider MaxVaule: "+sliderMaxValue, LogLevel.LOGGING_LEVEL_INFO);

        int currentAbsoluteValue = mQuantity;
        if (defaultQuantity >= currentAbsoluteValue)
        {
            sliderValue = sliderMaxValue;
        }
        else
        {
            int value = (int) defaultQuantity / sliderIncrement;
            sliderValue = value;
        }
        //float value2 = absoluteValue / sliderIncrement;

        Logger.log(TAG, "Slider Vaule: " + sliderValue, LogLevel.LOGGING_LEVEL_INFO);

        mSlider.setMax(sliderMaxValue);
        mSlider.incrementProgressBy(sliderIncrement);
        mSlider.setProgress(sliderValue);
        //}
    }

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
        freshQuoteHolder.identifier = "TradeFragment";
    }

    private void pushBuyFragmentIn()
    {
        // save own state for when popped
        Bundle own = getArguments();
        if (own == null && securityId != null)
        {
            own = securityId.getArgs();
        }
        else if (own == null)
        {
            own = new Bundle();
        }
        own.putBoolean(BUNDLE_KEY_IS_BUY, isTransactionTypeBuy);
        own.putInt(BUNDLE_KEY_QUANTITY_BUY, mBuyQuantity);
        own.putInt(BUNDLE_KEY_QUANTITY_SELL, mSellQuantity);
        //setArguments(own); // Cannot do it

        Bundle b = new Bundle();
        b.putBoolean(BuyFragment.BUNDLE_KEY_IS_BUY, isTransactionTypeBuy);
        b.putInt(BuyFragment.BUNDLE_KEY_QUANTITY_BUY, mBuyQuantity);
        b.putInt(BuyFragment.BUNDLE_KEY_QUANTITY_SELL, mSellQuantity);
        securityId.putParameters(b);

        navigator.pushFragment(BuyFragment.class, b);
    }

    private void showInfoDialog()
    {
        THToast.show("Nothing for now");
        // TODO
    }

    //<editor-fold desc="FreshQuoteHolder.FreshQuoteListener">
    @Override public void onMilliSecToRefreshQuote(long milliSecToRefresh)
    {
        if (mQuoteRefreshProgressBar != null)
        {
            mQuoteRefreshProgressBar.setProgress((int) (milliSecToRefresh / MILLISEC_QUOTE_COUNTDOWN_PRECISION));
        }
    }
    //</editor-fold>

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
                if (mStockBgLogo != null && TradeFragment.isUrlOk((String) mStockBgLogo.getTag(R.string.image_url)))
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
                    mPicasso.load(securityPositionDetailDTO.security.getExchangeLogoId())
                            .resize(mStockBgLogo.getWidth(), mStockBgLogo.getHeight())
                            .centerCrop()
                            .transform(foregroundTransformation)
                            .into(mStockBgLogo);
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
                pushBuyFragmentIn();
                return;
                //String buyDetail = String.format("Buy %s %s:%s @ %s %f\nTransaction fee: virtual US$ 10\nTotal cost: US$ %.2f",
                //        /*tvQuantity.getText()*/ "quantity", securityPositionDetailDTO.security.exchange, securityPositionDetailDTO.security.symbol, securityPositionDetailDTO.security.currencyDisplay,
                //        lastPrice, getTotalCostForBuy());
                //
                //Bundle b = new Bundle();
                //
                //b.putString(BuyFragment.BUNDLE_KEY_BUY_DETAIL_STR, buyDetail);
                //b.putString(BuyFragment.BUNDLE_KEY_LAST_PRICE, String.valueOf(lastPrice));
                //b.putString(BuyFragment.BUNDLE_KEY_QUANTITY_BUY, /*tvQuantity.getText().toString().replace(",", "")*/ "quantity");
                //b.putString(BuyFragment.BUNDLE_KEY_SYMBOL, securityPositionDetailDTO.security.symbol);
                //b.putString(BuyFragment.BUNDLE_KEY_EXCHANGE, securityPositionDetailDTO.security.exchange);
                //
                //Fragment newFragment = Fragment.instantiate(getActivity(), BuyFragment.class.getName(), b);
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
                else if (isTransactionTypeBuy && quoteDTO.ask != null)
                {
                    mBuyQuantity = (int) Math.floor(priceSelected / quoteDTO.ask);
                }
                else if (!isTransactionTypeBuy && quoteDTO.bid != null)
                {
                    mSellQuantity = (int) Math.floor(priceSelected / quoteDTO.bid);
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

    private OnClickListener createStockChartButtonClickListener()
    {
        return new OnClickListener()
        {
            @Override public void onClick(View view)
            {
                THToast.show("Nothing for now");
                // TODO call chart fragment in
            }
        };
    }

    private CompoundButton.OnCheckedChangeListener createBuySellListener()
    {
        return new CompoundButton.OnCheckedChangeListener()
        {
            @Override public void onCheckedChanged(CompoundButton compoundButton, boolean checked)
            {
                setTransactionTypeBuy(checked);
            }
        };
    }
    //</editor-fold>
}
