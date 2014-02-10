package com.tradehero.th.fragments.security;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;
import com.tradehero.common.thread.KnownExecutorServices;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.models.graphics.ForSecurityItemBackground;
import com.tradehero.th.models.graphics.ForSecurityItemForeground;
import com.tradehero.th.utils.ColorUtils;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.DateUtils;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 9/5/13 Time: 5:19 PM To change this template use File | Settings | File Templates. */
public class SecurityItemView<SecurityCompactDTOType extends SecurityCompactDTO> extends RelativeLayout
        implements DTOView<SecurityCompactDTOType>
{
    private static final String TAG = SecurityItemView.class.getSimpleName();
    public static final float DIVISOR_PC_50_COLOR = 5f;

    @Inject @ForSecurityItemForeground Transformation foregroundTransformation;
    @Inject @ForSecurityItemBackground Transformation backgroundTransformation;

    @Inject protected Picasso mPicasso;

    @InjectView(R.id.stock_bg_logo) ImageView stockBgLogo;
    @InjectView(R.id.stock_logo) ImageView stockLogo;
    @InjectView(R.id.ic_market_close) ImageView marketCloseIcon;
    @InjectView(R.id.stock_name) TextView stockName;
    @InjectView(R.id.exchange_symbol) TextView exchangeSymbol;
    @InjectView(R.id.profit_indicator) @Optional TextView profitIndicator;
    @InjectView(R.id.currency_display) TextView currencyDisplay;
    @InjectView(R.id.last_price) TextView lastPrice;
    @InjectView(R.id.country_logo) @Optional ImageView countryLogo;
    @InjectView(R.id.date) @Optional TextView date;
    @InjectView(R.id.sec_type) @Optional TextView securityType;

    protected SecurityCompactDTOType securityCompactDTO;

    private Runnable loadBgLogoRunnable = new Runnable()
    {
        @Override public void run()
        {
            loadImageInTarget(stockLogo, foregroundTransformation);
            // Posting it so the view has properly sized
            loadImageInTarget(stockBgLogo, backgroundTransformation, getMeasuredWidth(), getMeasuredHeight());
        }
    };

    //<editor-fold desc="Constructors">
    public SecurityItemView(Context context)
    {
        super(context);
    }

    public SecurityItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SecurityItemView(Context context, AttributeSet attrs, int defStyle)
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
        ButterKnife.inject(this);
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

    @Override public void display (final SecurityCompactDTOType securityCompactDTO)
    {
        linkWith(securityCompactDTO, true);
    }

    public void linkWith(SecurityCompactDTOType securityCompactDTO, boolean andDisplay)
    {
        if (this.securityCompactDTO != null && securityCompactDTO != null && securityCompactDTO.name.equals(this.securityCompactDTO.name))
        {
            return;
            // Note that this prevents updating values inside the securityCompactDTO
        }

        this.securityCompactDTO = securityCompactDTO;

        if (andDisplay)
        {
            displayStockName();
            displayExchangeSymbol();
            displayCurrencyDisplay();
            displayDate();
            displayLastPrice();
            displayProfitIndicator();
            displayMarketClose();
            displaySecurityType();
            displayCountryLogo();
            storeImageUrlInImageViews();
            loadImages();
        }
    }

    //<editor-fold desc="Display Methods">
    public void display()
    {
        displayStockName();
        displayExchangeSymbol();
        displayCurrencyDisplay();
        displayDate();
        displayLastPrice();
        displayProfitIndicator();
        displayMarketClose();
        displaySecurityType();
        displayCountryLogo();
        storeImageUrlInImageViews();
        loadImages();
    }

    public void displayStockName()
    {
        if (stockName != null)
        {
            if (securityCompactDTO != null)
            {
                stockName.setText(securityCompactDTO.name);
            }
            else
            {
                stockName.setText(R.string.na);
            }
        }
    }

    public void displayExchangeSymbol()
    {
        if (exchangeSymbol != null)
        {
            if (securityCompactDTO != null)
            {
                exchangeSymbol.setText(securityCompactDTO.getExchangeSymbol());
            }
            else
            {
                exchangeSymbol.setText(R.string.na);
            }
            exchangeSymbol.setTextColor(getResources().getColor(R.color.exchange_symbol));
        }
    }

    public void displayCurrencyDisplay()
    {
        if (currencyDisplay != null)
        {
            if (securityCompactDTO != null)
            {
                currencyDisplay.setText(securityCompactDTO.currencyDisplay);
            }
            else
            {
                currencyDisplay.setText(R.string.na);
            }
        }
    }

    public void displayDate()
    {
        if (date != null)
        {
            if (securityCompactDTO != null)
            {
                date.setText(DateUtils.getFormattedUtcDate(this.securityCompactDTO.lastPriceDateAndTimeUtc));
                if (securityCompactDTO.marketOpen != null)
                {
                    date.setTextColor(getResources().getColor(securityCompactDTO.marketOpen ? R.color.black : R.color.text_gray_normal));
                }
            }
            else
            {
                date.setText(R.string.na);
            }
        }
    }

    public void displayLastPrice()
    {
        if (lastPrice != null)
        {
            if (securityCompactDTO != null && securityCompactDTO.lastPrice != null && !Double.isNaN(securityCompactDTO.lastPrice))
            {
                lastPrice.setText(String.format("%.2f", securityCompactDTO.lastPrice));
            }
            else
            {
                lastPrice.setText(R.string.na);
                THLog.d(TAG, "TH: Unable to parse Last Price");
            }
        }
    }

    public void displayProfitIndicator()
    {
        if (profitIndicator != null)
        {
            if (securityCompactDTO != null)
            {
                if (securityCompactDTO.pc50DMA == null)
                {
                    THLog.w(TAG, "displayProfitIndicator, pc50DMA was null");
                }
                else
                {
                    if (securityCompactDTO.pc50DMA > 0)
                    {
                        //profitIndicator.setText(getContext().getString(R.string.positive_prefix));
                        profitIndicator.setText(R.string.arrow_prefix_positive);
                    }
                    else if (securityCompactDTO.pc50DMA < 0)
                    {
                        //profitIndicator.setText(getContext().getString(R.string.negative_prefix));
                        profitIndicator.setText(R.string.arrow_prefix_negative);
                    }
                    profitIndicator.setTextColor(ColorUtils.getColorForPercentage(((float) securityCompactDTO.pc50DMA) / DIVISOR_PC_50_COLOR));
                }
            }
        }
    }

    public void storeImageUrlInImageViews()
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

    public void displayMarketClose()
    {
        if (securityCompactDTO == null)
        {
            // Nothing to do
        }
        else if (securityCompactDTO.marketOpen == null)
        {
            THLog.w(TAG, "displayMarketClose marketOpen is null");
        }
        else if (securityCompactDTO.marketOpen)
        {
            if (marketCloseIcon != null)
            {
                marketCloseIcon.setVisibility(View.GONE);
            }
            if (currencyDisplay != null)
            {
                currencyDisplay.setTextColor(getResources().getColor(R.color.exchange_symbol));
            }
            if (lastPrice != null)
            {
                lastPrice.setTextColor(getResources().getColor(R.color.exchange_symbol));
            }
        }
        else
        {
            if (marketCloseIcon != null)
            {
                marketCloseIcon.setVisibility(View.VISIBLE);
            }
            if (currencyDisplay != null)
            {
                currencyDisplay.setTextColor(getResources().getColor(android.R.color.darker_gray));
            }
            if (lastPrice != null)
            {
                lastPrice.setTextColor(getResources().getColor(android.R.color.darker_gray));
            }
        }
    }

    public void displaySecurityType()
    {
        if (securityType != null)
        {
            if (this.securityCompactDTO != null && this.securityCompactDTO.getSecurityType() != null)
            {
                securityType.setText(securityCompactDTO.getSecurityTypeStringResourceId());
            }
            else
            {
                securityType.setText(R.string.na);
            }
        }
    }

    public void displayCountryLogo()
    {
        if (countryLogo != null)
        {
            if (securityCompactDTO != null)
            {
                countryLogo.setImageResource(securityCompactDTO.getExchangeLogoId());
            }
            else
            {
                countryLogo.setImageResource(R.drawable.default_image);
            }
        }
    }
    //</editor-fold>

    public void loadImages ()
    {
        if (stockLogo != null)
        {
            stockLogo.setImageResource(R.drawable.default_image);
        }

        if (stockBgLogo != null)
        {
            stockBgLogo.setImageResource(R.drawable.default_image);
        }

        if (isMyUrlOk())
        {
            loadImageInTarget(stockLogo, foregroundTransformation);
            // Launching the bg like this will result in double downloading the file.
            removeCallbacks(loadBgLogoRunnable); // In order to further delay the background
            postDelayed(loadBgLogoRunnable, 200);
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
