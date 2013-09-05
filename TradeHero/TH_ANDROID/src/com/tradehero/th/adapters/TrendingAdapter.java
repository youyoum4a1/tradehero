package com.tradehero.th.adapters;

import com.loopj.android.image.SmartImageView;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.cache.ImageLoader;
import com.tradehero.th.utills.Logger;
import com.tradehero.th.utills.TrendUtils;
import com.tradehero.th.utills.YUtils;
import java.util.List;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TrendingAdapter extends ArrayAdapter<SecurityCompactDTO>
{
    private final static String TAG = TrendingAdapter.class.getSimpleName();
    static private ImageLoader mImageLoader;

    public TrendingAdapter(Context context, List<SecurityCompactDTO> trendList)
    {
        super(context, 0, trendList);
        if (mImageLoader == null)
        {
            mImageLoader = new ImageLoader(context);
        }
    }

    @SuppressWarnings("deprecation")
    public View getView(final int position, View convertView,
            ViewGroup parent)
    {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.trending_grid_item, null);
        }

        TextView stockName = (TextView) convertView.findViewById(R.id.stock_name);
        TextView exchangeSymbol = (TextView) convertView.findViewById(R.id.exchange_symbol);
        TextView profitIndicator = (TextView) convertView.findViewById(R.id.profit_indicator);
        TextView currencyDisplay = (TextView) convertView.findViewById(R.id.currency_display);
        TextView lastPrice = (TextView) convertView.findViewById(R.id.last_price);
        //ImageView marketCloseIcon = (ImageView) convertView.findViewById(R.id.ic_market_close);
        final SmartImageView stockLogo = (SmartImageView) convertView.findViewById(R.id.stock_logo);
        final SmartImageView stockBgLogo = (SmartImageView) convertView.findViewById(R.id.stock_bg_logo);

        final SecurityCompactDTO trend = getItem(position);

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

        stockBgLogo.setAlpha(26); //15% opaque		
        final View finalisedConvertView = convertView;

        convertView.post(new Runnable()
        {
            @Override
            public void run()
            {
                //loadImage (trend, finalisedConvertView, stockLogo, stockBgLogo);
                loadSmartImage(trend, stockLogo, stockBgLogo);
            }
        });
        //loadImage (trend, finalisedConvertView, stockLogo, stockBgLogo);
        return convertView;
    }

    private void loadSmartImage (final SecurityCompactDTO trend, final SmartImageView stockLogo, final SmartImageView stockBgLogo)
    {
        if (trend.imageBlobUrl != null && trend.imageBlobUrl.length() > 0)
        {
            stockBgLogo.setImageUrl(trend.imageBlobUrl);
            stockLogo.setImageUrl(trend.imageBlobUrl);
        }
        else
        {
            stockLogo.setImageResource(R.drawable.default_image);
            stockBgLogo.setImageResource(R.drawable.default_image);
        }
    }

    public void loadImage (SecurityCompactDTO trend, final View trendingView, final ImageView stockLogo, final ImageView stockBgLogo)
    {
        if (trend.imageBlobUrl != null && trend.imageBlobUrl.length() > 0)
        {
            mImageLoader.getBitmapImage(trend.imageBlobUrl, createLoadingListener(trendingView, stockLogo, stockBgLogo));
        }
        else
        {
            THLog.d(TAG, "No Image url: " + trend.name);
            stockLogo.setImageResource(R.drawable.default_image);
            stockBgLogo.setImageResource(R.drawable.default_image);
            trendingView.invalidate();
        }
    }

    private ImageLoader.ImageLoadingListener createLoadingListener (final View trendingView, final ImageView stockLogo, final ImageView stockBgLogo)
    {
        return new ImageLoader.ImageLoadingListener()
        {
            public void onLoadingComplete(Bitmap b)
            {
                //final Bitmap bCleaned =
                //        ImageUtils.convertToMutableAndRemoveBackground(loadedImage);
                if (b != null)
                {
                    stockLogo.setImageBitmap(b);
                    stockBgLogo.setImageBitmap(b);
                    trendingView.invalidate();
                }
            }
        };
    }

    public Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        //ColorMatrix cm = new ColorMatrix();
        ColorMatrix cm = new ColorMatrix(
                new float[]{
                        0.5f,0.5f,0.5f,0,0,
                        0.5f,0.5f,0.5f,0,0,
                        0.5f,0.5f,0.5f,0,0,
                        0,0,0,1,0,0,
                        0,0,0,0,1,0
                });
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }
}