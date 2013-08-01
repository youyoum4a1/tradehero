/**
 * TrendingDetailFragment.java 
 * TradeHero
 *
 * Created by @author Siddesh Bingi on Jul 24, 2013
 */
package android.tradehero.fragments;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.text.TextUtils;
import android.tradehero.activities.R;
import android.tradehero.activities.TradeHeroTabActivity;
import android.tradehero.application.App;
import android.tradehero.application.Config;
import android.tradehero.models.Trend;
import android.tradehero.utills.Logger;
import android.tradehero.utills.Logger.LogLevel;
import android.tradehero.utills.YUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class TrendingDetailFragment extends Fragment {
	
	private final static String TAG = TrendingDetailFragment.class.getSimpleName();
	//private final static String YAHOO_SYMBOL = "yahooSymbol";
	
	
	private FragmentTabHost mTabHost;
	private TextView mHeaderText;
	private Trend trend;
	private String mYahooQuotesString = "";
	private List<String> mYahooQuoteValues;
	private boolean isRequestCompleted = false;
	private HashMap<String, String> yQuotes;

	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		View view = null;
		view = inflater.inflate(R.layout.fragment_trending_detail, container, false);
		
        mTabHost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
        //mTabHost.setBackgroundColor(getResources().getColor(R.color.trending_detail_bg));
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent1);

        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.tab_trade)).setIndicator(getString(R.string.tab_trade)),
                TradeFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.tab_stock_info)).setIndicator(getString(R.string.tab_stock_info)),
        		StockInfoFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.tab_news)).setIndicator(getString(R.string.tab_news)),
        		NewsFragment.class, null);
        
        trend = ((App)getActivity().getApplication()).getTrend();
        
        mHeaderText =  (TextView) view.findViewById(R.id.header_txt);
		mHeaderText.setText(String.format("%s:%s", trend.getExchange(), trend.getSymbol()));

        return view;
    }
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		((TradeHeroTabActivity)getActivity()).showTabs(false);
		
		mYahooQuotesString = YUtils.getYahooQuoteKeysString();
		mYahooQuoteValues = Arrays.asList(YUtils.YAHOO_QUOTE_VALUES);
		
		requestToGetYahooQuotes();
	}
	
	private void requestToGetYahooQuotes() {
		isRequestCompleted = false;
		AsyncHttpClient client = new AsyncHttpClient(); 
		client.get(String.format(Config.getYahooQuotes(), trend.getYahooSymbol(), mYahooQuotesString),
				new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(String response) {
				Logger.log(TAG, response, LogLevel.LOGGING_LEVEL_DEBUG);
				
				if(!TextUtils.isEmpty(response)) {
					HashMap<String, String> yahooQuotes = 
							mapYahooQuoteResposeWithItsValues(YUtils.CSVToStringList(response));
					yQuotes = yahooQuotes;
					notifyYahooQuoteUpdate(yahooQuotes);
				}
			}
			
			@Override
			public void onFailure(Throwable arg0, String arg1) {
				
			}
		});
	}
	
	
	private HashMap<String, String> mapYahooQuoteResposeWithItsValues(List<String> csvList) {
		final int size = mYahooQuoteValues.size();
		HashMap<String, String> map = new HashMap<String, String>();
		for(int i = 0; i < size; i++) {
			map.put(mYahooQuoteValues.get(i), csvList.get(i));
		}
		Logger.log(TAG, "YahooQuote Map:\n"+map.toString(), LogLevel.LOGGING_LEVEL_DEBUG);
		return map;
	}
	
	/*
	 * Yahoo Quote Listener 
	 */
	public YahooQuoteUpdateListener mYahooQuoteUpdateListener  = null;
	
	private void notifyYahooQuoteUpdate(HashMap<String, String> yQuotes) {
		isRequestCompleted = true;
		if(mYahooQuoteUpdateListener != null) {
			mYahooQuoteUpdateListener.onYahooQuoteUpdateListener(yQuotes);
		}
	}
	
	public void setYahooQuoteUpdateListener(YahooQuoteUpdateListener listener) {
		if(listener != null) {
			mYahooQuoteUpdateListener = listener;
			if(isRequestCompleted)
				mYahooQuoteUpdateListener.onYahooQuoteUpdateListener(yQuotes);
		}
	}
	
	public interface YahooQuoteUpdateListener {
		public void onYahooQuoteUpdateListener(HashMap<String, String> yQuotes);
	}

}
