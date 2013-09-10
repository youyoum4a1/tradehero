/**
 * TrendingDetailFragment.java 
 * TradeHero
 *
 * Created by @author Siddesh Bingi on Jul 24, 2013
 */
package com.tradehero.th.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTabHost;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.application.App;
import com.tradehero.th.application.Config;
import com.tradehero.th.base.THUser;
import com.tradehero.th.utills.Constants;
import com.tradehero.th.utills.Logger;
import com.tradehero.th.utills.Logger.LogLevel;
import com.tradehero.th.utills.YUtils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class TrendingDetailFragment extends SherlockFragment
{
    private final static String TAG = TrendingDetailFragment.class.getSimpleName();

    private final static int YAHOO_QUOTE_INTERVAL = 60 * 1000;

    private FragmentTabHost mTabHost;
    private TextView mHeaderText;

    /**
     * It is passed as part of the creation
     */
    private SecurityCompactDTO securityCompactDTO;

    private String mYahooQuotesString = "";
    private List<String> mYahooQuoteValues;
    private boolean isRequestCompleted = false;
    private LinkedHashMap<String, String> yQuotes;
    private RelativeLayout header;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // It assumes that the securityCompactDTO has already been set.
        View view = inflater.inflate(R.layout.fragment_trending_detail, container, false);

        mTabHost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
        //mTabHost.setBackgroundColor(getResources().getColor(R.color.trending_detail_bg));
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent1);

        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.tab_trade)).setIndicator(getString(R.string.tab_trade)),
                TradeFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.tab_stock_info)).setIndicator(getString(R.string.tab_stock_info)),
                StockInfoFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.tab_news)).setIndicator(getString(R.string.tab_news)),
                NewsFragment.class, null);

        mHeaderText = (TextView) view.findViewById(R.id.header_txt);
        mHeaderText.setText(String.format("%s:%s", securityCompactDTO.exchange, securityCompactDTO.symbol));

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        ((DashboardActivity) getActivity()).showTabs(false);

        header = (RelativeLayout) getActivity().findViewById(R.id.top_tabactivity);
        header.setVisibility(View.GONE);
    }

    //<editor-fold desc="Accessors">
    public SecurityCompactDTO getSecurityCompactDTO()
    {
        return securityCompactDTO;
    }

    public void setSecurityCompactDTO(SecurityCompactDTO securityCompactDTO)
    {
        this.securityCompactDTO = securityCompactDTO;
    }
    //</editor-fold>

    private void requestToGetBuyQuotes()
    {
        AsyncHttpClient client = new AsyncHttpClient();
        String authToken = Base64.encodeToString(THUser.getSessionToken().getBytes(), Base64.DEFAULT);
        client.addHeader(Constants.TH_CLIENT_VERSION, Constants.TH_CLIENT_VERSION_VALUE);
        client.addHeader(Constants.AUTHORIZATION, String.format("%s %s", Constants.TH_EMAIL_PREFIX, authToken));

        client.get(String.format(Config.getTrendNewBuyQuotes(), securityCompactDTO.exchange,
                securityCompactDTO.symbol), new AsyncHttpResponseHandler()
        {

            @Override
            public void onSuccess(int arg0, String response)
            {
                Logger.log(TAG, response, LogLevel.LOGGING_LEVEL_INFO);
            }

            @Override
            public void onFailure(Throwable arg0, String response)
            {
                Logger.log(TAG, "Unable to get Buy Quotes:\n" + response, LogLevel.LOGGING_LEVEL_ERROR);
            }
        });
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

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }
}
