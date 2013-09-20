package com.tradehero.th.widget.trending;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Cache;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.squareup.picasso.UrlConnectionDownloader;
import com.tradehero.common.cache.LruMemFileCache;
import com.tradehero.common.graphics.AbstractSequentialTransformation;
import com.tradehero.common.graphics.GaussianTransformation;
import com.tradehero.common.graphics.GrayscaleTransformation;
import com.tradehero.common.graphics.RoundedCornerTransformation;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.common.thread.KnownExecutorServices;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.widget.ImageUrlView;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.utills.DateUtils;
import com.tradehero.th.utills.TrendUtils;
import com.tradehero.th.utills.YUtils;
import java.util.ArrayList;
import java.util.concurrent.Future;

/** Created with IntelliJ IDEA. User: xavier Date: 9/5/13 Time: 5:19 PM To change this template use File | Settings | File Templates. */
public class TrendingSecurityView extends FrameLayout implements DTOView<SecurityCompactDTO>
{
    private static final String TAG = TrendingSecurityView.class.getSimpleName();
    private static Transformation foregroundTransformation;
    private static Transformation backgroundTransformation;
    private static Picasso mPicasso;

    private ImageUrlView stockBgLogo;
    private ImageUrlView stockLogo;
    private ImageUrlView countryLogo;
    private ImageView marketCloseIcon;
    private TextView stockName;
    private TextView exchangeSymbol;
    private TextView profitIndicator;
    private TextView currencyDisplay;
    private TextView lastPrice;
    private TextView date;
    private TextView securityType;

    private SecurityCompactDTO securityCompactDTO;
    private boolean mAttachedToWindow;
    private int mVisibility;

    //<editor-fold desc="Constructors">
    public TrendingSecurityView(Context context)
    {
        this(context, null);
    }

    public TrendingSecurityView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public TrendingSecurityView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        THLog.i(TAG, "OnFinishInflate");
        super.onFinishInflate();
        init();
    }

    protected void init ()
    {
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
            ((AbstractSequentialTransformation) backgroundTransformation).add(new GaussianTransformation());
            ((AbstractSequentialTransformation) backgroundTransformation).add(new RoundedCornerTransformation(
                            getResources().getDimensionPixelSize(R.dimen.trending_grid_item_corner_radius),
                            getResources().getColor(R.color.black)));
        }

        if (mPicasso == null)
        {
            Cache lruFileCache = null;
            try
            {
                lruFileCache = new LruMemFileCache(getContext());
            }
            catch (Exception e)
            {
                THLog.e(TAG, "Failed to create LRU", e);
            }

            mPicasso = new Picasso.Builder(getContext())
                    .downloader(new UrlConnectionDownloader(getContext()))
                    .memoryCache(lruFileCache)
                    .build();
            mPicasso.setDebugging(true);
        }

        stockName = (TextView) findViewById(R.id.stock_name);
        exchangeSymbol = (TextView) findViewById(R.id.exchange_symbol);
        profitIndicator = (TextView) findViewById(R.id.profit_indicator);
        currencyDisplay = (TextView) findViewById(R.id.currency_display);
        lastPrice = (TextView) findViewById(R.id.last_price);
        marketCloseIcon = (ImageView) findViewById(R.id.ic_market_close);
        stockLogo = (ImageUrlView) findViewById(R.id.stock_logo);
        stockLogo.softId = "logo";
        stockBgLogo = (ImageUrlView) findViewById(R.id.stock_bg_logo);
        stockBgLogo.softId = "logoBg";
        countryLogo = (ImageUrlView) findViewById(R.id.country_logo);
        date = (TextView) findViewById(R.id.date);
        securityType = (TextView) findViewById(R.id.sec_type);
        conditionalLoadImages();
    }

    @Override protected void onAttachedToWindow()
    {
        THLog.i(TAG, "Attached to Window");
        super.onAttachedToWindow();
        mAttachedToWindow = true;
        conditionalLoadImages();
    }

    @Override protected void onWindowVisibilityChanged(int visibility)
    {
        THLog.i(TAG, "Visibility changed " + visibility);
        super.onWindowVisibilityChanged(visibility);
        this.mVisibility = visibility;
        conditionalLoadImages();
    }

    @Override protected void onDetachedFromWindow()
    {
        THLog.i(TAG, "Detached from Window");
        mAttachedToWindow = false;
        stockLogo.setImageResource(R.drawable.default_image);
        stockBgLogo.setImageResource(R.drawable.default_image);
        this.securityCompactDTO = null;
        super.onDetachedFromWindow();
    }

    public boolean isMyUrlOk()
    {
        return (securityCompactDTO != null) && isUrlOk(securityCompactDTO.imageBlobUrl);
    }

    public static boolean isUrlOk(String url)
    {
        return (url != null) && (url.length() > 0);
    }

    public void display (final SecurityCompactDTO trend)
    {
        if (this.securityCompactDTO != null && trend.name.equals(this.securityCompactDTO.name))
        {
            //THLog.d(TAG, "Same securityCompactDTO again " + securityCompactDTO.name);
            return;
            // Note that this prevents updating values inside the securityCompactDTO
        }
        this.securityCompactDTO = trend;
        stockName.setText(trend.name.trim());
        exchangeSymbol.setText(trend.getExchangeSymbol());
        currencyDisplay.setText(trend.currencyDisplay);

        if (date != null)
        {
            date.setText(DateUtils.getFormatedTrendDate(securityCompactDTO.lastPriceDateAndTimeUtc));
        }

        double dLastPrice = YUtils.parseQuoteValue(trend.lastPrice.toString());
        if(!Double.isNaN(dLastPrice))
        {
            lastPrice.setText(String.format("%.2f", dLastPrice));
        }
        else
        {
            THLog.d(TAG, "TH: Unable to parse Last Price");
        }

        if(trend.pc50DMA > 0)
        {
            //profitIndicator.setText(getContext().getString(R.string.positive_prefix));
            profitIndicator.setText(R.string.positive_prefix);
        }
        else if(trend.pc50DMA < 0)
        {
            //profitIndicator.setText(getContext().getString(R.string.negative_prefix));
            profitIndicator.setText(R.string.negative_prefix);
        }

        profitIndicator.setTextColor(TrendUtils.colorForPercentage(trend.pc50DMA));
        exchangeSymbol.setTextColor(getResources().getColor(R.color.exchange_symbol));

        if(trend.marketOpen)
        {
            if (marketCloseIcon != null)
            {
                marketCloseIcon.setVisibility(View.GONE);
            }
            currencyDisplay.setTextColor(getResources().getColor(R.color.exchange_symbol));
            lastPrice.setTextColor(getResources().getColor(R.color.exchange_symbol));
        }
        else
        {
            if (marketCloseIcon != null)
            {
                marketCloseIcon.setVisibility(View.VISIBLE);
            }
            currencyDisplay.setTextColor(getResources().getColor(android.R.color.darker_gray));
            lastPrice.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }

        if (date != null)
        {
            // TODO
        }

        if (securityType != null && securityCompactDTO.securityType != null)
        {
            securityType.setText(trend.getSecurityTypeStringResourceId());
        }

        if (stockLogo != null)
        {
            stockLogo.setImageResource(R.drawable.default_image);
        }

        if (stockBgLogo != null)
        {
            stockBgLogo.setImageResource(R.drawable.default_image);
        }

        if (countryLogo != null)
        {
            countryLogo.setImageResource(trend.getExchangeLogoId());
        }

        //stockBgLogo.setAlpha(26); //15% opaque

        if (mAttachedToWindow)
        {
            loadImages();
        }
    }

    public boolean canDisplayImages()
    {
        return (mVisibility == VISIBLE) && mAttachedToWindow;
    }

    public void conditionalLoadImages()
    {
        if (canDisplayImages())
        {
            loadImages();
        }
    }

    public void loadImages ()
    {
        if (stockLogo != null)
        {
            stockLogo.setUrl(this.securityCompactDTO.imageBlobUrl);
        }
        if (stockBgLogo != null)
        {
            stockBgLogo.setUrl(this.securityCompactDTO.imageBlobUrl);
        }

        final Callback loadIntoBg = createLogoReadyCallback();

        if (isMyUrlOk())
        {

            // This line forces Picasso to clear the downloads running on the bg
            mPicasso.load((String) null)
                .placeholder(R.drawable.default_image)
                .error(R.drawable.default_image)
                .into(stockBgLogo);

            // This sequence gives the opportunity to android to cache the original http image if its cache headers instruct it to.
            Future<?> submitted = KnownExecutorServices.getCacheExecutor().submit(new Runnable()
            {
                @Override public void run()
                {
                    if (stockLogo != null)
                    {
                        THLog.i(TAG, "Loading Fore for " + stockLogo.getUrl());
                        mPicasso.load(stockLogo.getUrl())
                                .placeholder(R.drawable.default_image)
                                .error(R.drawable.default_image)
                                .transform(foregroundTransformation)
                                .into(stockLogo, loadIntoBg);
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
            if (stockLogo != null && countryLogo == null && this.securityCompactDTO != null)
            {
                stockLogo.setImageResource(this.securityCompactDTO.getExchangeLogoId());
            }
            else if (stockLogo != null)
            {
                mPicasso.load((String) null)
                        .placeholder(R.drawable.default_image)
                        .error(R.drawable.default_image)
                        .into(stockLogo);
            }

            post(new Runnable()
            {
                @Override public void run()
                {
                    loadIntoBg.onSuccess();
                }
            });
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
                if (stockBgLogo != null && TrendingSecurityView.isUrlOk(stockBgLogo.getUrl()))
                {
                    THLog.i(TAG, "Loading Bg for " + stockBgLogo.getUrl());
                    mPicasso.load(stockBgLogo.getUrl())
                            .placeholder(R.drawable.default_image)
                            .error(R.drawable.default_image)
                            .resize(getWidth(), getHeight())
                            .centerCrop()
                            .transform(backgroundTransformation)
                            .into(stockBgLogo);
                }
                else if (stockBgLogo != null && securityCompactDTO != null)
                {
                    mPicasso.load(securityCompactDTO.getExchangeLogoId())
                            .resize(getWidth(), getHeight())
                            .centerCrop()
                            .transform(backgroundTransformation)
                            .into(stockBgLogo);
                }
            }
        };
    }
}
