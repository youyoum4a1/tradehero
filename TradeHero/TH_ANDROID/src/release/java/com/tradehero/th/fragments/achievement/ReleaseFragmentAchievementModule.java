package com.tradehero.th.fragments.achievement;

import dagger.Module;
import dagger.Provides;

@Module(
        includes = {
        },
        injects = {
        },
        complete = false,
        library = true
)
public class ReleaseFragmentAchievementModule
{
    @Provides @ForAchievementListFragment Class provideAchievementListFragmentClass()
    {
        return AchievementListFragment.class;
    }
}
