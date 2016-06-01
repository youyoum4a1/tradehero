package com.ayondo.academy.fragments.onboarding.exchange;

import dagger.Module;

@Module(
        includes = {
        },
        injects = {
                ExchangeSelectionScreenFragment.class,
                OnBoardExchangeItemView.class,
                TopStockListView.class,
        },
        library = true,
        complete = false
)
public class FragmentOnBoardExchangeModule
{
}