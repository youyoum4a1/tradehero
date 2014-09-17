package com.tradehero.th.fragments.trending;

import dagger.Module;

@Module(
        injects = {
                TrendingFragmentTest.class,
                OpenTrendingFragment.class,
        },
        complete = false,
        library = true
)
public class FragmentTrendingTestModule
{
}
