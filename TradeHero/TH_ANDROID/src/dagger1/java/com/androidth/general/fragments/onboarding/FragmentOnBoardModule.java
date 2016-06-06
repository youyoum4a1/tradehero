package com.androidth.general.fragments.onboarding;

import com.androidth.general.fragments.onboarding.exchange.FragmentOnBoardExchangeModule;
import com.androidth.general.fragments.onboarding.hero.FragmentOnBoardHeroModule;
import com.androidth.general.fragments.onboarding.last.FragmentOnBoardLastModule;
import com.androidth.general.fragments.onboarding.sector.FragmentOnBoardSectorModule;
import com.androidth.general.fragments.onboarding.stock.FragmentOnBoardStockModule;
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