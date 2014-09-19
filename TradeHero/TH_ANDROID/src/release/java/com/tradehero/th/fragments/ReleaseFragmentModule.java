package com.tradehero.th.fragments;

import com.tradehero.th.fragments.achievement.ReleaseFragmentAchievementModule;
import dagger.Module;

@Module(
        includes = {
                ReleaseFragmentAchievementModule.class,
        },

        complete = false,
        library = true
)
public class ReleaseFragmentModule
{
}
