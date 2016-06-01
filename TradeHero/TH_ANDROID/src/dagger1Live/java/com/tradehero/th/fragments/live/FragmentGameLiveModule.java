package com.ayondo.academy.fragments.live;

import com.ayondo.academy.fragments.base.BaseLiveFragmentUtil;
import com.ayondo.academy.fragments.base.TrendingLiveFragmentUtil;
import com.ayondo.academy.fragments.live.ayondo.FragmentAyondoModule;
import dagger.Module;

@Module(
        includes = {
                FragmentAyondoModule.class,
        },
        injects = {
                LiveCallToActionFragment.class,
                LiveSignUpMainFragment.class,
                TrendingLiveFragmentUtil.class,
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
