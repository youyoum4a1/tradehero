package com.tradehero.th.widget.trending;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.fedorvlasov.lazylist.FileCache;
import com.fedorvlasov.lazylist.ImageLoader;
import com.squareup.picasso.Transformation;
import com.tradehero.common.cache.BitmapFiler;
import com.tradehero.common.cache.KnownCaches;
import com.tradehero.common.graphics.ColorMatrixFactory;
import com.tradehero.common.graphics.GaussianGrayscaleTransformation;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.common.thread.CPUExecutorService;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.utills.TrendUtils;
import com.tradehero.th.utills.YUtils;

/** Created with IntelliJ IDEA. User: xavier Date: 9/5/13 Time: 5:19 PM To change this template use File | Settings | File Templates. */
public class TrendingSecurityView extends FrameLayout implements DTOView<SecurityCompactDTO>
{
    private static final String TAG = TrendingSecurityView.class.getSimpleName();
    public static final int DEFAULT_CONCURRENT_DOWNLOAD = 3;
    public static ImageLoader mImageLoader;
    @Deprecated public static ImageLoader mImageBgLoader;
    private static WhiteToTransparentTransformation whiteToTransparentTransformation;
    private static GaussianGrayscaleTransformation gaussianGrayscaleTransformation;

    private Handler handler;

    private ImageView stockBgLogo;
    private ImageView stockLogo;
    private ImageView marketCloseIcon;
    private TextView stockName;
    private TextView exchangeSymbol;
    private TextView profitIndicator;
    private TextView currencyDisplay;
    private TextView lastPrice;

    private SecurityCompactDTO trend;

    //<editor-fold desc="Constructors">
    public TrendingSecurityView(Context context)
    {
        super(context);
        init();
    }

    public TrendingSecurityView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init ();
    }

    public TrendingSecurityView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init ();
    }
    //</editor-fold>

    protected void init ()
    {
        if (mImageLoader == null)
        {
            mImageLoader = new ImageLoader(null, DEFAULT_CONCURRENT_DOWNLOAD, R.drawable.default_image, KnownCaches.getOriginal());
        }
        if (mImageBgLoader == null)
        {
            mImageBgLoader = new ImageLoader(getContext(), new GaussianGrayscaleTransformation(ColorMatrixFactory.getDefaultGrayScaleMatrix()), DEFAULT_CONCURRENT_DOWNLOAD, R.drawable.default_image, "Bg");
        }

        whiteToTransparentTransformation = new WhiteToTransparentTransformation();
        gaussianGrayscaleTransformation = new GaussianGrayscaleTransformation();

        stockBgLogo = (ImageView) findViewById(R.id.stock_bg_logo);
        stockName = (TextView) findViewById(R.id.stock_name);
        exchangeSymbol = (TextView) findViewById(R.id.exchange_symbol);
        profitIndicator = (TextView) findViewById(R.id.profit_indicator);
        currencyDisplay = (TextView) findViewById(R.id.currency_display);
        lastPrice = (TextView) findViewById(R.id.last_price);
        //marketCloseIcon = (ImageView) findViewById(R.id.ic_market_close);
        stockLogo = (ImageView) findViewById(R.id.stock_logo);
        stockBgLogo = (ImageView) findViewById(R.id.stock_bg_logo);

        handler = new Handler();
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        init();
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        post(new Runnable()
        {
            @Override public void run()
            {
                // Dummy
            }
        });
        stockBgLogo.invalidate();
        stockLogo.invalidate();
        invalidate(); // To ensure processing of the queue
    }

    @Override protected void onWindowVisibilityChanged(int visibility)
    {
        super.onWindowVisibilityChanged(visibility);
        invalidate();
    }

    @Override protected void onDetachedFromWindow()
    {
        stockLogo.setImageResource(R.drawable.default_image);
        stockBgLogo.setImageResource(R.drawable.default_image);
        this.trend = null;
        super.onDetachedFromWindow();
    }

    public Handler getHandler()
    {
        return handler;
    }

    public boolean isMyUrl (String url)
    {
        return (TrendingSecurityView.this.trend != null) &&
            (TrendingSecurityView.this.trend.imageBlobUrl != null) && // Yes, some urls can be null
            TrendingSecurityView.this.trend.imageBlobUrl.equals(url);
    }

    public void display (final SecurityCompactDTO trend)
    {
        this.trend = trend;
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

        boolean posted = handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                THLog.d(TAG, trend.imageBlobUrl + " running");
                loadImages();
            }
        });
        THLog.d(TAG, trend.imageBlobUrl + (posted ? "" : " not") + " posted");
    }

    public void loadImages()
    {
        if (this.trend != null && this.trend.imageBlobUrl != null && this.trend.imageBlobUrl.length() > 0)
        {
            mImageLoader.displayImage(this.trend.imageBlobUrl, null, true, createOriginalImageLoadingListener());
        }
        else
        {
            THLog.d(TAG, "No Image url");
            stockLogo.setImageResource(R.drawable.default_image);
            stockBgLogo.setImageResource(R.drawable.default_image);
            invalidate();
        }
    }

    private ImageLoader.ImageLoadingListener createOriginalImageLoadingListener()
    {
        return new ImageLoader.ImageLoadingListener()
        {
            @Override public void onLoadingComplete(String url, Bitmap originalImage)
            {
                // Make sure we do not show another's
                if (isMyUrl(url))
                {
                    CPUExecutorService.getExecutor().submit(createTransformAndCacheRunnable(url, originalImage,
                            whiteToTransparentTransformation, KnownCaches.getTransparentBg(), stockLogo));
                    CPUExecutorService.getExecutor().submit(createTransformAndCacheRunnable(url, originalImage,
                            gaussianGrayscaleTransformation, KnownCaches.getGreyGaussian(), stockBgLogo));
                }
            }
        };
    }

    /**
     * Needs to run outside the UI thread
     * @param url
     * @param original
     * @return
     */
    private Runnable createTransformAndCacheRunnable (final String url, final Bitmap original, final  Transformation transformation, final FileCache fileCache, final ImageView imageView)
    {
        return new Runnable()
        {
            @Override public void run()
            {
                // We care to transform only if it the same image to show
                if (isMyUrl(url))
                {
                    final Bitmap converted = transformation.transform(original);
                    BitmapFiler.cache(fileCache.getFile(url), converted);
                    post(createLogoImageSetter(url, converted, imageView));
                }
            }
        };
    }

    private Runnable createLogoImageSetter (final String url, final Bitmap bitmap, final ImageView imageView)
    {
        return new Runnable()
        {
            @Override public void run()
            {
                if (isMyUrl(url))
                {
                    imageView.setImageBitmap(bitmap);
                    imageView.invalidate();
                }
            }
        };
    }
}
