package com.androidth.general.fragments;

import com.androidth.general.fragments.achievement.DebugFragmentAchievementModule;
import com.androidth.general.fragments.level.DebugFragmentLevelModule;
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
