package com.tradehero.th.fragments.live;

import dagger.Module;

@Module(
        injects = {
                LiveCallToActionFragment.class,
                LiveSignUpMainFragment.class,
                LiveSignUpStepBaseFragment.class
        },
        library = true,
        complete = false
)
public class FragmentGameLiveModule
{
}
