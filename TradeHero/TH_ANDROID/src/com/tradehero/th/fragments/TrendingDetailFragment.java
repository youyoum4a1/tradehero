/**
 * TrendingDetailFragment.java 
 * TradeHero
 *
 * Created by @author Siddesh Bingi on Jul 24, 2013
 */
package com.tradehero.th.fragments;

import android.support.v4.app.FragmentTabHost;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import com.tradehero.th.R;
import com.tradehero.th.activities.TradeHeroTabActivity;
import com.tradehero.th.application.App;
import com.tradehero.th.application.Config;
import com.tradehero.th.models.Token;
import com.tradehero.th.models.Trend;
import com.tradehero.th.utills.Constants;
import com.tradehero.th.utills.Logger;
import com.tradehero.th.utills.Logger.LogLevel;
import com.tradehero.th.utills.YUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class TrendingDetailFragment extends Fragment
{

    private final static String TAG = TrendingDetailFragment.class.getSimpleName();

    private final static int YAHOO_QUOTE_INTERVAL = 60 * 1000;

    private FragmentTabHost mTabHost;
    private TextView mHeaderText;
    private Trend trend;
    private String mYahooQuotesString = "";
    private List<String> mYahooQuoteValues;
    private boolean isRequestCompleted = false;
    private LinkedHashMap<String, String> yQuotes;
    private Handler mYahooQuotesUpdateTaskHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {

        View view = null;
        view = inflater.inflate(R.layout.fragment_trending_detail, container, false);

        mTabHost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
        //mTabHost.setBackgroundColor(getResources().getColor(R.color.trending_detail_bg));
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent1);

        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.tab_trade))
                .setIndicator(getString(R.string.tab_trade)),
                TradeFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.tab_stock_info))
                .setIndicator(getString(R.string.tab_stock_info)),
                StockInfoFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.tab_news))
                .setIndicator(getString(R.string.tab_news)),
                NewsFragment.class, null);

        trend = ((App) getActivity().getApplication()).getTrend();

        mHeaderText = (TextView) view.findViewById(R.id.header_txt);
        mHeaderText.setText(String.format("%s:%s", trend.getExchange(), trend.getSymbol()));

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        ((TradeHeroTabActivity) getActivity()).showTabs(false);

        mYahooQuotesString = YUtils.getYahooQuoteKeysString();
        mYahooQuoteValues = Arrays.asList(YUtils.YAHOO_QUOTE_VALUES);

        mYahooQuotesUpdateTaskHandler = new Handler();

        //requestToGetYahooQuotes();
    }

    private void requestToGetYahooQuotes()
    {
        Logger.log(TAG, "requestToGetYahooQuotes()", LogLevel.LOGGING_LEVEL_INFO);

        isRequestCompleted = false;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(
                String.format(Config.getYahooQuotes(), trend.getYahooSymbol(), mYahooQuotesString),
                new AsyncHttpResponseHandler()
                {

                    @Override
                    public void onSuccess(String response)
                    {
                        Logger.log(TAG, response, LogLevel.LOGGING_LEVEL_DEBUG);

                        if (!TextUtils.isEmpty(response))
                        {
                            LinkedHashMap<String, String> yahooQuotes =
                                    mapYahooQuoteResposeWithItsValues(
                                            YUtils.CSVToStringList(response));
                            yQuotes = yahooQuotes;
                            try
                            {
                                ((App) getActivity().getApplication()).setYahooQuotesMap(
                                        yahooQuotes);
                            } catch (NullPointerException e)
                            {
                                e.printStackTrace();
                            }
                            notifyYahooQuoteUpdate(yahooQuotes);
                        }
                    }

                    @Override
                    public void onFailure(Throwable arg0, String arg1)
                    {

                    }
                });
    }

    private void requestToGetBuyQuotes()
    {

        Token mToken = ((App) getActivity().getApplication()).getToken();

        AsyncHttpClient client = new AsyncHttpClient();
        String authToken = Base64.encodeToString(mToken.getToken().getBytes(), Base64.DEFAULT);
        client.addHeader(Constants.TH_CLIENT_VERSION, Constants.TH_CLIENT_VERSION_VALUE);
        client.addHeader(Constants.AUTHORIZATION,
                String.format("%s %s", Constants.TH_EMAIL_PREFIX, authToken));

        client.get(String.format(Config.getTrendNewBuyQuotes(), trend.getExchange(),
                trend.getSymbol()), new AsyncHttpResponseHandler()
        {

            @Override
            public void onSuccess(int arg0, String response)
            {
                Logger.log(TAG, response, LogLevel.LOGGING_LEVEL_INFO);
            }

            @Override
            public void onFailure(Throwable arg0, String response)
            {
                Logger.log(TAG, "Unable to get Buy Quotes:\n" + response,
                        LogLevel.LOGGING_LEVEL_ERROR);
            }
        });
    }

    private Runnable mYahooQuotesUpdateTask = new Runnable()
    {
        @Override
        public void run()
        {
            notityYahooQuoteUpdateStart();
            requestToGetYahooQuotes();
            //requestToGetBuyQuotes();
            mYahooQuotesUpdateTaskHandler.postDelayed(mYahooQuotesUpdateTask, YAHOO_QUOTE_INTERVAL);
        }
    };

    private void startYahooQuotesUpdateTask()
    {
        Logger.log(TAG, "startYahooQuotesUpdateTask()", LogLevel.LOGGING_LEVEL_INFO);
        mYahooQuotesUpdateTask.run();
    }

    private void stopYahooQuotesUpdateTask()
    {
        Logger.log(TAG, "stopYahooQuotesUpdateTask()", LogLevel.LOGGING_LEVEL_INFO);
        mYahooQuotesUpdateTaskHandler.removeCallbacks(mYahooQuotesUpdateTask);
    }

    private LinkedHashMap<String, String> mapYahooQuoteResposeWithItsValues(List<String> csvList)
    {
        final int size = mYahooQuoteValues.size();
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        for (int i = 0; i < size; i++)
        {
            map.put(mYahooQuoteValues.get(i), csvList.get(i));
        }
        Logger.log(TAG, "YahooQuote Map:\n" + map.toString(), LogLevel.LOGGING_LEVEL_DEBUG);
        return map;
    }

    /*
     * Yahoo Quote Listener
     */
    public YahooQuoteUpdateListener mYahooQuoteUpdateListener = null;

    private void notifyYahooQuoteUpdate(HashMap<String, String> yQuotes)
    {
        isRequestCompleted = true;
        if (mYahooQuoteUpdateListener != null)
        {
            mYahooQuoteUpdateListener.onYahooQuoteUpdateListener(yQuotes);
        }
    }

    public void setYahooQuoteUpdateListener(YahooQuoteUpdateListener listener)
    {
        if (listener != null)
        {
            mYahooQuoteUpdateListener = listener;
            if (isRequestCompleted)
            {
                mYahooQuoteUpdateListener.onYahooQuoteUpdateListener(yQuotes);
            }
        }
    }

    public void notityYahooQuoteUpdateStart()
    {
        if (mYahooQuoteUpdateListener != null)
        {
            mYahooQuoteUpdateListener.onYahooQuoteUpdateStarted();
        }
    }

    public interface YahooQuoteUpdateListener
    {
        public void onYahooQuoteUpdateListener(HashMap<String, String> yQuotes);

        public void onYahooQuoteUpdateStarted();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        startYahooQuotesUpdateTask();
    }

    @Override
    public void onStop()
    {
        stopYahooQuotesUpdateTask();
        super.onStop();
    }
}
