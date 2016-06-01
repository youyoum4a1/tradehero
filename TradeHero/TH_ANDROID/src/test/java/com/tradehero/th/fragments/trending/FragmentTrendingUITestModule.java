package com.ayondo.academy.fragments.trending;

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
