package com.tradehero.th.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import com.tradehero.th.R;
import com.tradehero.th.cache.ImageLoader;
import com.tradehero.th.cache.ImageLoader.ImageLoadingListener;
import com.tradehero.th.models.Trend;
import com.tradehero.th.utills.ImageUtils;
import com.tradehero.th.utills.Logger;
import com.tradehero.th.utills.Logger.LogLevel;
import com.tradehero.th.utills.TrendUtils;
import com.tradehero.th.utills.YUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;

public class TrendingAdapter extends ArrayAdapter<Trend>
{

    private final static String TAG = TrendingAdapter.class.getSimpleName();

    public TrendingAdapter(Context context, List<Trend> trendList)
    {
        super(context, 0, trendList);
    }

    @SuppressWarnings("deprecation")
    public View getView(final int position, View convertView,
            ViewGroup parent)
    {

        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.trending_grid_item, null);
        }

        TextView stockName = (TextView) convertView.findViewById(R.id.stock_name);
        TextView exchangeSymbol = (TextView) convertView.findViewById(R.id.exchange_symbol);
        TextView profitIndicator = (TextView) convertView.findViewById(R.id.profit_indicator);
        TextView currencyDisplay = (TextView) convertView.findViewById(R.id.currency_display);
        TextView lastPrice = (TextView) convertView.findViewById(R.id.last_price);
        final SmartImageView stockLogo = (SmartImageView) convertView.findViewById(R.id.stock_logo);
        final SmartImageView stockBgLogo =
                (SmartImageView) convertView.findViewById(R.id.stock_bg_logo);

        Trend trend = getItem(position);

        stockName.setText(trend.getName());
        exchangeSymbol.setText(String.format("%s:%s", trend.getExchange(), trend.getSymbol()));
        currencyDisplay.setText(trend.getCurrencyDisplay());

        double dLastPrice = YUtils.parseQuoteValue(trend.getLastPrice());
        if (!Double.isNaN(dLastPrice))
        {
            lastPrice.setText(String.format("%.2f", dLastPrice));
        }
        else
        {
            Logger.log(TAG, "TH: Unable to parse Last Price", LogLevel.LOGGING_LEVEL_ERROR);
        }

        if (trend.getPc50DMA() > 0)
        {
            profitIndicator.setText(getContext().getString(R.string.positive_prefix));
        }
        else if (trend.getPc50DMA() < 0)
        {
            profitIndicator.setText(getContext().getString(R.string.negetive_prefix));
        }

        profitIndicator.setTextColor(TrendUtils.colorForPercentage(trend.getPc50DMA()));

        stockBgLogo.setAlpha(26); //15% opaque
        if (trend.getImageBlobUrl() != null && trend.getImageBlobUrl().length() > 0)
        {
            //Bitmap b = convertToMutable((new WebImageCache(TrendingActivity.this)).get(trend.getImageBlobUrl()));
            new ImageLoader(getContext()).getBitmapImage(trend.getImageBlobUrl(),
                    new ImageLoadingListener()
                    {
                        public void onLoadingComplete(Bitmap loadedImage)
                        {
                            final Bitmap b =
                                    ImageUtils.convertToMutableAndRemoveBackground(loadedImage);
                            stockLogo.setImageBitmap(b);
                            stockBgLogo.setImageBitmap(b);
                        }
                    });
        }

        return convertView;
    }
}