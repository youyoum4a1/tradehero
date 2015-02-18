package com.tradehero.th.fragments.onboarding;

import com.tradehero.th.fragments.onboarding.exchange.FragmentOnBoardExchangeModule;
import com.tradehero.th.fragments.onboarding.hero.FragmentOnBoardHeroModule;
import com.tradehero.th.fragments.onboarding.pref.FragmentOnBoardPrefModule;
import dagger.Module;

@Module(
        includes = {
                FragmentOnBoardExchangeModule.class,
                FragmentOnBoardHeroModule.class,
                FragmentOnBoardPrefModule.class,
        },
        injects = {
                OnBoardDialogFragment.class,
                OnBoardNewDialogFragment.class,
        },
        library = true,
        complete = false
)
public class FragmentOnBoardModule
{
}