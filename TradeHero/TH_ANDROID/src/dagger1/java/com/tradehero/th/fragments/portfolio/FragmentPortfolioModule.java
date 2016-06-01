package com.ayondo.academy.fragments.portfolio;

import com.ayondo.academy.fragments.portfolio.header.FragmentPortfolioHeaderModule;
import com.ayondo.academy.fragments.portfolio.header.OtherUserPortfolioHeaderView;
import dagger.Module;

@Module(
        includes = {
                FragmentPortfolioHeaderModule.class,
        },
        injects = {
                PortfolioListItemView.class,
                OtherUserPortfolioHeaderView.class,
        },
        library = true,
        complete = false
)
public class FragmentPortfolioModule
{
}
