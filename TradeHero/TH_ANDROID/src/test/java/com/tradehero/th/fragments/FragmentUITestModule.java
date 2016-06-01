package com.ayondo.academy.fragments;

import com.ayondo.academy.fragments.alert.FragmentAlertUITestModule;
import com.ayondo.academy.fragments.billing.FragmentBillingUITestModule;
import com.ayondo.academy.fragments.competition.FragmentCompetitionUITestModule;
import com.ayondo.academy.fragments.discovery.FragmentDiscoveryUITestModule;
import com.ayondo.academy.fragments.leaderboard.FragmentLeaderboardUITestModule;
import com.ayondo.academy.fragments.security.FragmentSecurityUITestModule;
import com.ayondo.academy.fragments.social.FragmentSocialUITestModule;
import com.ayondo.academy.fragments.timeline.FragmentTimelineUITestModule;
import com.ayondo.academy.fragments.trade.FragmentTradeUITestModule;
import com.ayondo.academy.fragments.translation.FragmentTranslationUITestModule;
import com.ayondo.academy.fragments.trending.FragmentTrendingUITestModule;
import com.ayondo.academy.fragments.web.FragmentWebUITestModule;
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
