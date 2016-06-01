package com.ayondo.academy.fragments.onboarding;

import com.ayondo.academy.fragments.onboarding.exchange.FragmentOnBoardExchangeModule;
import com.ayondo.academy.fragments.onboarding.hero.FragmentOnBoardHeroModule;
import com.ayondo.academy.fragments.onboarding.last.FragmentOnBoardLastModule;
import com.ayondo.academy.fragments.onboarding.sector.FragmentOnBoardSectorModule;
import com.ayondo.academy.fragments.onboarding.stock.FragmentOnBoardStockModule;
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