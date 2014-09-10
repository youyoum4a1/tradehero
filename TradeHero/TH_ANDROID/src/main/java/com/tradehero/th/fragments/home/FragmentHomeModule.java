package com.tradehero.th.fragments.home;

import dagger.Module;

/**
 * Created by tho on 9/9/2014.
 */
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
