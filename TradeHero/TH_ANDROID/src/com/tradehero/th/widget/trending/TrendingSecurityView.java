package com.tradehero.th.widget.trending;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.squareup.picasso.UrlConnectionDownloader;
import com.tradehero.common.cache.LruFileCache;
import com.tradehero.common.graphics.ColorMatrixFactory;
import com.tradehero.common.graphics.GaussianGrayscaleTransformation;
import com.tradehero.common.graphics.GaussianTransformation;
import com.tradehero.common.graphics.GaussianWhiteToTransparentTransformation;
import com.tradehero.common.graphics.GrayscaleTransformation;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.widget.ImageUrlView;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.utills.TrendUtils;
import com.tradehero.th.utills.YUtils;

/** Created with IntelliJ IDEA. User: xavier Date: 9/5/13 Time: 5:19 PM To change this template use File | Settings | File Templates. */
public class TrendingSecurityView extends FrameLayout implements DTOView<SecurityCompactDTO>
{
    public static final int DEFAULT_CONCURRENT_DOWNLOAD = 3;
    private static final String TAG = TrendingSecurityView.class.getSimpleName();
    private static Transformation foregroundTransformation;
    private static Transformation backgroundTransformation;
    private static Picasso mPicasso;

    private ImageUrlView stockBgLogo;
    private ImageUrlView stockLogo;
    private ImageView marketCloseIcon;
    private TextView stockName;
    private TextView exchangeSymbol;
    private TextView profitIndicator;
    private TextView currencyDisplay;
    private TextView lastPrice;

    private SecurityCompactDTO securityCompactDTO;
    private boolean mAttachedToWindow;

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

    protected void init ()
    {
        if (foregroundTransformation == null)
        {
            foregroundTransformation = new WhiteToTransparentTransformation();
        }
        if (backgroundTransformation == null)
        {
            backgroundTransformation = new GaussianGrayscaleTransformation();
        }

        if (mPicasso == null)
        {
            LruFileCache lruFileCache = new LruFileCache(getContext());
            //lruFileCache.clearDir();

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
        //marketCloseIcon = (ImageView) findViewById(R.id.ic_market_close);
        stockLogo = (ImageUrlView) findViewById(R.id.stock_logo);
        stockBgLogo = (ImageUrlView) findViewById(R.id.stock_bg_logo);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        init();
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        mAttachedToWindow = true;
        post(new Runnable()
        {
            @Override public void run()
            {
                loadImages();
            }
        });
        invalidate(); // To ensure processing of the queue
    }

    @Override protected void onWindowVisibilityChanged(int visibility)
    {
        super.onWindowVisibilityChanged(visibility);
        invalidate();
    }

    @Override protected void onDetachedFromWindow()
    {
        mAttachedToWindow = false;
        stockLogo.setImageResource(R.drawable.default_image);
        stockBgLogo.setImageResource(R.drawable.default_image);
        this.securityCompactDTO = null;
        super.onDetachedFromWindow();
    }

    public boolean isMyUrlOk()
    {
        return (securityCompactDTO != null) &&
                (securityCompactDTO.imageBlobUrl != null) && // Yes, some urls can be null
                (securityCompactDTO.imageBlobUrl.length() > 0);
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
        exchangeSymbol.setText(String.format("%s:%s", trend.exchange, trend.symbol));
        currencyDisplay.setText(trend.currencyDisplay);

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
            profitIndicator.setText(getContext().getString(R.string.positive_prefix));
        }
        else if(trend.pc50DMA < 0)
        {
            profitIndicator.setText(getContext().getString(R.string.negative_prefix));
        }

        profitIndicator.setTextColor(TrendUtils.colorForPercentage(trend.pc50DMA));

        if(trend.marketOpen)
        {
            //marketCloseIcon.setVisibility(View.GONE);
            exchangeSymbol.setTextColor(getContext().getResources().getColor(R.color.exchange_symbol));
            currencyDisplay.setTextColor(getContext().getResources().getColor(R.color.exchange_symbol));
            lastPrice.setTextColor(getContext().getResources().getColor(R.color.exchange_symbol));
        }
        else
        {
            //marketCloseIcon.setVisibility(View.VISIBLE);
            exchangeSymbol.setTextColor(getContext().getResources().getColor(android.R.color.darker_gray));
            currencyDisplay.setTextColor(getContext().getResources().getColor(android.R.color.darker_gray));
            lastPrice.setTextColor(getContext().getResources().getColor(android.R.color.darker_gray));
        }

        stockLogo.setImageResource(R.drawable.default_image);
        stockBgLogo.setImageResource(R.drawable.default_image);

        //stockBgLogo.setAlpha(26); //15% opaque
        final View finalisedConvertView = this;

        if (mAttachedToWindow)
        {
            loadImages();
        }
    }

    public void loadImages ()
    {
        stockLogo.setUrl(this.securityCompactDTO.imageBlobUrl);
        stockBgLogo.setUrl(this.securityCompactDTO.imageBlobUrl);

        if (isMyUrlOk())
        {

            Callback loadIntoBg = createLogoReadyCallback();

            // This line forces Picasso to clear the downloads running on the bg
            mPicasso.load((String) null)
                .placeholder(R.drawable.default_image)
                .error(R.drawable.default_image)
                .into(stockBgLogo);

            // This sequence gives the opportunity to android to cache the original http image if its cache headers instruct it to.
            mPicasso.load(stockLogo.getUrl())
                .placeholder(R.drawable.default_image)
                .error(R.drawable.default_image)
                .transform(foregroundTransformation)
                .into(stockLogo, loadIntoBg);
        }
        else
        {
            // These ensure that views with a missing image do not receive images from elsewhere
            mPicasso.load((String) null)
                .placeholder(R.drawable.default_image)
                .error(R.drawable.default_image)
                .into(stockBgLogo);
            mPicasso.load((String) null)
                .placeholder(R.drawable.default_image)
                .error(R.drawable.default_image)
                .into(stockLogo);
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
                mPicasso.load(stockBgLogo.getUrl())
                    .placeholder(R.drawable.default_image)
                    .error(R.drawable.default_image)
                    .resize(getWidth(), getHeight())
                    .centerCrop()
                    .transform(backgroundTransformation)
                    .into(stockBgLogo);
            }
        };
    }
}
