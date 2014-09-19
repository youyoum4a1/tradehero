package com.tradehero.th.fragments.achievement;

import dagger.Module;
import dagger.Provides;

@Module(
        includes = {
        },
        injects = {
                QuestListTestingFragment.class,
                AchievementListDebugFragment.class,
        },
        complete = false,
        library = true
)
public class DebugFragmentAchievementModule
{
    @Provides @ForAchievementListFragment Class provideAchievementListFragmentClass()
    {
        return AchievementListDebugFragment.class;
    }
}
