package com.tradehero.th.fragments.timeline;

import dagger.Module;

@Module(
        injects = {
                TimelineFragment.class,
                MeTimelineFragment.class,
                PushableTimelineFragment.class,
                TimelineItemViewLinear.class,
                UserProfileCompactViewHolder.class,
                UserProfileDetailViewHolder.class,
                UserProfileResideMenuItem.class,
        },
        library = true,
        complete = false
)
public class FragmentTimelineModule
{
}
