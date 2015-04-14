package com.tradehero.th.fragments;

import com.tradehero.th.fragments.alert.FragmentAlertUITestModule;
import com.tradehero.th.fragments.billing.FragmentBillingUITestModule;
import com.tradehero.th.fragments.competition.FragmentCompetitionUITestModule;
import com.tradehero.th.fragments.discovery.FragmentDiscoveryUITestModule;
import com.tradehero.th.fragments.leaderboard.FragmentLeaderboardUITestModule;
import com.tradehero.th.fragments.security.FragmentSecurityUITestModule;
import com.tradehero.th.fragments.social.FragmentSocialUITestModule;
import com.tradehero.th.fragments.timeline.FragmentTimelineUITestModule;
import com.tradehero.th.fragments.trade.FragmentTradeUITestModule;
import com.tradehero.th.fragments.translation.FragmentTranslationUITestModule;
import com.tradehero.th.fragments.trending.FragmentTrendingUITestModule;
import com.tradehero.th.fragments.web.FragmentWebUITestModule;
import dagger.Module;

@Module(
        includes = {
                FragmentAlertUITestModule.class,
                FragmentBillingUITestModule.class,
                FragmentCompetitionUITestModule.class,
                FragmentDiscoveryUITestModule.class,
                FragmentLeaderboardUITestModule.class,
                FragmentSecurityUITestModule.class,
                FragmentSocialUITestModule.class,
                FragmentTimelineUITestModule.class,
                FragmentTradeUITestModule.class,
                FragmentTranslationUITestModule.class,
                FragmentTrendingUITestModule.class,
                FragmentWebUITestModule.class,
        },
        injects = {
                DashboardNavigatorTest.class,
        },
        complete = false,
        library = true
)
public class FragmentUITestModule
{
}
