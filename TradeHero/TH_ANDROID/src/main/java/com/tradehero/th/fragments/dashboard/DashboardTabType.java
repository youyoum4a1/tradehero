package com.tradehero.th.fragments.dashboard;

import android.support.v4.app.Fragment;
import com.tradehero.th.R;
import com.tradehero.th.fragments.billing.StoreScreenFragment;
import com.tradehero.th.fragments.leaderboard.LeaderboardCommunityFragment;
import com.tradehero.th.fragments.portfolio.PortfolioListFragment;
import com.tradehero.th.fragments.settings.SettingsFragment;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.trending.TrendingFragment;
import com.tradehero.th.fragments.updatecenter.UpdateCenterFragment;

/** Created with IntelliJ IDEA. User: xavier Date: 10/18/13 Time: 1:26 PM To change this template use File | Settings | File Templates. */
public enum DashboardTabType
{
    //NEWS(R.string.dashboard_headline, R.drawable.trending_selector, HeadlineFragment.class),
    TIMELINE(R.layout.home_selector, R.string.empty, R.color.transparent, MeTimelineFragment.class),
    TRENDING(R.string.dashboard_trending, R.drawable.icn_menu_trending, TrendingFragment.class),
    COMMUNITY(R.string.dashboard_community, R.drawable.icn_menu_leaderboards, LeaderboardCommunityFragment.class),
    PORTFOLIO(R.string.dashboard_portfolio, R.drawable.icn_menu_portfolio, PortfolioListFragment.class),
    STORE(R.string.dashboard_store, R.drawable.icn_menu_store, StoreScreenFragment.class),
    UPDATE_CENTER(R.string.update_center, R.drawable.superman_facebook, UpdateCenterFragment.class),
    SETTING(R.string.settings, R.drawable.icn_menu_settings, SettingsFragment.class);

    private static final int DEFAULT_VIEW_LAYOUT_ID = R.layout.tab_indicator_holo;

    public final int viewResId;
    public final int stringResId;
    public final int drawableResId;

    public final Class<? extends Fragment> fragmentClass;

    private DashboardTabType(int viewResId, int stringResId, int drawableResId, Class<? extends Fragment> fragmentClass)
    {
        this.viewResId = viewResId;
        this.stringResId = stringResId;
        this.drawableResId = drawableResId;
        this.fragmentClass = fragmentClass;
    }

    private DashboardTabType(int stringResId, int drawableResId, Class<? extends Fragment> fragmentClass)
    {
        this(DEFAULT_VIEW_LAYOUT_ID, stringResId, drawableResId, fragmentClass);
    }
    
    public boolean hasCustomView()
    {
        return viewResId != DEFAULT_VIEW_LAYOUT_ID;
    }
}
