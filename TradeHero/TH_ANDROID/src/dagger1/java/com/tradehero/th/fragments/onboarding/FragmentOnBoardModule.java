package com.tradehero.th.fragments.onboarding;

import com.tradehero.th.fragments.onboarding.exchange.FragmentOnBoardExchangeModule;
import com.tradehero.th.fragments.onboarding.hero.FragmentOnBoardHeroModule;
import com.tradehero.th.fragments.onboarding.last.FragmentOnBoardLastModule;
import com.tradehero.th.fragments.onboarding.sector.FragmentOnBoardSectorModule;
import com.tradehero.th.fragments.onboarding.stock.FragmentOnBoardStockModule;
import dagger.Module;

@Module(
        includes = {
                FragmentOnBoardExchangeModule.class,
                FragmentOnBoardHeroModule.class,
                FragmentOnBoardLastModule.class,
                FragmentOnBoardSectorModule.class,
                FragmentOnBoardStockModule.class,
        },
        injects = {
                OnBoardFragment.class,
        },
        library = true,
        complete = false
)
public class FragmentOnBoardModule
{
}