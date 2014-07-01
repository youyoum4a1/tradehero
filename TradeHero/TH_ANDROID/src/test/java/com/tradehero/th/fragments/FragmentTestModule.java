package com.tradehero.th.fragments;

import com.tradehero.th.fragments.competition.FragmentCompetitionTestModule;
import com.tradehero.th.fragments.security.FragmentSecurityTestModule;
import com.tradehero.th.fragments.timeline.FragmentTimelineTestModule;
import com.tradehero.th.fragments.trade.FragmentTradeTestModule;
import com.tradehero.th.fragments.trending.FragmentTrendingTestModule;
import dagger.Module;

@Module(
        includes = {
                FragmentCompetitionTestModule.class,
                FragmentSecurityTestModule.class,
                FragmentTimelineTestModule.class,
                FragmentTradeTestModule.class,
                FragmentTrendingTestModule.class,
        },
        complete = false,
        library = true
)
public class FragmentTestModule
{
}
