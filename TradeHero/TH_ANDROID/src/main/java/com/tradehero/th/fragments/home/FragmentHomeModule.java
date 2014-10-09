package com.tradehero.th.fragments.home;

import dagger.Module;

@Module(
        injects = {
                HomeFragment.class,
                HomeWebView.class
        },
        library = true,
        complete = false
)
public class FragmentHomeModule
{
}
