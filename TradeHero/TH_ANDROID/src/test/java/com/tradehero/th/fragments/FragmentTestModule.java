package com.tradehero.th.fragments;

import com.tradehero.th.fragments.security.FragmentSecurityTestModule;
import com.tradehero.th.fragments.trending.FragmentTrendingTestModule;
import dagger.Module;

@Module(
        includes = {
                FragmentSecurityTestModule.class,
                FragmentTrendingTestModule.class,
        },
        complete = false,
        library = true
)
public class FragmentTestModule
{
}
