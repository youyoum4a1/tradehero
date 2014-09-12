package com.tradehero.th.fragments.discovery;

import dagger.Module;

@Module(
        injects = {
                DiscoveryMainFragment.class,
                NewsPagerFragment.class,

                NewsHeadlineFragment.class,
                RegionalNewsHeadlineFragment.class,
                DiscoveryDiscussionFragment.class,
                LearningFragment.class,

                RegionalNewsSelectorView.class
        },
        library = true,
        complete = false
)
public class FragmentDiscoveryModule
{
}
