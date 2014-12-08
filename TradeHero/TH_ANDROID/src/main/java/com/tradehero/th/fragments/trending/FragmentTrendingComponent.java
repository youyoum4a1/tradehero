package com.tradehero.th.fragments.trending;

import com.tradehero.th.fragments.trending.filter.TrendingFilterSelectorView;
import dagger.Component;

@Component
public interface FragmentTrendingComponent
{
    void injectTrendingFragment(TrendingFragment target);
    void injectTrendingFilterSelectorView(TrendingFilterSelectorView target);
    void injectSearchPeopleItemView(SearchPeopleItemView target);

    // Extra Tile needs to know about userProfile data for survey tile element
    void injectExtraTileAdapter(ExtraTileAdapter target);
    void injectProviderTileView(ProviderTileView target);
    void injectSurveyTileView(SurveyTileView target);
    void injectResetPortfolioTileView(ResetPortfolioTileView target);
    void injectEarnCreditTileView(EarnCreditTileView target);
    void injectExtraCashTileView(ExtraCashTileView target);
}
