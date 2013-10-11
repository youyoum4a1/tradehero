package com.tradehero.th.activities;

import android.os.Bundle;
import android.support.v4.r11.app.FragmentTabHost;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.base.NavigatorActivity;
import com.tradehero.th.fragments.CommunityScreenFragment;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.PortfolioScreenFragment;
import com.tradehero.th.fragments.StoreScreenFragment;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.trending.TrendingFragment;

public class DashboardActivity extends SherlockFragmentActivity
    implements NavigatorActivity
{
    public static final String TAG = DashboardActivity.class.getSimpleName();

    private Navigator navigator;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dashboard_with_bottom_bar);
        navigator = new DashboardNavigator(this, getSupportFragmentManager(), R.id.realtabcontent);
    }

    @Override public void onBackPressed()
    {
        //super.onBackPressed();
        navigator.popFragment();
    }

    //<editor-fold desc="NavigatorActivity">
    @Override public Navigator getNavigator()
    {
        return navigator;
    }
    //</editor-fold>
}
