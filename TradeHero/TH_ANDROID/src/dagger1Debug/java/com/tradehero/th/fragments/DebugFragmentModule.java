package com.ayondo.academy.fragments;

import com.ayondo.academy.fragments.achievement.DebugFragmentAchievementModule;
import com.ayondo.academy.fragments.level.DebugFragmentLevelModule;
import dagger.Module;

@Module(
        includes = {
                DebugFragmentAchievementModule.class,
                DebugFragmentLevelModule.class,
        },
        injects = {
                TypographyExampleFragment.class,
                TestKChartsFragment.class
        },

        complete = false,
        library = true
)
public class DebugFragmentModule
{
}
