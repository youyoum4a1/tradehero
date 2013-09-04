package com.tradehero.th.activities;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.widget.TabHost;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.tradehero.th.R;
import com.tradehero.th.fragments.CommunityScreenFragment;
import com.tradehero.th.fragments.HomeScreenFragment;
import com.tradehero.th.fragments.PortfolioScreenFragment;
import com.tradehero.th.fragments.StoreScreenFragment;
import com.tradehero.th.fragments.TrendingFragment;
import com.tradehero.th.utills.Util;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

public class TradeHeroTabActivity extends SherlockFragmentActivity implements OnClickListener
{

    private FragmentTabHost mTabHost;
    private boolean isExpanded;
    private RelativeLayout slidingPanel;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_with_bottom_bar/* bottom_bar*/);
        initialSetup();
    }

    private void initialSetup()
    {
        boolean response = getIntent().getBooleanExtra(BaseActivity.LOGGEDIN, false);
        if (response)
        {
            Util.show_toast(TradeHeroTabActivity.this, getResources().getString(R.string.login_message));
        }

        Resources resources = getResources();

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        Bundle b = new Bundle();
        b.putString("key", "Trending");
        mTabHost.addTab(createTabSpecWithIcon("Trending", R.drawable.trending_selector), TrendingFragment.class, b);

        b = new Bundle();
        b.putString("key", "Community");
        mTabHost.addTab(createTabSpecWithIcon("Community", R.drawable.community_selector), CommunityScreenFragment.class, b);

        b = new Bundle();
        b.putString("key", "Home");
        mTabHost.addTab(createTabSpecWithIcon("Home", R.drawable.home_selector), HomeScreenFragment.class, b);

        b = new Bundle();
        b.putString("key", "Portfolio");
        mTabHost.addTab(createTabSpecWithIcon("Portfolio", R.drawable.pofilio_selector), PortfolioScreenFragment.class, b);

        b = new Bundle();
        b.putString("key", "Store");
        mTabHost.addTab(createTabSpecWithIcon("Store", R.drawable.store_selector), StoreScreenFragment.class, b);

        // setContentView(mTabHost);
        mTabHost.setCurrentTabByTag("Trending");
    }

    private TabHost.TabSpec createTabSpecWithIcon(String tag, int drawableId)
    {
        return mTabHost.newTabSpec(tag).setIndicator("", getResources().getDrawable(drawableId));
    }

    public void showTabs(boolean value)
    {
        if (mTabHost != null)
        {
            mTabHost.getTabWidget().setVisibility(value ? View.VISIBLE : View.GONE);
        }
    }

    public void showTabContent(String value)
    {
        if (mTabHost != null)
        {
            mTabHost.setCurrentTabByTag(value);
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            default:
                break;
        }
    }
}
