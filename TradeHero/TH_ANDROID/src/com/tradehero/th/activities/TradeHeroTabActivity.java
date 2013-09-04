package com.tradehero.th.activities;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.tradehero.th.R;
import com.tradehero.th.fragments.CommunityScreenFragment;
import com.tradehero.th.fragments.HomeScreenFragment;
import com.tradehero.th.fragments.LeftMenuFragment;
import com.tradehero.th.fragments.PortfolioScreenFragment;
import com.tradehero.th.fragments.StoreScreenFragment;
import com.tradehero.th.fragments.TrendingFragment;
import com.tradehero.th.slidermenue.CollapseAnimation;
import com.tradehero.th.slidermenue.ExpandAnimation;
import com.tradehero.th.utills.Util;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
        mTabHost.addTab(mTabHost
                .newTabSpec("Trending")
                .setIndicator("", resources.getDrawable(R.drawable.trending_selector)),
                TrendingFragment.class, b);

        b = new Bundle();
        b.putString("key", "Community");
        mTabHost.addTab(mTabHost
                .newTabSpec("Community")
                .setIndicator("", resources.getDrawable(R.drawable.community_selector)),
                CommunityScreenFragment.class, b);

        b = new Bundle();
        b.putString("key", "Home");
        mTabHost.addTab(mTabHost
                .newTabSpec("Home")
                .setIndicator("", resources.getDrawable(R.drawable.home_selector)),
                HomeScreenFragment.class, b);

        b = new Bundle();
        b.putString("key", "Portfolio");
        mTabHost.addTab(mTabHost
                .newTabSpec("Portfolio")
                .setIndicator("", resources.getDrawable(R.drawable.pofilio_selector)),
                PortfolioScreenFragment.class, b);

        b = new Bundle();
        b.putString("key", "Store");
        mTabHost.addTab(mTabHost
                .newTabSpec("Store")
                .setIndicator("", resources.getDrawable(R.drawable.store_selector)),
                StoreScreenFragment.class, b);

        // setContentView(mTabHost);
        mTabHost.setCurrentTabByTag("Trending");
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
