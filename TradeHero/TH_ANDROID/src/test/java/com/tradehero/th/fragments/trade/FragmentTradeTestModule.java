package com.tradehero.th.fragments.trade;

import com.tradehero.th.fragments.competition.MainCompetitionFragmentTest;
import dagger.Module;

@Module(
        injects = {
                TradeListFragmentTest.class
        },
        complete = false,
        library = true
)
public class FragmentTradeTestModule
{
}
