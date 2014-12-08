package com.tradehero.th.fragments.portfolio;

import com.tradehero.th.fragments.portfolio.header.OtherUserPortfolioHeaderView;
import dagger.Component;

@Component
public interface FragmentPortfolioComponent
{
    void injectSimpleOwnPortfolioListItemAdapter(SimpleOwnPortfolioListItemAdapter target);
    void injectPortfolioListItemView(PortfolioListItemView target);
    void injectOtherUserPortfolioHeaderView(OtherUserPortfolioHeaderView target);
}
