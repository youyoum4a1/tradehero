package com.tradehero.th.fragments.onboarding;

import com.tradehero.th.fragments.onboarding.exchange.FragmentOnBoardExchangeModule;
import com.tradehero.th.fragments.onboarding.hero.FragmentOnBoardHeroModule;
import com.tradehero.th.fragments.onboarding.last.FragmentOnBoardLastModule;
import com.tradehero.th.fragments.onboarding.pref.FragmentOnBoardPrefModule;
import com.tradehero.th.fragments.onboarding.sector.FragmentOnBoardSectorModule;
import com.tradehero.th.fragments.onboarding.stock.FragmentOnBoardStockModule;
import dagger.Module;

@Module(
        includes = {
                FragmentOnBoardExchangeModule.class,
                FragmentOnBoardHeroModule.class,
                FragmentOnBoardLastModule.class,
                FragmentOnBoardPrefModule.class,
                FragmentOnBoardSectorModule.class,
                FragmentOnBoardStockModule.class,
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