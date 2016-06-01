package com.ayondo.academy.fragments;

import com.ayondo.academy.fragments.achievement.FragmentAchievementModule;
import com.ayondo.academy.fragments.alert.FragmentAlertModule;
import com.ayondo.academy.fragments.authentication.FragmentAuthenticationModule;
import com.ayondo.academy.fragments.base.DashboardFragment;
import com.ayondo.academy.fragments.billing.FragmentBillingModule;
import com.ayondo.academy.fragments.competition.FragmentCompetitionModule;
import com.ayondo.academy.fragments.contestcenter.FragmentContestCenterModule;
import com.ayondo.academy.fragments.discovery.FragmentDiscoveryModule;
import com.ayondo.academy.fragments.discussion.FragmentDiscussionModule;
import com.ayondo.academy.fragments.education.FragmentEducationModule;
import com.ayondo.academy.fragments.fxonboard.FragmentFxOnBoardModule;
import com.ayondo.academy.fragments.leaderboard.FragmentLeaderboardModule;
import com.ayondo.academy.fragments.live.FragmentGameLiveModule;
import com.ayondo.academy.fragments.location.FragmentLocationModule;
import com.ayondo.academy.fragments.news.FragmentNewsModule;
import com.ayondo.academy.fragments.onboarding.FragmentOnBoardModule;
import com.ayondo.academy.fragments.portfolio.FragmentPortfolioModule;
import com.ayondo.academy.fragments.position.FragmentPositionModule;
import com.ayondo.academy.fragments.security.FragmentSecurityModule;
import com.ayondo.academy.fragments.settings.FragmentSettingUIModule;
import com.ayondo.academy.fragments.social.FragmentSocialModule;
import com.ayondo.academy.fragments.timeline.FragmentTimelineModule;
import com.ayondo.academy.fragments.trade.FragmentTradeModule;
import com.ayondo.academy.fragments.translation.FragmentTranslationModule;
import com.ayondo.academy.fragments.trending.FragmentTrendingModule;
import com.ayondo.academy.fragments.updatecenter.FragmentUpdateCenterModule;
import com.ayondo.academy.fragments.watchlist.FragmentWatchlistModule;
import com.ayondo.academy.fragments.web.FragmentWebModule;
import dagger.Module;

@Module(
        includes = {
                FragmentAchievementModule.class,
                FragmentAlertModule.class,
                FragmentAuthenticationModule.class,
                FragmentBillingModule.class,
                FragmentCompetitionModule.class,
                FragmentContestCenterModule.class,
                FragmentDiscussionModule.class,
                FragmentDiscoveryModule.class,
                FragmentEducationModule.class,
                FragmentLeaderboardModule.class,
                FragmentLocationModule.class,
                FragmentNewsModule.class,
                FragmentOnBoardModule.class,
                FragmentPortfolioModule.class,
                FragmentPositionModule.class,
                FragmentSecurityModule.class,
                FragmentSettingUIModule.class,
                FragmentSocialModule.class,
                FragmentTimelineModule.class,
                FragmentTradeModule.class,
                FragmentTranslationModule.class,
                FragmentTrendingModule.class,
                FragmentUpdateCenterModule.class,
                FragmentWatchlistModule.class,
                FragmentWebModule.class,
                FragmentFxOnBoardModule.class,
                FragmentGameLiveModule.class,
        },
        injects = {
                DashboardFragment.class,
        },
        library = true,
        complete = false
)
public class FragmentModule
{
}
