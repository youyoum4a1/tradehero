package com.tradehero.th.fragments.trending;

import dagger.Module;

@Module(
        injects = {
                TrendingStockFragmentTest.class,
                OpenTrendingFragment.class,
        },
        complete = false,
        library = true
)
public class FragmentTrendingUITestModule
{
}
