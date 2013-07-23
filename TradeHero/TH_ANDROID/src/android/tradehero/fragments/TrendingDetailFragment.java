/**
 * TrendingDetailFragment.java 
 * TradeHero
 *
 * Created by @author Siddesh Bingi on Jul 24, 2013
 */
package android.tradehero.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.tradehero.activities.R;
import android.tradehero.activities.TradeHeroTabActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TrendingDetailFragment extends Fragment {
	
	private FragmentTabHost mTabHost;
	private TextView mHeaderText;
	
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
        		TradeFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.tab_news)).setIndicator(getString(R.string.tab_news)),
        		TradeFragment.class, null);
        
        mHeaderText =  (TextView) view.findViewById(R.id.header_txt);
		mHeaderText.setText(getArguments().getString("header"));

        return view;
    }
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		((TradeHeroTabActivity)getActivity()).showTabs(false);
	}

}
