package com.tradehero.th.fragments.dashboard;

import com.tradehero.th.R;
import com.tradehero.th.fragments.billing.StoreScreenFragment;
import com.tradehero.th.fragments.leaderboard.LeaderboardCommunityFragment;
import com.tradehero.th.fragments.portfolio.PortfolioListFragment;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.trending.TrendingFragment;

/** Created with IntelliJ IDEA. User: xavier Date: 10/18/13 Time: 1:26 PM To change this template use File | Settings | File Templates. */
public enum DashboardTabType
{
    TRENDING(R.string.trending, R.drawable.trending_selector, TrendingFragment.class),
    COMMUNITY(R.string.community, R.drawable.community_selector, LeaderboardCommunityFragment.class),
    TIMELINE(R.string.home, R.drawable.home_selector, MeTimelineFragment.class),
    PORTFOLIO(R.string.portfolio, R.drawable.portfolio_selector, PortfolioListFragment.class),
    STORE(R.string.store, R.drawable.store_selector, StoreScreenFragment.class);

    public final int stringResId;
    public final int drawableResId;
    public final Class<?> fragmentClass;

    private DashboardTabType(int stringResId, int drawableResId, Class<?> fragmentClass)
    {
        this.stringResId = stringResId;
        this.drawableResId = drawableResId;
        this.fragmentClass = fragmentClass;
    }
}
