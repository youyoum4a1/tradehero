package com.tradehero.th.fragments.competition;

import dagger.Module;

@Module(
        staticInjections =
                {
                },
        injects =
                {
                        ProviderVideoListFragment.class,
                        ProviderVideoListItem.class,
                },
        complete = false,
        library = true
)
public class CompetitionModule
{
    public CompetitionModule()
    {
    }
}
