package com.tradehero.th.fragments.trending;

import com.tradehero.th.fragments.trending.filter.TrendingFilterSelectorView;
import dagger.Module;

@Module(
        injects = {
                TrendingFragment.class,
                TrendingFilterSelectorView.class,
                SearchPeopleItemView.class,

                // Extra Tile needs to know about userProfile data for survey tile element
                ExtraTileAdapter.class,
                ProviderTileView.class,
                SurveyTileView.class,
                ResetPortfolioTileView.class,
                EarnCreditTileView.class,
                ExtraCashTileView.class,
        },
        library = true,
        complete = false
)
public class FragmentTrendingModule
{
}
