package com.tradehero.th.fragments.security;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import com.squareup.picasso.Target;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.market.Exchange;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.utils.DateUtils;
import javax.inject.Inject;
import timber.log.Timber;

public class SecurityItemView extends RelativeLayout
        implements DTOView<SecurityCompactDTO>
{
    @Inject protected Picasso mPicasso;

    @InjectView(R.id.stock_logo) ImageView stockLogo;
    @InjectView(R.id.ic_market_close) @Optional ImageView marketCloseIcon;
    @InjectView(R.id.stock_name) TextView stockName;
    @InjectView(R.id.exchange_symbol) TextView exchangeSymbol;
    @InjectView(R.id.last_price) TextView lastPrice;
    @InjectView(R.id.country_logo) @Optional ImageView countryLogo;
    @InjectView(R.id.date) @Optional TextView date;
    @InjectView(R.id.sec_type) @Optional TextView securityType;

    protected SecurityCompactDTO securityCompactDTO;
    private Target myLogoImageTarget;

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
        myLogoImageTarget = createLogoImageTarget();
    }

    protected void init()
    {
        HierarchyInjector.inject(this);
        ButterKnife.inject(this);
        stockLogo.setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        if (getMyLogoImageTarget() == null)
        {
            myLogoImageTarget = createLogoImageTarget();
        }
        if (mPicasso != null)
        {
            loadImage();
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        mPicasso.cancelRequest(myLogoImageTarget);

        myLogoImageTarget = null;

        if (mPicasso != null)
        {
            clearHandler();
        }
        if (stockLogo != null)
        {
            stockLogo.setImageDrawable(null);
        }
        if (countryLogo != null)
        {
            countryLogo.setImageDrawable(null);
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

    @Override public void display(final SecurityCompactDTO securityCompactDTO)
    {
        linkWith(securityCompactDTO, true);
    }

    public void linkWith(SecurityCompactDTO securityCompactDTO, boolean andDisplay)
    {
        this.securityCompactDTO = securityCompactDTO;

        if (andDisplay)
        {
            displayStockName();
            displayExchangeSymbol();
            displayDate();
            displayLastPrice();
            displayMarketClose();
            displaySecurityType();
            displayCountryLogo();
            Timber.d("onLinkWith");
            loadImage();
        }
    }

    //<editor-fold desc="Display Methods">
    public void display()
    {
        displayStockName();
        displayExchangeSymbol();
        displayDate();
        displayLastPrice();
        displayMarketClose();
        displaySecurityType();
        displayCountryLogo();
        Timber.d("onDisplay");
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
            exchangeSymbol.setTextColor(getResources().getColor(R.color.text_primary));
        }
    }

    public void displayDate()
    {
        if (date != null)
        {
            if (securityCompactDTO != null)
            {
                if (securityCompactDTO.lastPriceDateAndTimeUtc != null)
                {
                    date.setText(DateUtils.getFormattedUtcDate(getResources(),
                            securityCompactDTO.lastPriceDateAndTimeUtc));
                }
                if (securityCompactDTO.marketOpen != null)
                {
                    date.setTextColor(getResources().getColor(
                            securityCompactDTO.marketOpen ? R.color.text_primary : R.color.text_secondary));
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
            if (securityCompactDTO != null && securityCompactDTO.lastPrice != null && !Double.isNaN(
                    securityCompactDTO.lastPrice))
            {
                THSignedMoney.builder(securityCompactDTO.lastPrice)
                        .signTypeArrow()
                        .withValueColor(R.color.text_primary)
                        .withCurrencyColor(R.color.text_primary)
                        .withSignValue(securityCompactDTO.pc50DMA)
                        .currency(securityCompactDTO.currencyDisplay)
                        .withDefaultColor()
                        .boldValue()
                        .build()
                        .into(lastPrice);
            }
            else
            {
                lastPrice.setText(R.string.na);
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
        }
        else
        {
            if (marketCloseIcon != null)
            {
                marketCloseIcon.setVisibility(View.VISIBLE);
            }
        }
    }

    public void displaySecurityType()
    {
        if (securityType != null)
        {
            if (this.securityCompactDTO != null && this.securityCompactDTO.getSecurityTypeStringResourceId() != null)
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
            try
            {
                if (securityCompactDTO != null)
                {
                    countryLogo.setImageResource(securityCompactDTO.getExchangeLogoId());
                }
                else
                {
                    countryLogo.setImageResource(R.drawable.default_image);
                }
            } catch (OutOfMemoryError e)
            {
                Timber.e(e, "");
            }
        }
    }
    //</editor-fold>

    private void resetImage()
    {
        if (stockLogo != null)
        {
            stockLogo.setImageBitmap(null);
        }
    }

    public Target getMyLogoImageTarget()
    {
        return this.myLogoImageTarget;
    }

    public void loadImage()
    {
        resetImage();

        if (isMyUrlOk())
        {
            picassoSetupLogoParam(mPicasso.load(securityCompactDTO.imageBlobUrl)).into(getMyLogoImageTarget());
        }
        else
        {
            loadExchangeImage();
        }
    }

    public void loadExchangeImage()
    {
        if (securityCompactDTO != null && securityCompactDTO.exchange != null)
        {
            try
            {
                Exchange exchange = Exchange.valueOf(securityCompactDTO.exchange);

                mPicasso.load(exchange.logoId).into(getMyLogoImageTarget());
            } catch (IllegalArgumentException e)
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

    public void loadDefaultImage()
    {
        if (stockLogo != null)
        {
            stockLogo.setVisibility(View.VISIBLE);
            stockLogo.setImageResource(R.drawable.default_image);
        }
    }

    private RequestCreator picassoSetupLogoParam(@NonNull RequestCreator creator)
    {
        return creator
                .resizeDimen(R.dimen.security_logo_width, R.dimen.security_logo_height)
                .centerInside();
    }

    protected Target createLogoImageTarget()
    {
        return new SecurityItemViewExchangeImageTarget();
    }

    protected class SecurityItemViewExchangeImageTarget
            implements Target
    {

        @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
        {
            if (stockLogo != null)
            {
                stockLogo.setImageBitmap(bitmap);
            }
        }

        @Override public void onBitmapFailed(Drawable errorDrawable)
        {
            loadDefaultImage();
        }

        @Override public void onPrepareLoad(Drawable placeHolderDrawable)
        {

        }
    }

}
