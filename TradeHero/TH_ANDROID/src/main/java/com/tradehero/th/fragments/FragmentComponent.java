package com.tradehero.th.fragments;

import com.tradehero.th.fragments.achievement.FragmentAchievementComponent;
import com.tradehero.th.fragments.alert.FragmentAlertComponent;
import com.tradehero.th.fragments.authentication.FragmentAuthenticationComponent;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.billing.FragmentBillingComponent;
import com.tradehero.th.fragments.competition.FragmentCompetitionComponent;
import com.tradehero.th.fragments.contestcenter.FragmentContestCenterComponent;
import com.tradehero.th.fragments.discovery.FragmentDiscoveryComponent;
import com.tradehero.th.fragments.discussion.FragmentDiscussionComponent;
import com.tradehero.th.fragments.education.FragmentEducationComponent;
import com.tradehero.th.fragments.games.FragmentGamesComponent;
import com.tradehero.th.fragments.home.FragmentHomeComponent;
import com.tradehero.th.fragments.leaderboard.FragmentLeaderboardComponent;
import com.tradehero.th.fragments.location.FragmentLocationComponent;
import com.tradehero.th.fragments.news.FragmentNewsComponent;
import com.tradehero.th.fragments.onboarding.FragmentOnBoardComponent;
import com.tradehero.th.fragments.portfolio.FragmentPortfolioComponent;
import com.tradehero.th.fragments.position.FragmentPositionComponent;
import com.tradehero.th.fragments.security.FragmentSecurityComponent;
import com.tradehero.th.fragments.settings.FragmentSettingUIComponent;
import com.tradehero.th.fragments.social.FragmentSocialComponent;
import com.tradehero.th.fragments.timeline.FragmentTimelineComponent;
import com.tradehero.th.fragments.trade.FragmentTradeComponent;
import com.tradehero.th.fragments.translation.FragmentTranslationComponent;
import com.tradehero.th.fragments.trending.FragmentTrendingComponent;
import com.tradehero.th.fragments.updatecenter.FragmentUpdateCenterComponent;
import com.tradehero.th.fragments.watchlist.FragmentWatchlistComponent;
import com.tradehero.th.fragments.web.FragmentWebComponent;
import dagger.Component;

@Component
public interface FragmentComponent extends
        FragmentAchievementComponent,
        FragmentAlertComponent,
        FragmentAuthenticationComponent,
        FragmentBillingComponent,
        FragmentCompetitionComponent,
        FragmentContestCenterComponent,
        FragmentDiscussionComponent,
        FragmentDiscoveryComponent,
        FragmentEducationComponent,
        FragmentGamesComponent,
        FragmentHomeComponent,
        FragmentLeaderboardComponent,
        FragmentLocationComponent,
        FragmentNewsComponent,
        FragmentOnBoardComponent,
        FragmentPortfolioComponent,
        FragmentPositionComponent,
        FragmentSecurityComponent,
        FragmentSettingUIComponent,
        FragmentSocialComponent,
        FragmentTimelineComponent,
        FragmentTradeComponent,
        FragmentTranslationComponent,
        FragmentTrendingComponent,
        FragmentUpdateCenterComponent,
        FragmentWatchlistComponent,
        FragmentWebComponent
{
    void injectDashboardFragment(DashboardFragment dashboardFragment);
    void injectBasePagedListFragment(BasePagedListFragment basePagedListFragment);
}
