package com.tradehero.th.fragments;

import com.tradehero.th.fragments.achievement.DebugFragmentAchievementModule;
import dagger.Module;

@Module(
        includes = {
                DebugFragmentAchievementModule.class,
        },

        complete = false,
        library = true
)
public class DebugFragmentModule
{
}
