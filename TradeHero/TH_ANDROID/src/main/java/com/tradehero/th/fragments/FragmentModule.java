package com.tradehero.th.fragments;

import com.tradehero.th.fragments.alert.FragmentAlertModule;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.billing.FragmentBillingModule;
import com.tradehero.th.fragments.competition.FragmentCompetitionModule;
import com.tradehero.th.fragments.contestcenter.FragmentContestCenter;
import com.tradehero.th.fragments.discovery.FragmentDiscoveryModule;
import com.tradehero.th.fragments.discussion.FragmentDiscussionModule;
import com.tradehero.th.fragments.education.FragmentEducationModule;
import com.tradehero.th.fragments.home.FragmentHomeModule;
import com.tradehero.th.fragments.leaderboard.FragmentLeaderboardModule;
import com.tradehero.th.fragments.location.FragmentLocationModule;
import com.tradehero.th.fragments.news.FragmentNewsModule;
import com.tradehero.th.fragments.portfolio.FragmentPortfolioModule;
import com.tradehero.th.fragments.position.FragmentPositionModule;
import com.tradehero.th.fragments.security.FragmentSecurityModule;
import com.tradehero.th.fragments.settings.FragmentSettingModule;
import com.tradehero.th.fragments.social.FragmentSocialModule;
import com.tradehero.th.fragments.timeline.FragmentTimelineModule;
import com.tradehero.th.fragments.trade.FragmentTradeModule;
import com.tradehero.th.fragments.trending.FragmentTrendingModule;
import com.tradehero.th.fragments.updatecenter.FragmentUpdateCenterModule;
import com.tradehero.th.fragments.watchlist.FragmentWatchlistModule;
import com.tradehero.th.fragments.web.FragmentWebModule;
import dagger.Module;

@Module(
        includes = {
                FragmentTimelineModule.class,
                FragmentLeaderboardModule.class,
                FragmentHomeModule.class,
                FragmentSettingModule.class,
                FragmentTradeModule.class,
                FragmentSocialModule.class,
                FragmentAlertModule.class,
                FragmentSecurityModule.class,
                FragmentDiscussionModule.class,
                FragmentDiscoveryModule.class,
                FragmentPositionModule.class,
                FragmentUpdateCenterModule.class,
                FragmentPortfolioModule.class,
                FragmentContestCenter.class,
                FragmentNewsModule.class,
                FragmentTrendingModule.class,
                FragmentWatchlistModule.class,
                FragmentCompetitionModule.class,
                FragmentBillingModule.class,
                FragmentLocationModule.class,
                FragmentWebModule.class,
                FragmentEducationModule.class
        },
        injects = {
                DashboardFragment.class,
                BasePagedListFragment.class
        },
        library = true,
        complete = false
)
public class FragmentModule
{
}
