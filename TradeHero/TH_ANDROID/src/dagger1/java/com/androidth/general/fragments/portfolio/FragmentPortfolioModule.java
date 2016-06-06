package com.androidth.general.fragments.portfolio;

import com.androidth.general.fragments.portfolio.header.FragmentPortfolioHeaderModule;
import com.androidth.general.fragments.portfolio.header.OtherUserPortfolioHeaderView;
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
