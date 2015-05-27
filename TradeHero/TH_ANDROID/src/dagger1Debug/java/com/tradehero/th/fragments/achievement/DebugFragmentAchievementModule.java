package com.tradehero.th.fragments.achievement;

import dagger.Module;

@Module(
        includes = {
        },
        injects = {
                AchievementListTestingFragment.class,
                QuestListTestingFragment.class,
        },
        complete = false,
        library = true
)
public class DebugFragmentAchievementModule
{
}
