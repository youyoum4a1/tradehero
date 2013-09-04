package com.tradehero.th.activities;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import com.tradehero.th.R;
import com.tradehero.th.fragments.CommunityScreenFragment;
import com.tradehero.th.fragments.HomeScreenFragment;
import com.tradehero.th.fragments.LeftmenueFragment;
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

public class TradeHeroTabActivity extends FragmentActivity implements OnClickListener
{

    private FragmentTabHost mTabHost;
    FrameLayout.LayoutParams menuPanelParameters;
    FrameLayout.LayoutParams slidingPanelParameters;
    LinearLayout.LayoutParams headerPanelParameters;
    LinearLayout.LayoutParams listViewParameters;
    private ImageView mMenue;
    private boolean isExpanded;
    private RelativeLayout slidingPanel;
    private int panelWidth1;
    private DisplayMetrics metrics;
    private android.support.v4.app.FragmentManager fragmentManager;
    private android.support.v4.app.FragmentTransaction fragmentTransaction;

    LayoutInflater inflater;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_bar);

        initialSetup();
    }

    @Override
    public void onBackPressed()
    {
        // TODO Auto-generated method stub
        super.onBackPressed();
    }

    private void initialSetup()
    {

        inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.leftmenue_container, null);

        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //panelWidth = (int) ((metrics.widthPixels) * -0.75);
        panelWidth1 = (int) ((metrics.widthPixels) * 0.75);
        slidingPanel = (RelativeLayout) findViewById(R.id.slidingPanel);
        slidingPanelParameters = (FrameLayout.LayoutParams) slidingPanel.getLayoutParams();
        slidingPanelParameters.width = metrics.widthPixels;
        slidingPanel.setLayoutParams(slidingPanelParameters);
        mMenue = (ImageView) findViewById(R.id.menuViewButton);
        mMenue.setOnClickListener(this);

        boolean response = getIntent().getBooleanExtra(BaseActivity.LOGGEDIN, false);
        if (response)
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

    public void showSlidingMenue(boolean isExpanded)
    {

        if (!isExpanded)
        {
            isExpanded = true;

            fragmentManager = TradeHeroTabActivity.this.getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.menuPanel,
                    new LeftmenueFragment(), "menu_fragment");
            fragmentTransaction.commit();
            new ExpandAnimation(slidingPanel, panelWidth1,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.75f, 0, 0.0f, 0, 0.0f);
        }
        else
        {
            isExpanded = false;
            // Collapse

            new CollapseAnimation(slidingPanel, panelWidth1,
                    TranslateAnimation.RELATIVE_TO_SELF, 0.75f,
                    TranslateAnimation.RELATIVE_TO_SELF, 0.0f, 0, 0.0f,
                    0, 0.0f);
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.menuViewButton:

                showSlidingMenue(false);

                break;

            default:
                break;
        }
    }
}
