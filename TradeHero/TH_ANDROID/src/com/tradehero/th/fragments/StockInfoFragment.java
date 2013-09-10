/**
 * StockInfoFragment.java 
 * TradeHero
 *
 * Created by @author Siddesh Bingi on Jul 26, 2013
 */
package com.tradehero.th.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.models.Trend;
import com.tradehero.th.utills.Logger;
import com.tradehero.th.utills.Logger.LogLevel;
import com.tradehero.th.utills.YUtils;

public class StockInfoFragment extends Fragment
{

    private final static String TAG = StockInfoFragment.class.getSimpleName();

    private TextView mPreviousClose;
    private TextView mOpen;
    private TextView mDaysHigh;
    private TextView mDaysLow;
    private TextView mMarketCap;
    private TextView mPERatio;
    private TextView mEps;
    private TextView mVolume;
    private TextView mAvgVolume;
    private Trend t;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = null;
        view = inflater.inflate(R.layout.fragment_stockinfo, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View v)
    {
        mPreviousClose = (TextView) v.findViewById(R.id.vprevious_close);
        mOpen = (TextView) v.findViewById(R.id.vopen);
        mDaysHigh = (TextView) v.findViewById(R.id.vdays_high);
        mDaysLow = (TextView) v.findViewById(R.id.vdays_low);
        mMarketCap = (TextView) v.findViewById(R.id.vmarket_cap);
        mPERatio = (TextView) v.findViewById(R.id.vpe_ratio);
        mEps = (TextView) v.findViewById(R.id.veps);
        mVolume = (TextView) v.findViewById(R.id.vvolume);
        mAvgVolume = (TextView) v.findViewById(R.id.vavg_volume);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        Logger.log(TAG, "onActivityCreated()", LogLevel.LOGGING_LEVEL_DEBUG);

        //t = ((App) getActivity().getApplication()).getTrend();

        //((TrendingDetailFragment) getActivity().getSupportFragmentManager()
        //        .findFragmentByTag("trending_detail")).setYahooQuoteUpdateListener(this);

        double prevClose = YUtils.parseQuoteValue(t.getPreviousClose());
        if (!Double.isNaN(prevClose))
        {
            mPreviousClose.setText(String.format("%.3f", prevClose));
        }
        else
        {
            Logger.log(TAG, "Unable to parse Previous Close", LogLevel.LOGGING_LEVEL_ERROR);
        }

        double avgVolume = YUtils.parseQuoteValue(t.getAverageDailyVolume());
        if (!Double.isNaN(avgVolume))
        {
            mAvgVolume.setText(String.format("%,d", (int) avgVolume));
        }
        else
        {
            Logger.log(TAG, "TH: Unable to parse Avg. Volume", LogLevel.LOGGING_LEVEL_ERROR);
        }

        double volume = YUtils.parseQuoteValue(t.getVolume());
        if (!Double.isNaN(volume))
        {
            mVolume.setText(String.format("%,d", (int) volume));
        }
        else
        {
            Logger.log(TAG, "TH: Unable to parse Volume", LogLevel.LOGGING_LEVEL_ERROR);
        }

        if (!TextUtils.isEmpty(t.getMarketCap()))
        {
            mMarketCap.setText(YUtils.largeNumberFormat(t.getMarketCap()));
        }
        else
        {
            Logger.log(TAG, "Unable to parse Market Cap", LogLevel.LOGGING_LEVEL_ERROR);
        }

        double eps = YUtils.parseQuoteValue(t.getEps());
        if (!Double.isNaN(eps))
        {
            mEps.setText(String.format("%.3f", eps));
        }
        else
        {
            Logger.log(TAG, "Unable to parse EPS", LogLevel.LOGGING_LEVEL_ERROR);
        }
    }
    //
    //@Override
    //public void onYahooQuoteUpdateListener(HashMap<String, String> yQuotes)
    //{
    //    //mPreviousClose
    //
    //    //double previousClose = YUtils.parseQuoteValue(yQuotes.get(""));
    //    double daysHigh = YUtils.parseQuoteValue(yQuotes.get("Day's High"));
    //    if (!Double.isNaN(daysHigh))
    //    {
    //        mDaysHigh.setText(String.format("%.2f", daysHigh));
    //    }
    //    else
    //    {
    //        Logger.log(TAG, "Unable to parse Day\'s High", LogLevel.LOGGING_LEVEL_ERROR);
    //    }
    //
    //    double daysLow = YUtils.parseQuoteValue(yQuotes.get("Day's Low"));
    //    if (!Double.isNaN(daysLow))
    //    {
    //        mDaysLow.setText(String.format("%.2f", daysLow));
    //    }
    //    else
    //    {
    //        Logger.log(TAG, "Unable to parse Day\'s Low", LogLevel.LOGGING_LEVEL_ERROR);
    //    }
    //
    //    double peRatio = YUtils.parseQuoteValue(yQuotes.get("P/E Ratio"));
    //    if (!Double.isNaN(peRatio))
    //    {
    //        mPERatio.setText(String.format("%.2f", peRatio));
    //    }
    //    else
    //    {
    //        Logger.log(TAG, "Unable to parse P/E Ratio", LogLevel.LOGGING_LEVEL_ERROR);
    //    }
    //
    //    double open = YUtils.parseQuoteValue(yQuotes.get("Open"));
    //    if (!Double.isNaN(open))
    //    {
    //        mOpen.setText(String.format("%.2f", open));
    //    }
    //    else
    //    {
    //        Logger.log(TAG, "Unable to parse Open", LogLevel.LOGGING_LEVEL_ERROR);
    //    }
    //
    //    // NOTE: only update the price from Yahoo when the market is open
    //    if (t.getMarketOpen())
    //    {
    //
    //        double avgVolume = YUtils.parseQuoteValue(yQuotes.get("Average Daily Volume"));
    //        if (!Double.isNaN(avgVolume))
    //        {
    //            mAvgVolume.setText(String.format("%,d", (int) avgVolume));
    //        }
    //        else
    //        {
    //            Logger.log(TAG, "Y: Unable to parse Avg. Volume", LogLevel.LOGGING_LEVEL_ERROR);
    //        }
    //
    //        double volume = YUtils.parseQuoteValue(yQuotes.get("Volume"));
    //        //if(!Double.isNaN(volume)){
    //        if (!Double.isNaN(volume))
    //        {
    //            mVolume.setText(String.format("%,d", (int) volume));
    //        }
    //        else
    //        {
    //            Logger.log(TAG, "Y: Unable to parse Volume", LogLevel.LOGGING_LEVEL_ERROR);
    //        }
    //        //}
    //    }
    //}

    @Override
    public void onDestroy()
    {
        //((TrendingDetailFragment) getActivity().getSupportFragmentManager()
        //        .findFragmentByTag("trending_detail")).setYahooQuoteUpdateListener(null);
        super.onDestroy();
    }
}
