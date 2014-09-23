package com.tradehero.th.fragments.achievement;

import dagger.Module;

@Module(
        includes = {
        },
        injects = {
                AchievementListDebugFragment.class,
                AchievementListTestingFragment.class,
                QuestListTestingFragment.class,
        },
        complete = false,
        library = true
)
public class DebugFragmentAchievementModule
{
}
