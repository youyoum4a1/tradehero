/**
 * SearchStockAdapter.java 
 * TradeHero
 *
 * Created by @author Siddesh Bingi on Aug 10, 2013
 */
package com.tradehero.th.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import com.tradehero.th.R;
import com.tradehero.th.cache.ImageLoader;
import com.tradehero.th.cache.ImageLoader.ImageLoadingListener;
import com.tradehero.th.models.Trend;
import com.tradehero.th.utills.DateUtils;
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

public class SearchStockAdapter extends ArrayAdapter<Trend>
{

    private final static String TAG = SearchStockAdapter.class.getSimpleName();

    public SearchStockAdapter(Context context, List<Trend> trendList)
    {
        super(context, 0, trendList);
    }

    public static class ViewHolder
    {
        TextView stockName;
        TextView exchangeSymbol;
        TextView profitIndicator;
        TextView currencyDisplay;
        TextView lastPrice;
        TextView date;
        TextView secType;
        SmartImageView stockLogo;
        SmartImageView stockBgLogo;
    }

    @SuppressWarnings("deprecation")
    public View getView(final int position, View convertView, ViewGroup parent)
    {

        ViewHolder holder = null;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.search_stock_item, null);

            holder = new ViewHolder();

            holder.stockName = (TextView) convertView.findViewById(R.id.stock_name);
            holder.exchangeSymbol = (TextView) convertView.findViewById(R.id.exchange_symbol);
            holder.profitIndicator = (TextView) convertView.findViewById(R.id.profit_indicator);
            holder.currencyDisplay = (TextView) convertView.findViewById(R.id.currency_display);
            holder.lastPrice = (TextView) convertView.findViewById(R.id.last_price);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.secType = (TextView) convertView.findViewById(R.id.sec_type);
            holder.stockLogo = (SmartImageView) convertView.findViewById(R.id.stock_logo);
            holder.stockBgLogo = (SmartImageView) convertView.findViewById(R.id.stock_bg_logo);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        Trend trend = getItem(position);

        holder.stockName.setText(trend.getName());
        holder.exchangeSymbol
                .setText(String.format("%s:%s", trend.getExchange(), trend.getSymbol()));
        holder.currencyDisplay.setText(trend.getCurrencyDisplay());
        holder.date.setText(DateUtils.getFormatedTrendDate(trend.getLastPriceDateAndTimeUtc()));
        holder.secType.setText(trend.getSecTypeDesc().toUpperCase());

        double dLastPrice = YUtils.parseQuoteValue(trend.getLastPrice());
        if (!Double.isNaN(dLastPrice))
        {
            holder.lastPrice.setText(String.format("%.2f", dLastPrice));
        }
        else
        {
            Logger.log(TAG, "TH: Unable to parse Last Price", LogLevel.LOGGING_LEVEL_ERROR);
        }

        if (trend.getPc50DMA() > 0)
        {
            holder.profitIndicator.setText(getContext().getString(R.string.positive_prefix));
        }
        else if (trend.getPc50DMA() < 0)
        {
            holder.profitIndicator.setText(getContext().getString(R.string.negative_prefix));
        }

        holder.profitIndicator.setTextColor(TrendUtils.colorForPercentage(trend.getPc50DMA()));

        //15% opaque
        final SmartImageView sBgLogo = holder.stockBgLogo;
        final SmartImageView sLogo = holder.stockLogo;
        if (trend.getImageBlobUrl() != null && trend.getImageBlobUrl().length() > 0)
        {
            //Bitmap b = convertToMutable((new WebImageCache(TrendingActivity.this)).get(trend.getImageBlobUrl()));
            ImageLoader.getInstance(getContext()).getBitmapImage(trend.getImageBlobUrl(),
                    new ImageLoadingListener()
                    {
                        public void onLoadingComplete(Bitmap loadedImage)
                        {
                            final Bitmap b =
                                    ImageUtils.convertToMutableAndRemoveBackground(loadedImage);
                            sLogo.setImageBitmap(b);
                            sBgLogo.setImageBitmap(b);
                            sBgLogo.setAlpha(26);
                        }
                    });
        }

        return convertView;
    }
}