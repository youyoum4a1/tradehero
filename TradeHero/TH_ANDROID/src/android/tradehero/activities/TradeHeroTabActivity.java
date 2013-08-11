package android.tradehero.activities;

import android.app.TabActivity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.tradehero.fragments.CommunityScreenFragment;
import android.tradehero.fragments.HomeScreenFragment;
import android.tradehero.fragments.PortfolioScreenFragment;
import android.tradehero.fragments.StoreScreenFragment;
import android.tradehero.fragments.TrendingFragment;
import android.tradehero.utills.Util;
import android.view.View;

public class TradeHeroTabActivity extends FragmentActivity {

	private FragmentTabHost mTabHost;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bottom_bar);

		initialSetup();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();


	}


	private void initialSetup() {
		
		boolean response = getIntent().getBooleanExtra(BaseActivity.LOGGEDIN, false);
		if(response)
		{
			Util.show_toast(TradeHeroTabActivity.this, getResources().getString(R.string.login_message));
		}

		Resources ressources = getResources(); 

		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		

		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

		Bundle b = new Bundle();
		b.putString("key", "Trending");
		mTabHost.addTab(mTabHost
				.newTabSpec("Trending")
				.setIndicator("", ressources.getDrawable(R.drawable.trending_selector)),
				TrendingFragment.class, b);

		b = new Bundle();
		b.putString("key", "Community");
		mTabHost.addTab(mTabHost
				.newTabSpec("Community")
				.setIndicator("", ressources.getDrawable(R.drawable.community_selector)),
				CommunityScreenFragment.class, b);

		b = new Bundle();
		b.putString("key", "Home");
		mTabHost.addTab(mTabHost
				.newTabSpec("Home")
				.setIndicator("", ressources.getDrawable(R.drawable.home_selector)),
				HomeScreenFragment.class, b);


		b = new Bundle();
		b.putString("key", "Portfolio");
		mTabHost.addTab(mTabHost
				.newTabSpec("Portfolio")
				.setIndicator("", ressources.getDrawable(R.drawable.pofilio_selector)),
				PortfolioScreenFragment.class, b);

		b = new Bundle();
		b.putString("key", "Store");
		mTabHost.addTab(mTabHost
				.newTabSpec("Store")
				.setIndicator("", ressources.getDrawable(R.drawable.store_selector)),
				StoreScreenFragment.class, b);


		// setContentView(mTabHost);
		mTabHost.setCurrentTabByTag("Home");

	}

	public void showTabs(boolean value) {
		if(mTabHost != null) {
			mTabHost.getTabWidget().setVisibility(value? View.VISIBLE: View.GONE);
		}
	}

}
