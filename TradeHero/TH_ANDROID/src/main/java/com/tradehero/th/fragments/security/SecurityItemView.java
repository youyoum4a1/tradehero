package com.tradehero.th.fragments.security;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.market.Exchange;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.models.graphics.ForSecurityItemBackground;
import com.tradehero.th.models.graphics.ForSecurityItemForeground;
import com.tradehero.th.utils.ColorUtils;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.DateUtils;
import javax.inject.Inject;
import timber.log.Timber;

/** Created with IntelliJ IDEA. User: xavier Date: 9/5/13 Time: 5:19 PM To change this template use File | Settings | File Templates. */
public class SecurityItemView<SecurityCompactDTOType extends SecurityCompactDTO> extends RelativeLayout
        implements DTOView<SecurityCompactDTOType>
{
    public static final float DIVISOR_PC_50_COLOR = 5f;
    public static final int MS_DELAY_FOR_BG_IMAGE  = 200;

    @Inject @ForSecurityItemForeground Transformation foregroundTransformation;
    @Inject @ForSecurityItemBackground Transformation backgroundTransformation;

    @Inject protected Picasso mPicasso;

    @InjectView(R.id.stock_bg_logo) @Optional ImageView stockBgLogo;
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

    protected void init()
    {
        DaggerUtils.inject(this);
        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (stockBgLogo != null)
        {
            stockBgLogo.setVisibility(GONE);
        }
        if (mPicasso != null)
        {
            loadImage();
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        if (mPicasso != null)
        {
            loadDefaultImage();
            loadBgDefault();
            clearHandler();
        }
        super.onDetachedFromWindow();
    }

    protected void clearHandler()
    {
        Handler handler = getHandler();
        if (handler != null)
        {
            handler.removeCallbacks(null);
        }
    }

    public boolean isMyUrlOk()
    {
        return (securityCompactDTO != null) && isUrlOk(securityCompactDTO.imageBlobUrl);
    }

    public static boolean isUrlOk(String url)
    {
        return (url != null) && (!url.isEmpty());
    }

    @Override public void display(final SecurityCompactDTOType securityCompactDTO)
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
            loadImage();
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
        loadImage();
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
                    Timber.w("displayProfitIndicator, pc50DMA was null");
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

    public void displayMarketClose()
    {
        if (securityCompactDTO == null)
        {
            // Nothing to do
        }
        else if (securityCompactDTO.marketOpen == null)
        {
            Timber.w("displayMarketClose marketOpen is null");
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

    public void loadImage()
    {
        if (stockLogo != null)
        {
            if (stockBgLogo != null)
            {
                stockBgLogo.setVisibility(GONE);
            }
            if (isMyUrlOk())
            {
                mPicasso.load(securityCompactDTO.imageBlobUrl)
                        .transform(foregroundTransformation)
                        .into(stockLogo, new Callback()
                        {
                            @Override public void onSuccess()
                            {
                                //loadBgImageDelayed();
                            }

                            @Override public void onError()
                            {
                                loadExchangeImage();
                            }
                        });
            }
            else
            {
                loadExchangeImage();
            }
        }
        else
        {
            //loadBgImageDelayed();
        }
    }

    public void loadExchangeImage()
    {
        if (stockLogo != null)
        {
            if (securityCompactDTO != null && securityCompactDTO.exchange != null)
            {
                try
                {
                    Exchange exchange = Exchange.valueOf(securityCompactDTO.exchange);
                    stockLogo.setImageResource(exchange.logoId);
                    //loadBgImageDelayed();
                }
                catch (IllegalArgumentException e)
                {
                    Timber.e("Unknown Exchange %s", securityCompactDTO.exchange, e);
                    loadDefaultImage();
                }
            }
            else
            {
                loadDefaultImage();
            }
        }
        else
        {
            //loadBgImageDelayed();
        }
    }

    public void loadDefaultImage()
    {
        if (stockLogo != null)
        {
            stockLogo.setImageResource(R.drawable.default_image);
        }
        //loadBgImageDelayed();
    }

    public void loadBgImageDelayed()
    {
        clearHandler();
        postDelayed(new Runnable()
        {
            @Override public void run()
            {
                loadBgImage();
            }
        }, MS_DELAY_FOR_BG_IMAGE);
    }

    public void loadBgImage()
    {
        if (stockBgLogo != null)
        {
            if (isMyUrlOk())
            {
                RequestCreator requestCreator = mPicasso.load(securityCompactDTO.imageBlobUrl)
                        .transform(backgroundTransformation);
                resizeBackground(requestCreator, stockBgLogo,new Callback()
                            {
                                @Override public void onSuccess()
                                {
                                    stockBgLogo.setVisibility(VISIBLE);
                                }

                                @Override public void onError()
                                {
                                    loadBgExchange();
                                }
                            });
            }
            else
            {
                loadBgExchange();
            }
        }
    }

    public void loadBgExchange()
    {
        if (stockBgLogo != null)
        {
            if (securityCompactDTO != null && securityCompactDTO.exchange != null)
            {
                try
                {
                    Exchange exchange = Exchange.valueOf(securityCompactDTO.exchange);
                    RequestCreator requestCreator = mPicasso.load(exchange.logoId)
                            .transform(backgroundTransformation);
                    resizeBackground(requestCreator, stockBgLogo, null);
                    stockBgLogo.setVisibility(VISIBLE);
                }
                catch (IllegalArgumentException e)
                {
                    loadBgDefault();
                }
            }
            else
            {
                loadBgDefault();
            }
        }
    }

    public void loadBgDefault()
    {
        if (stockBgLogo != null)
        {
            stockBgLogo.setImageResource(R.drawable.default_image);
        }
    }

    protected void resizeBackground(RequestCreator requestCreator, ImageView imageView, Callback callback)
    {
        int width = getWidth();
        int height = getHeight();
        if (width > 0 && height > 0)
        {
            requestCreator.resize(width, height)
                    .centerCrop()
                    .into(imageView, callback);
        }
    }
}
