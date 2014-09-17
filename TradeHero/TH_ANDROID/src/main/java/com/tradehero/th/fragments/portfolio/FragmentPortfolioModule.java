package com.tradehero.th.fragments.portfolio;

import com.tradehero.th.fragments.portfolio.header.OtherUserPortfolioHeaderView;
import dagger.Module;

/**
 * Created by tho on 9/9/2014.
 */
@Module(
        injects = {
                SimpleOwnPortfolioListItemAdapter.class,
                PortfolioListFragment.class,
                PortfolioListItemView.class,
                PortfolioListItemAdapter.class,
                OtherUserPortfolioHeaderView.class,
        },
        library = true,
        complete = false
)
public class FragmentPortfolioModule
{
}
