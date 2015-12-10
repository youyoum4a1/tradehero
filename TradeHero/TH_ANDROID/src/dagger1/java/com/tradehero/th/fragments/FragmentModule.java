package com.tradehero.th.fragments;

import com.tradehero.th.fragments.achievement.FragmentAchievementModule;
import com.tradehero.th.fragments.alert.FragmentAlertModule;
import com.tradehero.th.fragments.authentication.FragmentAuthenticationModule;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.billing.FragmentBillingModule;
import com.tradehero.th.fragments.competition.FragmentCompetitionModule;
import com.tradehero.th.fragments.contestcenter.FragmentContestCenterModule;
import com.tradehero.th.fragments.discovery.FragmentDiscoveryModule;
import com.tradehero.th.fragments.discussion.FragmentDiscussionModule;
import com.tradehero.th.fragments.education.FragmentEducationModule;
import com.tradehero.th.fragments.fxonboard.FragmentFxOnBoardModule;
import com.tradehero.th.fragments.leaderboard.FragmentLeaderboardModule;
import com.tradehero.th.fragments.live.FragmentGameLiveModule;
import com.tradehero.th.fragments.location.FragmentLocationModule;
import com.tradehero.th.fragments.news.FragmentNewsModule;
import com.tradehero.th.fragments.onboarding.FragmentOnBoardModule;
import com.tradehero.th.fragments.portfolio.FragmentPortfolioModule;
import com.tradehero.th.fragments.position.FragmentPositionModule;
import com.tradehero.th.fragments.security.FragmentSecurityModule;
import com.tradehero.th.fragments.settings.FragmentSettingUIModule;
import com.tradehero.th.fragments.social.FragmentSocialModule;
import com.tradehero.th.fragments.timeline.FragmentTimelineModule;
import com.tradehero.th.fragments.trade.FragmentTradeModule;
import com.tradehero.th.fragments.translation.FragmentTranslationModule;
import com.tradehero.th.fragments.trending.FragmentTrendingModule;
import com.tradehero.th.fragments.updatecenter.FragmentUpdateCenterModule;
import com.tradehero.th.fragments.watchlist.FragmentWatchlistModule;
import com.tradehero.th.fragments.web.FragmentWebModule;
import com.tradehero.th.utils.THTheme;
import com.tradehero.th.utils.THThemeManager;
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
                THThemeManager.class,
                THTheme.class,
        },
        library = true,
        complete = false
)
public class FragmentModule
{
}
