package com.androidth.general.fragments.education;

import dagger.Module;

@Module(
        injects = {
                VideoCategoriesFragment.class,
                VideoCategoryView.class,
                VideoView.class,
        },
        library = true,
        complete = false
)
public class FragmentEducationModule
{
}
