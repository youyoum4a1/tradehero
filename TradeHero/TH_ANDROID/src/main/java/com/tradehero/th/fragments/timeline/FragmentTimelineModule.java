package com.tradehero.th.fragments.timeline;

import dagger.Module;

/**
 * Created by tho on 9/9/2014.
 */
@Module(
        injects = {
                TimelineFragment.class,
                MeTimelineFragment.class,
                PushableTimelineFragment.class,
                UserStatisticView.class,
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
