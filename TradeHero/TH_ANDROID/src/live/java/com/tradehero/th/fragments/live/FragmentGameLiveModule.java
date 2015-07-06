package com.tradehero.th.fragments.live;

import com.tradehero.th.fragments.base.LiveFragmentUtil;
import com.tradehero.th.fragments.live.ayondo.FragmentAyondoModule;
import dagger.Module;

@Module(
        includes = {
                FragmentAyondoModule.class,
        },
        injects = {
                LiveCallToActionFragment.class,
                LiveSignUpMainFragment.class,
                LiveFragmentUtil.class
        },
        library = true,
        complete = false
)
public class FragmentGameLiveModule
{
}
