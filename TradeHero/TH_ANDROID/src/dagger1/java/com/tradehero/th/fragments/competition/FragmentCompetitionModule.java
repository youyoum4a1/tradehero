package com.tradehero.th.fragments.competition;

import com.tradehero.th.fragments.competition.zone.FragmentCompetitionZoneModule;
import dagger.Module;

@Module(
        includes = {
                FragmentCompetitionZoneModule.class,
        },
        injects = {
                CompetitionWebViewFragment.class,
                CompetitionPreseasonDialogFragment.class,
                MainCompetitionFragment.class,
                ProviderVideoListFragment.class,
                ProviderVideoListItemView.class,
                ProviderFxListFragment.class,
                AdView.class,
        },
        library = true,
        complete = false
)
public class FragmentCompetitionModule
{
}
