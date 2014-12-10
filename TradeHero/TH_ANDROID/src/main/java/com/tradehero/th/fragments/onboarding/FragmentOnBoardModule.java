package com.tradehero.th.fragments.onboarding;

import com.tradehero.th.fragments.onboarding.hero.FragmentOnBoardHeroModule;
import com.tradehero.th.fragments.onboarding.pref.FragmentOnBoardPrefModule;
import dagger.Module;

@Module(
        includes = {
                FragmentOnBoardPrefModule.class,
                FragmentOnBoardHeroModule.class
        },
        injects = {
                OnBoardDialogFragment.class
        },
        library = true,
        complete = false
)
public class FragmentOnBoardModule
{
}