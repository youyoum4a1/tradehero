package com.tradehero.th.fragments.live;

import com.tradehero.th.fragments.live.ayondo.FragmentAyondoModule;
import dagger.Module;

@Module(
        includes = {
                FragmentAyondoModule.class,
        },
        injects = {
                LiveCallToActionFragment.class,
                LiveSignUpMainFragment.class,
        },
        library = true,
        complete = false
)
public class FragmentGameLiveModule
{
}
