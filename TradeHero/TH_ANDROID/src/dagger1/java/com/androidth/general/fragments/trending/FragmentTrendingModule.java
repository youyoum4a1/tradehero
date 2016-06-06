package com.androidth.general.fragments.trending;

import dagger.Module;

@Module(
        injects = {
                TrendingMainFragment.class,
                TrendingStockFragment.class,
                TrendingFXFragment.class,
                SearchPeopleItemView.class,

                // Extra Tile needs to know about userProfile data for survey tile element
                ProviderTileView.class,
                SurveyTileView.class,
        },
        library = true,
        complete = false
)
public class FragmentTrendingModule
{
}
