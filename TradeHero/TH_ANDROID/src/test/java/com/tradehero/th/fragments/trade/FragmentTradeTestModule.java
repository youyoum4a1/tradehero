package com.tradehero.th.fragments.trade;

import dagger.Module;

@Module(
        injects = {
                BuySellFragmentTest.class,
                TradeListFragmentTest.class,
        },
        complete = false,
        library = true
)
public class FragmentTradeTestModule
{
}
