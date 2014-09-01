package com.tradehero.th.fragments.discovery;

import dagger.Module;

@Module(
        injects = {
                DiscoveryMainFragment.class,
                NewsPagerFragment.class,
                FeaturedNewsHeadlineFragment.class,
                WhatsHotFragment.class,
                DiscoveryDiscussionFragment.class,
        },
        library = true,
        complete = false
)
public class DiscoveryModule
{
}
