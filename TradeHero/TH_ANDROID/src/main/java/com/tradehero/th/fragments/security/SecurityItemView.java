package com.tradehero.th.fragments.security;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import com.squareup.picasso.CallbackExt;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.common.utils.MetaHelper;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.market.Exchange;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.models.graphics.ForSecurityItemBackground2;
import com.tradehero.th.models.graphics.ForSecurityItemForeground;
import com.tradehero.th.utils.ColorUtils;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.DateUtils;
import javax.inject.Inject;
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
        if (stockBgLogo != null)
        {
            stockBgLogo.setVisibility(View.INVISIBLE);
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
        //if (this.securityCompactDTO != null && securityCompactDTO != null && securityCompactDTO.name.equals(this.securityCompactDTO.name))
        //{
        //    return;
        //    // Note that this prevents updating values inside the securityCompactDTO
        //}

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
                if (securityCompactDTO.lastPriceDateAndTimeUtc != null)
                {
                    date.setText(DateUtils.getFormattedUtcDate(getResources(), securityCompactDTO.lastPriceDateAndTimeUtc));
                }
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
            }
            catch (OutOfMemoryError e)
            {
                Timber.e(e, "");
            }
        }
    }
    //</editor-fold>

    public void loadImage()
    {
        if (stockBgLogo != null)
        {
            stockBgLogo.setVisibility(View.INVISIBLE);
            if (isMyUrlOk())
            {
                mPicasso.load(securityCompactDTO.imageBlobUrl)
                        //don't have use transform
                        .withMerge()
                        .placeholder(R.drawable.trending_grid_item_bg2)
                        .into(stockBgLogo, createBgImageCallback());
            }
            else
            {
                loadExchangeImage();
            }
        }
        else
        {
            //if stockBgLogo is null,we don't have to load image.
        }
    }

    public void loadExchangeImage()
    {
        if (stockBgLogo != null)
        {
            if (securityCompactDTO != null && securityCompactDTO.exchange != null)
            {
                try
                {
                    Exchange exchange = Exchange.valueOf(securityCompactDTO.exchange);
                    mPicasso.load(exchange.logoId)
                            .withMerge()
                            .placeholder(R.drawable.trending_grid_item_bg2)
                            .into(stockBgLogo, createExchangeImageCallback());
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
            stockBgLogo.setVisibility(View.INVISIBLE);
        }
    }

    private Bitmap resizeForegroundBitmap(
            float pw, float ph,
            Bitmap in, boolean recycle)
    {

        if (ph <= 0 || ph <= 0)
        {
            ph = getResources().getDimension(R.dimen.security_logo_height);
            pw = getResources().getDimension(R.dimen.security_logo_width);
        }

        int inWidth = in.getWidth();
        int inHeight = in.getHeight();
        float scaleY = ph / inHeight;
        float scaleX = pw / inWidth;
        float scale = Math.min(scaleX, scaleY);
        if (scale >= 1)
        {
            //Log.d(TAG, "scaleLogo bitmap"+inWidth+","+inHeight+" parent "+getWidth()+" "+getHeight());
            return in;
        }

        int dstWidth = (int) (inWidth * scale);
        int dstHeight = (int) (inHeight * scale);
        //Log.d(TAG, "scaleLogo scaleX "+scaleX+" scaleY "+scaleY+" scale "+scale+" dstWidth "+dstWidth+" dstHeight "+dstHeight+" parent "+getWidth()+" "+getHeight());
        Bitmap rt = Bitmap.createScaledBitmap(in, dstWidth, dstHeight, false);
        if (recycle)
        {
            in.recycle();
        }
        return rt;
    }

    /**
     * merge two bitmaps into single one.
     */
    private Bitmap mergeBitmaps(Bitmap background, Bitmap logo, boolean recycle, int w, int h)
    {
        if (background == null || logo == null || background.isRecycled() || logo.isRecycled())
        {
            return null;
        }
        Paint paint = new Paint();

        int bgW = background.getWidth();
        int bgH = background.getHeight();

        int top = (int) getResources().getDimension(R.dimen.security_logo_offset);
        int offX = (bgW - logo.getWidth()) / 2;
        int offY = (bgH - logo.getHeight()) / 2 - top;

        Canvas canvas = new Canvas(background);

        //draw icon
        canvas.drawBitmap(logo, offX, offY, paint);

        //Log.d(TAG, "bg:"+bgW+","+bgH+" target:"+w+","+h +" logo "+logo.getWidth()+","+logo.getHeight());
        //Bitmap retVal = Bitmap.createBitmap(background, offX, offY, w, h, null, false);
        if (recycle)
        {
            logo.recycle();
        }
        return background;
    }

    /**
     * resize background bitmap.
     */
    Bitmap resizeBackgroundBitmap(int targetWidth, int targetHeight,
            Bitmap result, int exifRotation, boolean recycle)
    {
        boolean swapDimens = exifRotation == 90 || exifRotation == 270;
        int inWidth = swapDimens ? result.getHeight() : result.getWidth();
        int inHeight = swapDimens ? result.getWidth() : result.getHeight();

        int drawX = 0;
        int drawY = 0;
        int drawWidth = inWidth;
        int drawHeight = inHeight;

        Matrix matrix = new Matrix();

        float widthRatio = targetWidth / (float) inWidth;
        float heightRatio = targetHeight / (float) inHeight;
        float scale;
        if (widthRatio > heightRatio)
        {
            scale = widthRatio;
            int newSize = (int) Math
                    .floor(inHeight * (heightRatio / widthRatio));
            drawY = (inHeight - newSize) / 2;
            drawHeight = newSize;
        }
        else
        {
            scale = heightRatio;
            int newSize = (int) Math.floor(inWidth * (widthRatio / heightRatio));
            drawX = (inWidth - newSize) / 2;
            drawWidth = newSize;
        }
        matrix.preScale(scale, scale);

        Bitmap newResult = Bitmap.createBitmap(result, drawX, drawY, drawWidth,
                drawHeight, matrix, true);
        if (newResult != result)
        {
            if (recycle)
            {
                result.recycle();
            }
            result = null;
            result = newResult;
        }

        return result;
    }

    protected CallbackExt createBgImageCallback()
    {
        return new SecurityItemViewBgImageCallback();
    }

    protected class SecurityItemViewBgImageCallback extends CallbackExt.EmptyCallbackExt
    {
        @Override public void onSuccess()
        {
            if (stockBgLogo != null)
            {
                stockBgLogo.setVisibility(View.VISIBLE);
            }
        }

        @Override public void onError()
        {
            loadExchangeImage();
        }

        @Override public Bitmap onOriginalBitmapLoaded(ImageView target, Bitmap bmp)
        {
            return processTransformation(target, bmp);
        }
    }

    protected CallbackExt createExchangeImageCallback()
    {
        return new SecurityItemViewExchangeImageCallback();
    }

    /**
     * Callback for loading exchange image
     */
    protected class SecurityItemViewExchangeImageCallback extends CallbackExt.EmptyCallbackExt
    {
        @Override public void onSuccess()
        {
            if (stockBgLogo != null)
            {
                stockBgLogo.setVisibility(View.VISIBLE);
            }
        }

        @Override public void onError()
        {
            //load default image if failed to load exchange image
            loadDefaultImage();
        }

        @Override public Bitmap onOriginalBitmapLoaded(ImageView target, Bitmap bmp)
        {
            return processTransformation(target, bmp);
        }
    }

    private Bitmap processTransformation(ImageView target, Bitmap bmp)
    {
        if (bmp == null || bmp.isRecycled())
        {
            return null;
        }
        try
        {
            System.gc();

            int originalWidth = bmp.getWidth();
            int originalHeight = bmp.getHeight();
            View parent = (View) target.getParent();
            int parentWidth = 0;
            int parentHeight = 0;
            //parentWidth may be 0?
            if ((parentWidth = parent.getWidth()) <= 0 || (parentHeight = parent.getHeight()) <= 0)
            {
                parentWidth = MetaHelper.getScreensize(getContext())[0];
                parentHeight =
                        Math.round(getResources().getDimension(R.dimen.security_item_height));
            }
            float scaleX = parentWidth / (float) originalWidth;
            float scaleY = parentHeight / (float) originalHeight;
            float scale = Math.max(scaleX, scaleY);

            Bitmap transformResult =
                    resizeBackgroundBitmap(parent.getWidth(), parent.getHeight(), bmp, 0, false);

            float ph = getResources().getDimension(R.dimen.security_logo_height);
            float pw = getWidth()
                    - getResources().getDimension(R.dimen.security_logo_margin_vertical) * 2;

            Bitmap logo =
                    foregroundTransformation.transform(resizeForegroundBitmap(pw, ph, bmp, false));
            Bitmap background = backgroundTransformation.transform(transformResult);

            Bitmap retVal =
                    mergeBitmaps(background, logo, false, parent.getWidth(), parent.getHeight());

            return retVal;
        }
        catch (OutOfMemoryError e)
        {
            Timber.e(e, "OutOfMemoryError");
            return null;
        }
    }
}
