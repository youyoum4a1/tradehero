package com.tradehero.th.fragments.dashboard;

import android.support.v4.app.Fragment;
import com.tradehero.th.R;
import com.tradehero.th.fragments.billing.StoreScreenFragment;
import com.tradehero.th.fragments.leaderboard.LeaderboardCommunityFragment;
import com.tradehero.th.fragments.portfolio.PortfolioListFragment;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.trending.TrendingFragment;

/** Created with IntelliJ IDEA. User: xavier Date: 10/18/13 Time: 1:26 PM To change this template use File | Settings | File Templates. */
public enum DashboardTabType
{
    TRENDING(R.string.dashboard_trending, R.drawable.trending_selector, TrendingFragment.class),
    COMMUNITY(R.string.dashboard_community, R.drawable.community_selector, LeaderboardCommunityFragment.class),
    TIMELINE(R.layout.home_selector, R.string.dashboard_home, R.drawable.home_selector, MeTimelineFragment.class),
    PORTFOLIO(R.string.dashboard_portfolio, R.drawable.portfolio_selector, PortfolioListFragment.class),
    STORE(R.string.dashboard_store, R.drawable.store_selector, StoreScreenFragment.class);

    public final int stringResId;
    public final int drawableResId;
    public final int viewResId;

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
        this(R.layout.tab_indicator_holo, stringResId, drawableResId, fragmentClass);
    }
}
