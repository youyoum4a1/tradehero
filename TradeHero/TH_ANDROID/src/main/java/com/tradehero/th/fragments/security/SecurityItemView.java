package com.tradehero.th.fragments.security;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
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
import com.squareup.picasso.Transformation;
import com.tradehero.thm.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.market.Exchange;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.models.graphics.ForSecurityItemBackground2;
import com.tradehero.th.models.graphics.ForSecurityItemForeground;
import com.tradehero.th.utils.ColorUtils;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.DateUtils;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

public class SecurityItemView<SecurityCompactDTOType extends SecurityCompactDTO>
        extends RelativeLayout
        implements DTOView<SecurityCompactDTOType>
{

    public static final float DIVISOR_PC_50_COLOR = 5f;

    @Inject @ForSecurityItemForeground Transformation foregroundTransformation;
    @Inject @ForSecurityItemBackground2 Transformation backgroundTransformation;

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
    private Target myLogoImageTarget;
    private Target myBgImageTarget;

    //<editor-fold desc="Constructors">
    public SecurityItemView(Context context)
    {
        super(context);
    }

    public SecurityItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        //setBackgroundResource(R.drawable.trending_grid_item_bg);
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
        myBgImageTarget = createBGImageTarget();
    }

    protected void init()
    {
        DaggerUtils.inject(this);
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
        if (getMyBgImageTarget() == null)
        {
            myBgImageTarget = createBGImageTarget();
        }

        if (stockBgLogo != null)
        {
            stockBgLogo.setVisibility(View.GONE);
        }

        if (mPicasso != null)
        {
            loadImage();
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        mPicasso.cancelRequest(myLogoImageTarget);
        mPicasso.cancelRequest(myBgImageTarget);

        myLogoImageTarget = null;
        myBgImageTarget = null;

        if (mPicasso != null)
        {
            clearHandler();
        }
        if (stockLogo != null)
        {
            stockLogo.setImageDrawable(null);
        }
        if (stockBgLogo != null)
        {
            stockBgLogo.setImageDrawable(null);
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

    @Override public void display(final SecurityCompactDTOType securityCompactDTO)
    {
        linkWith(securityCompactDTO, true);
    }

    public void linkWith(SecurityCompactDTOType securityCompactDTO, boolean andDisplay)
    {
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
            Timber.d("onLinkWith");
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
                if (securityCompactDTO.lastPriceDateAndTimeUtc != null)
                {
                    date.setText(DateUtils.getFormattedUtcDate(getResources(),
                            securityCompactDTO.lastPriceDateAndTimeUtc));
                }
                if (securityCompactDTO.marketOpen != null)
                {
                    date.setTextColor(getResources().getColor(
                            securityCompactDTO.marketOpen ? R.color.black : R.color.text_gray_normal));
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
                    profitIndicator.setTextColor(ColorUtils.getProperColorForNumber(((float) securityCompactDTO.pc50DMA) / DIVISOR_PC_50_COLOR));
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

        if (stockBgLogo != null)
        {
            stockBgLogo.setImageBitmap(null);
        }
    }

    public Target getMyBgImageTarget()
    {
        return this.myBgImageTarget;
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
            picassoSetupBGParam(mPicasso.load(securityCompactDTO.imageBlobUrl)).into(getMyBgImageTarget());
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
                picassoSetupBGParam(mPicasso.load(exchange.logoId)).into(getMyBgImageTarget());
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
        if (stockBgLogo != null)
        {
            stockBgLogo.setVisibility(View.GONE);
        }
    }

    private RequestCreator picassoSetupLogoParam(@NotNull RequestCreator creator)
    {
        return creator.transform(foregroundTransformation)
                .resizeDimen(R.dimen.security_logo_width, R.dimen.security_logo_height)
                .centerInside();
    }

    private RequestCreator picassoSetupBGParam(@NotNull RequestCreator creator)
    {
        return creator.transform(backgroundTransformation)
                .resizeDimen(R.dimen.security_logo_width, R.dimen.security_logo_height)
                .centerCrop();
    }

    protected Target createBGImageTarget()

    {
        return new SecurityItemViewBgImageTarget();
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

    protected class SecurityItemViewBgImageTarget
            implements Target
    {

        @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
        {
            if (stockBgLogo != null)
            {
                stockBgLogo.setImageBitmap(bitmap);
                stockBgLogo.setVisibility(View.VISIBLE);
            }
            else
            {
            }
        }

        @Override public void onBitmapFailed(Drawable errorDrawable)
        {
        }

        @Override public void onPrepareLoad(Drawable placeHolderDrawable)
        {

        }
    }
}
