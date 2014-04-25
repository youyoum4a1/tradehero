package com.tradehero.th.fragments.security;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
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
import com.tradehero.th.BuildConfig;
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

/** Created with IntelliJ IDEA. User: xavier Date: 9/5/13 Time: 5:19 PM To change this template use File | Settings | File Templates. */
public class SecurityItemView<SecurityCompactDTOType extends SecurityCompactDTO> extends RelativeLayout
        implements DTOView<SecurityCompactDTOType>
{
    public static final float DIVISOR_PC_50_COLOR = 5f;
    public static final int MS_DELAY_FOR_BG_IMAGE  = 200;

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
    //for test
    static String TAG ="SecurityItemView";

    private ImageCallback callback;
    private ExchangeImageCallback exchangeImageCallback;
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
        //setBackgroundResource(R.drawable.trending_grid_item_bg);
        this.callback = new ImageCallback();
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
            //loadDefaultImage();
            //loadBgDefault();
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
        //setBackgroundResource(R.drawable.trending_grid_item_bg);
        //Log.d(TAG, "LruMemFileCache " +TAG+ "  loadImage url:"+securityCompactDTO.imageBlobUrl);
        if (stockBgLogo != null)
        {
            if (stockBgLogo != null)
            {
                stockBgLogo.setVisibility(View.INVISIBLE);

            }
            //just for test
            if(BuildConfig.DEBUG && securityCompactDTO.name.contains("Cannabis")){
                Log.d(securityCompactDTO.name,"testKey");
                callback.setTest(true);
                callback.setTestKey(securityCompactDTO.name);
            }
            //just for test
            if (isMyUrlOk())
            {
                //just for test
                if(callback.isTest()){
                    Log.d(securityCompactDTO.name,"testKey ,intent to load image");
                }
                mPicasso.load(securityCompactDTO.imageBlobUrl)
                        //don't have use transform
                        //.transform(foregroundTransformation)
                        .withMerge()
                        .placeholder(R.drawable.trending_grid_item_bg2)
                        .into(stockBgLogo, callback);
            }
            else
            {
                if(callback.isTest()){
                    Log.d(securityCompactDTO.name,"testKey ,url is not OK so loadExchangeImage");
                }
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
        //Log.d(TAG, "LruMemFileCache " +TAG+ "  loadBgImage");
        if (stockBgLogo != null)
        {
            if (securityCompactDTO != null && securityCompactDTO.exchange != null)
            {
                try
                {
                    if(exchangeImageCallback == null) {
                        exchangeImageCallback = new ExchangeImageCallback();
                    }
                    if(callback.isTest()){
                        exchangeImageCallback.setTest(true);
                        exchangeImageCallback.setTestKey(callback.getTestKey());
                    }
                    if(exchangeImageCallback.isTest()){
                        Log.d(securityCompactDTO.name,"testKey ,loading exchange");
                    }
                    Exchange exchange = Exchange.valueOf(securityCompactDTO.exchange);
                    //stockLogo.setImageResource(exchange.logoId);
                    mPicasso.load(exchange.logoId)
                            //.transform(foregroundTransformation)
                            .withMerge()
                            .placeholder(R.drawable.trending_grid_item_bg2)
                            .into(stockBgLogo, exchangeImageCallback);


                }
                catch (IllegalArgumentException e)
                {
                    Timber.e("Unknown Exchange %s", securityCompactDTO.exchange, e);
                    Log.d(securityCompactDTO.name,"testKey ,exchange error,loadDefaultImage",e);
                    loadDefaultImage();
                }
            }
            else
            {
                Log.d(securityCompactDTO.name,"testKey ,exchange error,loadDefaultImage");
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
        if(stockBgLogo != null){
            stockBgLogo.setVisibility(View.INVISIBLE);
        }
    }


    private Bitmap resizeForegroundBitmap(
            float pw,float ph,
            Bitmap in,boolean recycle){

        if(ph <=0 || ph <=0){
            ph = getResources().getDimension(R.dimen.security_logo_height);
            pw = getResources().getDimension(R.dimen.security_logo_width);
        }

        int inWidth = in.getWidth();
        int inHeight = in.getHeight();
        float scaleY = ph / inHeight;
        float scaleX = pw / inWidth;
        float scale = Math.min(scaleX, scaleY);
        if(scale >= 1){
            //Log.d(TAG, "scaleLogo bitmap"+inWidth+","+inHeight+" parent "+getWidth()+" "+getHeight());
            return in;
        }

        int dstWidth = (int) (inWidth * scale);
        int dstHeight = (int) (inHeight * scale);
        //Log.d(TAG, "scaleLogo scaleX "+scaleX+" scaleY "+scaleY+" scale "+scale+" dstWidth "+dstWidth+" dstHeight "+dstHeight+" parent "+getWidth()+" "+getHeight());
        Bitmap rt = Bitmap.createScaledBitmap(in, dstWidth, dstHeight, false);
        if(recycle){
            in.recycle();
        }
        return rt;
    }

    /**
     * merge two bitmaps into single one.
     * @param background
     * @param logo
     * @param recycle
     * @param w
     * @param h
     * @return
     */
    private Bitmap mergeBitmaps(Bitmap background,Bitmap logo,boolean recycle,int w,int h){
        if(background == null || logo == null || background.isRecycled() || logo.isRecycled()){
            return null;
        }
        Paint paint = new Paint();

        int bgW = background.getWidth();
        int bgH = background.getHeight();

        int top = (int)getResources().getDimension(R.dimen.security_logo_offset);
        int offX = (bgW - logo.getWidth()) / 2;
        int offY = (bgH - logo.getHeight()) / 2 - top;

        Canvas canvas = new Canvas(background);

        //draw icon
        canvas.drawBitmap(logo, offX, offY, paint);

        //Log.d(TAG, "bg:"+bgW+","+bgH+" target:"+w+","+h +" logo "+logo.getWidth()+","+logo.getHeight());
        //Bitmap retVal = Bitmap.createBitmap(background, offX, offY, w, h, null, false);
        if(recycle){
            logo.recycle();
        }
        return background;
    }

    /**
     * resize background bitmap.
     * @param targetWidth
     * @param targetHeight
     * @param result
     * @param exifRotation
     * @param recycle
     * @return
     */
    Bitmap reszieBackgroundBitmap(int targetWidth, int targetHeight,
            Bitmap result, int exifRotation,boolean recycle) {
        //Log.i(TAG, "targetWidth "+targetWidth+" targetHeight "+targetHeight+" original "+result.getWidth() +","+result.getHeight());
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
        if (widthRatio > heightRatio) {
            scale = widthRatio;
            int newSize = (int) Math
                    .floor(inHeight * (heightRatio / widthRatio));
            drawY = (inHeight - newSize) / 2;
            drawHeight = newSize;
            //            Log.d(TAG, String.format("transformResult 1:targetWidth:%s,inWidth:%s,targetHeight:%s,inHeight:%s,scale:%s, drawX:%s, drawY:%s, drawWidth:%s, drawHeight:%s",
            //                    targetWidth,inWidth,targetHeight,inHeight,scale,drawX, drawY, drawWidth, drawHeight));
        } else {
            scale = heightRatio;
            int newSize = (int) Math.floor(inWidth * (widthRatio / heightRatio));
            drawX = (inWidth - newSize) / 2;
            drawWidth = newSize;
            //            Log.d(TAG, String.format("transformResult 2:targetWidth:%s,inWidth:%s,targetHeight:%s,inHeight:%s,scale:%s, drawX:%s, drawY:%s, drawWidth:%s, drawHeight:%s",
            //                    targetWidth,inWidth,targetHeight,inHeight,scale,drawX, drawY, drawWidth, drawHeight));
        }
        matrix.preScale(scale, scale);

        Bitmap newResult = Bitmap.createBitmap(result, drawX, drawY, drawWidth,
                drawHeight, matrix, true);
        if (newResult != result ) {
            if(recycle){
                result.recycle();
            }
            result = null;
            result = newResult;
        }

        return result;
    }

    class ImageCallback extends CallbackExt.EmptyCallbackExt {
        boolean isTest = false;
        String testKey = null;
        @Override
        public void onSuccess() {
            if(isTest()){
                Log.d(securityCompactDTO.name,"testKey ,load image success");
            }
            if(stockBgLogo != null){
                //setBackgroundDrawable(null);
                stockBgLogo.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onError() {
            if(isTest()){
                Log.d(securityCompactDTO.name,"testKey ,load image error,loadExchangeImage");
            }
            loadExchangeImage();
        }

        @Override
        public boolean isTest() {
            return isTest;
        }

        /**
         *
         * @return
         */
        @Override
        public String getTestKey() {
            return testKey;
        }

        /**
         * for test
         * @param test
         */
        public  void setTest(boolean test){
            this.isTest = test;
        }

        /**
         * for test
         * @param testKey
         */
        public  void setTestKey(String testKey){
            this.testKey = testKey;
        }

        @Override
        public Bitmap onOriginalBitmapLoaded(ImageView target, Bitmap bmp) {
            return processTransformation(target, bmp);
        }


    }

    /**
     * Callback for loading exchange image
     */
    class ExchangeImageCallback extends CallbackExt.EmptyCallbackExt {
        boolean isTest = false;
        String testKey = null;
        @Override
        public void onSuccess() {
            if(isTest()){
                Log.d(securityCompactDTO.name,"testKey ,load exchange success");
            }
            if(stockBgLogo != null){
                //setBackgroundDrawable(null);
                stockBgLogo.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onError() {
            if(isTest()){
                Log.d(securityCompactDTO.name,"testKey ,load loadDefaultImage ");
            }
            //load default image if failed to load exchange image
            loadDefaultImage();
        }

        @Override
        public boolean isTest() {
            return isTest;
        }

        /**
         *
         * @return
         */
        @Override
        public String getTestKey() {
            return testKey;
        }

        /**
         * for test
         * @param test
         */
        public  void setTest(boolean test){
            this.isTest = test;
        }

        /**
         * for test
         * @param testKey
         */
        public  void setTestKey(String testKey){
            this.testKey = testKey;
        }

        @Override
        public Bitmap onOriginalBitmapLoaded(ImageView target, Bitmap bmp) {
            return processTransformation(target, bmp);
        }

    }

    private Bitmap processTransformation(ImageView target, Bitmap bmp) {

        if(bmp == null || bmp.isRecycled()){
            return null;
        }
        try {
            System.gc();

            int originalWidth = bmp.getWidth();
            int originalHeight = bmp.getHeight();
            View parent = (View)target.getParent();
            int parentWidth = 0;
            int parentHeight = 0;
            //parentWidth may be 0?
            if ((parentWidth = parent.getWidth()) <= 0 || (parentHeight = parent.getHeight()) <= 0)
            {
                parentWidth = MetaHelper.getScreensize(getContext())[0];
                parentHeight = Math.round(getResources().getDimension(R.dimen.security_item_height));
            }
            float scaleX = parentWidth / (float)originalWidth;
            float scaleY = parentHeight / (float)originalHeight;
            float scale = Math.max(scaleX, scaleY);

            Bitmap transformResult = reszieBackgroundBitmap(parent.getWidth(), parent.getHeight(), bmp, 0,false);

            float ph = getResources().getDimension(R.dimen.security_logo_height);
            float pw = getWidth() - getResources().getDimension(R.dimen.security_logo_margin_vertical) * 2;

            Bitmap logo = foregroundTransformation.transform(resizeForegroundBitmap(pw,ph,bmp, false));
            Bitmap background = backgroundTransformation.transform(transformResult);

            Bitmap retVal =  mergeBitmaps(background, logo, false,parent.getWidth(),parent.getHeight());
            if(true || callback.isTest())
            {
                Log.i("onOriginalBitmapLoaded", String.format("original:%s,%s;scale:%s;scaled:%s,%s;transformed:%s,%s;parent:%s,%s",
                        originalWidth, originalHeight,
                        scale,
                        transformResult.getWidth(), transformResult.getHeight(),
                        background.getWidth(), background.getHeight(),
                        parent.getWidth(), parent.getHeight())
                );
                Log.i("onOriginalBitmapLoaded", String.format("retVal:%s,%s",
                        retVal.getWidth(),retVal.getHeight())
                );
            }

            return retVal;
        }catch (OutOfMemoryError e){
            Timber.e("SecurityItemView","OutOfMemoryError",e);
            return null;
        }
    }
}
