package com.androidth.general.fragments.competition;

import com.androidth.general.fragments.competition.zone.FragmentCompetitionZoneModule;
import com.androidth.general.fragments.live.LiveViewFragment;

import dagger.Module;

@Module(
        includes = {
                FragmentCompetitionZoneModule.class,
        },
        injects = {
                CompetitionWebViewFragment.class,
                CompetitionPreseasonDialogFragment.class,
                LiveViewFragment.class,
                MainCompetitionFragment.class,
                ProviderVideoListFragment.class,
                ProviderVideoListItemView.class,
                ProviderFxListFragment.class,
                AdView.class,
                WizardWebViewFragment.class,
                RedeemFragment.class
        },
        library = true,
        complete = false
)
public class FragmentCompetitionModule
{
}
