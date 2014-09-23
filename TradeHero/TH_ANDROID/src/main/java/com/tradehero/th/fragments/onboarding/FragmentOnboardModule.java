package com.tradehero.th.fragments.onboarding;

import com.tradehero.th.fragments.onboarding.hero.FragmentOnboardHeroModule;
import com.tradehero.th.fragments.onboarding.pref.FragmentOnboardPrefModule;
import dagger.Module;

@Module(
        includes = {
                FragmentOnboardPrefModule.class,
                FragmentOnboardHeroModule.class
        },
        injects = {
                OnBoardDialogFragment.class
        },
        library = true,
        complete = false
)
public class FragmentOnboardModule
{
}
