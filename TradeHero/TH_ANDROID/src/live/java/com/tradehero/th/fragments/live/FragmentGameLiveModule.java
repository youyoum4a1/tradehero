package com.tradehero.th.fragments.live;

import dagger.Module;

@Module(
        injects = {
                LiveCallToActionFragment.class,
                LiveSignUpMainFragment.class,
                LiveSignUpStepBaseFragment.class,
                LiveSignUpStep1Fragment.class,
                LiveSignUpStep2Fragment.class,
                LiveSignUpStep3Fragment.class,
                LiveSignUpStep4Fragment.class,
                LiveSignUpStep5Fragment.class,
        },
        library = true,
        complete = false
)
public class FragmentGameLiveModule
{
}
