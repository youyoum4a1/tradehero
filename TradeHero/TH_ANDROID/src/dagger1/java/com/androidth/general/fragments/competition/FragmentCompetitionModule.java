package com.androidth.general.fragments.competition;

import com.androidth.general.fragments.competition.zone.FragmentCompetitionZoneModule;
<<<<<<< HEAD
import com.androidth.general.fragments.live.LiveViewFragment;
=======
>>>>>>> 2c39b22b64dcfe1b9cf42f64f9cf4e997862756f

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
