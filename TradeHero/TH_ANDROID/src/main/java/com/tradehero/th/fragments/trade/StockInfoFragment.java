/**
 * StockInfoFragment.java 
 * TradeHero
 *
 * Created by @author Siddesh Bingi on Jul 26, 2013
 */
package com.tradehero.th.fragments.trade;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.models.Trend;
import com.tradehero.th.utills.Logger;
import com.tradehero.th.utills.Logger.LogLevel;
import com.tradehero.th.utills.YUtils;
import java.util.HashMap;

public class StockInfoFragment extends SherlockFragment implements DTOView<SecurityCompactDTO>
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
    private SecurityCompactDTO securityCompactDTO;

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
        THLog.d(TAG, "onActivityCreated()");

        //((TrendingDetailFragment) getActivity().getSupportFragmentManager()
        //        .findFragmentByTag("trending_detail")).setYahooQuoteUpdateListener(this);

        display();
    }

    @Override public void onResume()
    {
        super.onResume();
        Bundle args = getArguments();
        if (args != null)
        {
            new SecurityId(args);
            // TODO make use of it to query the cache
        }
        display();
    }

    public void display(SecurityCompactDTO securityCompactDTO)
    {
        this.securityCompactDTO = securityCompactDTO;
        display();
    }

    public void display()
    {
        if (securityCompactDTO != null)
        {
            Double prevClose = securityCompactDTO.previousClose;
            if (!Double.isNaN(prevClose))
            {
                if (mPreviousClose != null)
                {
                    mPreviousClose.setText(String.format("%.3f", prevClose));
                }
            }
            else
            {
                THLog.d(TAG, "Unable to parse Previous Close");
            }

            Double avgVolume = securityCompactDTO.averageDailyVolume;
            if (!Double.isNaN(avgVolume))
            {
                if (mAvgVolume != null)
                {
                    mAvgVolume.setText(String.format("%,d", (int) (double) avgVolume));
                }
            }
            else
            {
                THLog.d(TAG, "TH: Unable to parse Avg. Volume");
            }

            Double volume = securityCompactDTO.volume;
            if (!Double.isNaN(volume))
            {
                if (mVolume != null)
                {
                    mVolume.setText(String.format("%,d", (int) (double) volume));
                }
            }
            else
            {
                THLog.d(TAG, "TH: Unable to parse Volume");
            }

            //if (!TextUtils.isEmpty(securityCompactDTO.marketCap))
            //{
            //    mMarketCap.setText(YUtils.largeNumberFormat(securityCompactDTO.getMarketCap()));
            //}
            //else
            //{
            //    Logger.log(TAG, "Unable to parse Market Cap", LogLevel.LOGGING_LEVEL_ERROR);
            //}

            Double eps = securityCompactDTO.eps;
            if (!Double.isNaN(eps))
            {
                if (mEps != null)
                {
                    mEps.setText(String.format("%.3f", eps));
                }
            }
            else
            {
                THLog.d(TAG, "Unable to parse EPS");
            }
        }
        else
        {
            // TODO
        }
    }

    public void onYahooQuoteUpdateListener(HashMap<String, String> yQuotes)
    {
        //mPreviousClose

        //double previousClose = YUtils.parseQuoteValue(yQuotes.get(""));
        double daysHigh = YUtils.parseQuoteValue(yQuotes.get("Day's High"));
        if (!Double.isNaN(daysHigh))
        {
            mDaysHigh.setText(String.format("%.2f", daysHigh));
        }
        else
        {
            THLog.d(TAG, "Unable to parse Day\'s High");
        }

        double daysLow = YUtils.parseQuoteValue(yQuotes.get("Day's Low"));
        if (!Double.isNaN(daysLow))
        {
            mDaysLow.setText(String.format("%.2f", daysLow));
        }
        else
        {
            THLog.d(TAG, "Unable to parse Day\'s Low");
        }

        double peRatio = YUtils.parseQuoteValue(yQuotes.get("P/E Ratio"));
        if (!Double.isNaN(peRatio))
        {
            mPERatio.setText(String.format("%.2f", peRatio));
        }
        else
        {
            THLog.d(TAG, "Unable to parse P/E Ratio");
        }

        double open = YUtils.parseQuoteValue(yQuotes.get("Open"));
        if (!Double.isNaN(open))
        {
            mOpen.setText(String.format("%.2f", open));
        }
        else
        {
            THLog.d(TAG, "Unable to parse Open");
        }

        // NOTE: only update the price from Yahoo when the market is open
        if (securityCompactDTO.marketOpen)
        {

            double avgVolume = YUtils.parseQuoteValue(yQuotes.get("Average Daily Volume"));
            if (!Double.isNaN(avgVolume))
            {
                mAvgVolume.setText(String.format("%,d", (int) avgVolume));
            }
            else
            {
                THLog.d(TAG, "Y: Unable to parse Avg. Volume");
            }

            double volume = YUtils.parseQuoteValue(yQuotes.get("Volume"));
            //if(!Double.isNaN(volume)){
            if (!Double.isNaN(volume))
            {
                mVolume.setText(String.format("%,d", (int) volume));
            }
            else
            {
                THLog.d(TAG, "Y: Unable to parse Volume");
            }
            //}
        }
    }

    @Override
    public void onDestroy()
    {
        //((TrendingDetailFragment) getActivity().getSupportFragmentManager()
        //        .findFragmentByTag("trending_detail")).setYahooQuoteUpdateListener(null);
        super.onDestroy();
    }
}
