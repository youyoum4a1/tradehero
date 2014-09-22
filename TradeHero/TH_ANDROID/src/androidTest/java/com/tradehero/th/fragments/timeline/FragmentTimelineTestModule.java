package com.tradehero.th.fragments.timeline;

import dagger.Module;

@Module(
        injects = {
                TimelineFragmentTest.class,
                MeTimelineFragmentTest.class
        },
        complete = false,
        library = true
)
public class FragmentTimelineTestModule
{
}
