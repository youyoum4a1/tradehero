package com.tradehero.th.widget.trending;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.*;
import com.tradehero.common.cache.LruMemFileCache;
import com.tradehero.common.graphics.AbstractSequentialTransformation;
import com.tradehero.common.graphics.FastBlurTransformation;
import com.tradehero.common.graphics.GrayscaleTransformation;
import com.tradehero.common.graphics.RoundedCornerTransformation;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.common.thread.KnownExecutorServices;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.utills.DateUtils;
import com.tradehero.th.utills.YUtils;
import com.tradehero.th.utils.ColorUtils;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 9/5/13 Time: 5:19 PM To change this template use File | Settings | File Templates. */
public class TrendingSecurityView extends FrameLayout implements DTOView<SecurityCompactDTO>
{
    private static final String TAG = TrendingSecurityView.class.getSimpleName();
    private static Transformation foregroundTransformation;
    private static Transformation backgroundTransformation;

    @Inject protected Picasso mPicasso;

    private ImageView stockBgLogo;
    private ImageView stockLogo;
    private ImageView countryLogo;
    private ImageView marketCloseIcon;
    private TextView stockName;
    private TextView exchangeSymbol;
    private TextView profitIndicator;
    private TextView currencyDisplay;
    private TextView lastPrice;
    private TextView date;
    private TextView securityType;

    private SecurityCompactDTO securityCompactDTO;

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
        super.onFinishInflate();
        init();
    }

    protected void init ()
    {
        DaggerUtils.inject(this);
        createTransformations();
        fetchViews();
    }

    private void createTransformations()
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
            ((AbstractSequentialTransformation) backgroundTransformation).add(new FastBlurTransformation(10));
            ((AbstractSequentialTransformation) backgroundTransformation).add(new RoundedCornerTransformation(
                            getResources().getDimensionPixelSize(R.dimen.trending_grid_item_corner_radius),
                            getResources().getColor(R.color.black)));
        }
    }

    private void fetchViews()
    {
        stockName = (TextView) findViewById(R.id.stock_name);
        exchangeSymbol = (TextView) findViewById(R.id.exchange_symbol);
        profitIndicator = (TextView) findViewById(R.id.profit_indicator);
        currencyDisplay = (TextView) findViewById(R.id.currency_display);
        lastPrice = (TextView) findViewById(R.id.last_price);
        marketCloseIcon = (ImageView) findViewById(R.id.ic_market_close);
        stockLogo = (ImageView) findViewById(R.id.stock_logo);
        stockBgLogo = (ImageView) findViewById(R.id.stock_bg_logo);
        countryLogo = (ImageView) findViewById(R.id.country_logo);
        date = (TextView) findViewById(R.id.date);
        securityType = (TextView) findViewById(R.id.sec_type);
    }

    public boolean isMyUrlOk()
    {
        return (securityCompactDTO != null) && isUrlOk(securityCompactDTO.imageBlobUrl);
    }

    public static boolean isUrlOk(String url)
    {
        return (url != null) && (!url.isEmpty());
    }

    @Override protected void onDetachedFromWindow()
    {
        clearImageViewUrls();
        clearRunningOperations();

        super.onDetachedFromWindow();
    }

    private void clearImageViewUrls()
    {
        if (stockLogo != null)
        {
            stockLogo.setTag(R.string.image_url, null);
        }
        if (stockBgLogo != null)
        {
            stockBgLogo.setTag(R.string.image_url, null);
        }
    }

    private void clearRunningOperations()
    {
        if (stockLogo != null)
        {
            mPicasso.load((String) null)
                    .placeholder(R.drawable.default_image)
                    .error(R.drawable.default_image)
                    .into(stockLogo);
        }

        if (stockBgLogo != null)
        {
            mPicasso.load((String) null)
                    .placeholder(R.drawable.default_image)
                    .error(R.drawable.default_image)
                    .into(stockBgLogo);
        }
    }

    @Override public void display (final SecurityCompactDTO securityCompactDTO)
    {
        if (this.securityCompactDTO != null && securityCompactDTO.name.equals(this.securityCompactDTO.name))
        {
            return;
            // Note that this prevents updating values inside the securityCompactDTO
        }

        this.securityCompactDTO = securityCompactDTO;
        stockName.setText(securityCompactDTO.name);
        exchangeSymbol.setText(securityCompactDTO.getExchangeSymbol());
        currencyDisplay.setText(securityCompactDTO.currencyDisplay);

        if (date != null)
        {
            date.setText(DateUtils.getFormatedTrendDate(this.securityCompactDTO.lastPriceDateAndTimeUtc));
        }

        double dLastPrice = YUtils.parseQuoteValue(securityCompactDTO.lastPrice.toString());
        if(!Double.isNaN(dLastPrice))
        {
            lastPrice.setText(String.format("%.2f", dLastPrice));
        }
        else
        {
            THLog.d(TAG, "TH: Unable to parse Last Price");
        }

        if(securityCompactDTO.pc50DMA > 0)
        {
            //profitIndicator.setText(getContext().getString(R.string.positive_prefix));
            profitIndicator.setText(R.string.positive_prefix);
        }
        else if(securityCompactDTO.pc50DMA < 0)
        {
            //profitIndicator.setText(getContext().getString(R.string.negative_prefix));
            profitIndicator.setText(R.string.negative_prefix);
        }

        profitIndicator.setTextColor(ColorUtils.getColorForPercentage(((float) securityCompactDTO.pc50DMA) / 5f));
        exchangeSymbol.setTextColor(getResources().getColor(R.color.exchange_symbol));

        if(securityCompactDTO.marketOpen)
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

        if (securityType != null && this.securityCompactDTO.getSecurityType() != null)
        {
            securityType.setText(securityCompactDTO.getSecurityTypeStringResourceId());
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
            countryLogo.setImageResource(securityCompactDTO.getExchangeLogoId());
        }

        storeImageUrlInImageViews();
    }

    private void storeImageUrlInImageViews()
    {
        if (stockLogo != null)
        {
            stockLogo.setTag(R.string.image_url, this.securityCompactDTO.imageBlobUrl);
        }

        if (stockBgLogo != null)
        {
            stockBgLogo.setTag(R.string.image_url, this.securityCompactDTO.imageBlobUrl);
        }
    }

    public void loadImages ()
    {
        if (isMyUrlOk())
        {
            loadImageInTarget(stockLogo, foregroundTransformation);
            // Launching the bg like this will result in double downloading the file.
            loadImageInTarget(stockBgLogo, backgroundTransformation, getMeasuredWidth(), getMeasuredHeight());
        }
        else
        {
            //THLog.i(TAG, "no url");
            mPicasso.load(securityCompactDTO.getExchangeLogoId())
                    .placeholder(R.drawable.default_image)
                    .error(R.drawable.default_image)
                    .into(stockLogo);
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


}
