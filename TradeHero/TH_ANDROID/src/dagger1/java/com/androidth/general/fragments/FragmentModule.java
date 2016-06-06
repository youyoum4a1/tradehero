package com.androidth.general.fragments;

import com.androidth.general.fragments.achievement.FragmentAchievementModule;
import com.androidth.general.fragments.alert.FragmentAlertModule;
import com.androidth.general.fragments.authentication.FragmentAuthenticationModule;
import com.androidth.general.fragments.base.DashboardFragment;
import com.androidth.general.fragments.billing.FragmentBillingModule;
import com.androidth.general.fragments.competition.FragmentCompetitionModule;
import com.androidth.general.fragments.contestcenter.FragmentContestCenterModule;
import com.androidth.general.fragments.discovery.FragmentDiscoveryModule;
import com.androidth.general.fragments.discussion.FragmentDiscussionModule;
import com.androidth.general.fragments.education.FragmentEducationModule;
import com.androidth.general.fragments.fxonboard.FragmentFxOnBoardModule;
import com.androidth.general.fragments.leaderboard.FragmentLeaderboardModule;
import com.androidth.general.fragments.location.FragmentLocationModule;
import com.androidth.general.fragments.news.FragmentNewsModule;
import com.androidth.general.fragments.onboarding.FragmentOnBoardModule;
import com.androidth.general.fragments.portfolio.FragmentPortfolioModule;
import com.androidth.general.fragments.position.FragmentPositionModule;
import com.androidth.general.fragments.security.FragmentSecurityModule;
import com.androidth.general.fragments.settings.FragmentSettingUIModule;
import com.androidth.general.fragments.social.FragmentSocialModule;
import com.androidth.general.fragments.timeline.FragmentTimelineModule;
import com.androidth.general.fragments.trade.FragmentTradeModule;
import com.androidth.general.fragments.translation.FragmentTranslationModule;
import com.androidth.general.fragments.trending.FragmentTrendingModule;
import com.androidth.general.fragments.updatecenter.FragmentUpdateCenterModule;
import com.androidth.general.fragments.watchlist.FragmentWatchlistModule;
import com.androidth.general.fragments.web.FragmentWebModule;
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
