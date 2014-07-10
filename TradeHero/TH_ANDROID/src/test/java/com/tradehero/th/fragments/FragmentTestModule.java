package com.tradehero.th.fragments;

import com.tradehero.th.fragments.billing.FragmentBillingTestModule;
import com.tradehero.th.fragments.competition.FragmentCompetitionTestModule;
import com.tradehero.th.fragments.security.FragmentSecurityTestModule;
import com.tradehero.th.fragments.settings.FragmentSettingsTestModule;
import com.tradehero.th.fragments.social.FragmentSocialTestModule;
import com.tradehero.th.fragments.timeline.FragmentTimelineTestModule;
import com.tradehero.th.fragments.trade.FragmentTradeTestModule;
import com.tradehero.th.fragments.trending.FragmentTrendingTestModule;
import com.tradehero.th.fragments.updatecenter.FragmentUpdateCenterTestModule;
import com.tradehero.th.fragments.web.FragmentWebTestModule;
import dagger.Module;

@Module(
        includes = {
                FragmentBillingTestModule.class,
                FragmentCompetitionTestModule.class,
                FragmentSecurityTestModule.class,
                FragmentSettingsTestModule.class,
                FragmentSocialTestModule.class,
                FragmentTimelineTestModule.class,
                FragmentTradeTestModule.class,
                FragmentTrendingTestModule.class,
                FragmentUpdateCenterTestModule.class,
                FragmentWebTestModule.class
        },
        complete = false,
        library = true
)
public class FragmentTestModule
{
}
