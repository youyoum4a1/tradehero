package com.ayondo.academy.fragments.timeline;

import dagger.Module;

@Module(
        injects = {
                TimelineFragmentTest.class,
                MeTimelineFragmentTest.class
        },
        complete = false,
        library = true
)
public class FragmentTimelineUITestModule
{
}
