package com.tradehero.th.fragments.live;

import com.tradehero.th.fragments.base.BaseLiveFragmentUtil;
import com.tradehero.th.fragments.live.ayondo.FragmentAyondoModule;
import dagger.Module;

@Module(
        includes = {
                FragmentAyondoModule.class,
        },
        injects = {
                LiveCallToActionFragment.class,
                LiveSignUpMainFragment.class,
                BaseLiveFragmentUtil.class,
                DatePickerDialogFragment.class,
                VerifyPhoneDialogFragment.class
        },
        library = true,
        complete = false
)
public class FragmentGameLiveModule
{
}
