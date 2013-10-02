/**
 * TradeFragment.java 
 * TradeHero
 *
 * Created by @author Siddesh Bingi on Jul 24, 2013
 */
package com.tradehero.th.fragments.trending;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import com.squareup.picasso.Cache;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.squareup.picasso.UrlConnectionDownloader;
import com.tradehero.common.cache.LruMemFileCache;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.common.thread.KnownExecutorServices;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.widget.ImageUrlView;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.base.THUser;
import com.tradehero.th.fragments.BuyFragment;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.utills.Logger;
import com.tradehero.th.utills.Logger.LogLevel;
import com.tradehero.th.widget.trade.PricingBidAskView;
import com.tradehero.th.widget.trade.QuickPriceButtonSet;
import com.tradehero.th.widget.trade.TradeQuantityView;
import java.util.concurrent.Future;

public class TradeFragment extends DashboardFragment implements DTOView<SecurityCompactDTO>
{
    private final static String TAG = TradeFragment.class.getSimpleName();
    public final static int TRANSACTION_COST = 10;
    public final static String BUNDLE_KEY_EXCHANGE = TradeFragment.class.getName() + ".exchange";
    public final static String BUNDLE_KEY_SYMBOL = TradeFragment.class.getName() + ".symbol";

    public final static String BUY_DETAIL_STR = "buy_detail_str";
    public final static String LAST_PRICE = "last_price";
    public final static String QUANTITY = "quantity";
    public final static String SYMBOL = "symbol";
    public final static String EXCHANGE = "exchange";

    private ImageUrlView mStockBgLogo;
    private ImageUrlView mStockLogo;
    private ImageView mStockChart;

    private TextView mStockName;
    private ImageButton mStockChartButton;

    private PricingBidAskView mPricingBidAskView;
    private TradeQuantityView mTradeQuantityView;
    private QuickPriceButtonSet mQuickPriceButtonSet;

    private Button mBuyBtn;

    private SeekBar mSlider;

    private SecurityId securityId;
    private SecurityCompactDTO securityCompactDTO;

    double lastPrice;
    int sliderIncrement = 0;
    //int maxQuantity = 0;
    int mQuantity = 0;
    int sliderMaxValue = 0;

    int volume = 0;
    int avgDailyVolume = 0;

    private double mCashAvailable = 0;
    private boolean isTransactionTypeBuy = true;

    private Picasso mPicasso;
    private Transformation foregroundTransformation;

    public static void putParameters(Bundle args, SecurityId securityId)
    {
        args.putString(BUNDLE_KEY_EXCHANGE, securityId.exchange);
        args.putString(BUNDLE_KEY_SYMBOL, securityId.securitySymbol);
    }

    public static SecurityId getSecurityId(Bundle args)
    {
        return new SecurityId(args.getString(BUNDLE_KEY_EXCHANGE), args.getString(BUNDLE_KEY_SYMBOL));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (savedInstanceState != null)
        {
            securityId = getSecurityId(savedInstanceState);
            if (securityId.isValid())
            {
                // TODO
            }
        }
        View view = null;
        view = inflater.inflate(R.layout.fragment_trade, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view)
    {
        mStockBgLogo = (ImageUrlView) view.findViewById(R.id.stock_bg_logo);
        mStockLogo = (ImageUrlView) view.findViewById(R.id.stock_logo);
        mStockChart = (ImageView) view.findViewById(R.id.stock_chart);

        mStockName = (TextView) view.findViewById(R.id.stock_name);
        mStockChartButton = (ImageButton) view.findViewById(R.id.stock_chart_button);
        mStockChartButton.setOnClickListener(createStockChartButtonClickListener());

        mPricingBidAskView = (PricingBidAskView) view.findViewById(R.id.pricing_bid_ask_view);
        mTradeQuantityView = (TradeQuantityView) view.findViewById(R.id.trade_quantity_view);

        mQuickPriceButtonSet = (QuickPriceButtonSet) view.findViewById(R.id.quick_price_button_set);
        mQuickPriceButtonSet.setListener(createQuickButtonSetListener());
        mQuickPriceButtonSet.addButton(R.id.toggle5k);
        mQuickPriceButtonSet.addButton(R.id.toggle10k);
        mQuickPriceButtonSet.addButton(R.id.toggle25k);
        mQuickPriceButtonSet.addButton(R.id.toggle50k);

        mSlider = (SeekBar) view.findViewById(R.id.seekBar);
        mSlider.setOnSeekBarChangeListener(createSeekBarListener());

        mBuyBtn = (Button) view.findViewById(R.id.btn_buy);
        mBuyBtn.setOnClickListener(createBuyButtonListener());

        if (foregroundTransformation == null)
        {
            foregroundTransformation = new WhiteToTransparentTransformation();
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

        enableFields(false);
        display();
    }

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        //((TrendingDetailFragment) getActivity().getSupportFragmentManager()
        //        .findFragmentByTag("trending_detail")).setYahooQuoteUpdateListener(this);
    }

    @Override public void onResume()
    {
        super.onResume();
        display();
    }

    private void enableFields(boolean flag)
    {
        mQuickPriceButtonSet.setEnabled(flag);
        mBuyBtn.setEnabled(flag);
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

    @Override
    public void display(SecurityCompactDTO securityCompactDTO)
    {
        this.securityCompactDTO = securityCompactDTO;
        display();
    }

    public void display()
    {
        if (mPricingBidAskView != null)
        {
            mPricingBidAskView.display(securityCompactDTO);
        }
        if (mTradeQuantityView != null)
        {
            mTradeQuantityView.display(securityCompactDTO);
        }

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

        if (mSlider != null)
        {
            int maxShares = (int) getMaxPurchasableShares();
            mSlider.setMax(maxShares);
            mSlider.setEnabled(maxShares > 0);
        }
    }

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
        return (securityCompactDTO != null) && isUrlOk(securityCompactDTO.imageBlobUrl);
    }

    public static boolean isUrlOk(String url)
    {
        return (url != null) && (url.length() > 0);
    }

    public double getMaxPurchasableShares()
    {
        if (securityCompactDTO == null || securityCompactDTO.askPrice == null || securityCompactDTO.askPrice == 0)
        {
            return 0;
        }
        return Math.floor(THUser.getCurrentUser().portfolio.cashBalance / securityCompactDTO.askPrice);
    }

    public void loadImages ()
    {
        if (mStockLogo != null)
        {
            mStockLogo.setUrl(this.securityCompactDTO.imageBlobUrl);
        }
        if (mStockBgLogo != null)
        {
            mStockBgLogo.setUrl(this.securityCompactDTO.imageBlobUrl);
        }

        final Callback loadIntoBg = createLogoReadyCallback();

        if (isMyUrlOk())
        {

            // This line forces Picasso to clear the downloads running on the bg
            mPicasso.load((String) null)
                    .placeholder(R.drawable.default_image)
                    .error(R.drawable.default_image)
                    .into(mStockBgLogo);

            // This sequence gives the opportunity to android to cache the original http image if its cache headers instruct it to.
            Future<?> submitted = KnownExecutorServices.getCacheExecutor().submit(new Runnable()
            {
                @Override public void run()
                {
                    if (mStockLogo != null)
                    {
                        THLog.i(TAG, "Loading Fore for " + mStockLogo.getUrl());
                        mPicasso.load(mStockLogo.getUrl())
                                .placeholder(R.drawable.default_image)
                                .error(R.drawable.default_image)
                                .transform(foregroundTransformation)
                                .into(mStockLogo, loadIntoBg);
                    }
                }
            });

            if (submitted == null)
            {
                THLog.i(TAG, "Future submission was null");
            }
            else
            {
                THLog.i(TAG, "Future submission was ok");
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

            if (mStockBgLogo != null)
            {
                mStockBgLogo.post(new Runnable()
                {
                    @Override public void run()
                    {
                        loadIntoBg.onSuccess();
                    }
                });
            }
        }
    }

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
                if (mStockBgLogo != null && TradeFragment.isUrlOk(mStockBgLogo.getUrl()))
                {
                    THLog.i(TAG, "Loading Bg for " + mStockBgLogo.getUrl());
                    mPicasso.load(mStockBgLogo.getUrl())
                            .placeholder(R.drawable.default_image)
                            .error(R.drawable.default_image)
                            .resize(mStockBgLogo.getWidth(), mStockBgLogo.getHeight())
                            .centerInside()
                            .transform(foregroundTransformation)
                            .into(mStockBgLogo);
                }
                else if (mStockBgLogo != null && securityCompactDTO != null)
                {
                    mPicasso.load(securityCompactDTO.getExchangeLogoId())
                            .resize(mStockBgLogo.getWidth(), mStockBgLogo.getHeight())
                            .centerCrop()
                            .transform(foregroundTransformation)
                            .into(mStockBgLogo);
                }
            }
        };
    }

    private void segmentedUpdate(int cash)
    {
        int totalCashAvailable = cash - TRANSACTION_COST;

        int mQuantity = (int) Math.floor(totalCashAvailable / lastPrice);

        //		int avgDailyVolume = (int)Math.ceil(Double.parseDouble(trend.getAverageDailyVolume()));
        //		int volume = (int)Math.ceil(Double.parseDouble(trend.getVolume()));
        //
        //
        //		int maxQuantity = avgDailyVolume;
        //
        //		if(volume > avgDailyVolume)
        //			maxQuantity = volume;
        //
        //		maxQuantity = (int)Math.ceil(maxQuantity*0.2);
        //
        //		if(maxQuantity == 0)
        //			maxQuantity = 1;
        //
        //		if(mQuantity > maxQuantity)
        //			mQuantity = maxQuantity;

        int sValue = mQuantity / sliderIncrement;
        mSlider.setProgress(sValue);
    }

    private double getTotalCostForBuy()
    {
        double q = Double.parseDouble(/*tvQuantity.getText().toString().replace(",", "")*/ "12");
        return q * (lastPrice + TRANSACTION_COST);
    }

    @Override public void onDestroy()
    {
        //((TrendingDetailFragment) getActivity().getSupportFragmentManager()
        //        .findFragmentByTag("trending_detail")).setYahooQuoteUpdateListener(null);
        super.onDestroy();
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
                }
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
                String buyDetail = String.format("Buy %s %s:%s @ %s %f\nTransaction fee: virtual US$ 10\nTotal cost: US$ %.2f",
                        /*tvQuantity.getText()*/ "quantity", securityCompactDTO.exchange, securityCompactDTO.symbol, securityCompactDTO.currencyDisplay,
                        lastPrice, getTotalCostForBuy());

                Bundle b = new Bundle();

                b.putString(BUY_DETAIL_STR, buyDetail);
                b.putString(LAST_PRICE, String.valueOf(lastPrice));
                b.putString(QUANTITY, /*tvQuantity.getText().toString().replace(",", "")*/ "quantity");
                b.putString(SYMBOL, securityCompactDTO.symbol);
                b.putString(EXCHANGE, securityCompactDTO.exchange);

                Fragment newFragment = Fragment.instantiate(getActivity(), BuyFragment.class.getName(), b);

                // Add the fragment to the activity, pushing this transaction
                // on to the back stack.
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.realtabcontent, newFragment, "trend_buy");
                ft.addToBackStack("trend_buy");
                ft.commit();
            }
        };
    }

    private QuickPriceButtonSet.OnQuickPriceButtonSelectedListener createQuickButtonSetListener()
    {
        return new QuickPriceButtonSet.OnQuickPriceButtonSelectedListener()
        {
            @Override public void onQuickPriceButtonSelected(double priceSelected)
            {
                segmentedUpdate((int) priceSelected);
            }
        };
    }

    private OnClickListener createStockChartButtonClickListener()
    {
        return new OnClickListener()
        {
            @Override public void onClick(View view)
            {
                // TODO call chart fragment in
            }
        };
    }


}
