package com.tradehero.th.fragments.portfolio;

import com.tradehero.th.fragments.portfolio.header.OtherUserPortfolioHeaderView;
import dagger.Module;

@Module(
        injects = {
                SimpleOwnPortfolioListItemAdapter.class,
                PortfolioListItemView.class,
                OtherUserPortfolioHeaderView.class,
        },
        library = true,
        complete = false
)
public class FragmentPortfolioModule
{
}
