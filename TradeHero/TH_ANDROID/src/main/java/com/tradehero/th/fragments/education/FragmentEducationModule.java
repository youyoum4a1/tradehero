package com.tradehero.th.fragments.education;

import dagger.Module;

/**
 * Created by tho on 9/11/2014.
 */
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
