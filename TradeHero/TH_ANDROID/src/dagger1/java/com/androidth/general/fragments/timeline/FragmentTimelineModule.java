package com.androidth.general.fragments.timeline;

import dagger.Module;

@Module(
        injects = {
                TimelineFragment.class,
                MeTimelineFragment.class,
                PushableTimelineFragment.class,
                TimelineItemViewLinear.class,
                UserProfileCompactViewHolder.class,
                UserProfileDetailViewHolder.class,
        },
        library = true,
        complete = false
)
public class FragmentTimelineModule
{
}
