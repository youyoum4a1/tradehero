package com.androidth.general.fragments.live;

import com.androidth.general.fragments.base.BaseLiveFragmentUtil;
import com.androidth.general.fragments.base.TrendingLiveFragmentUtil;
import com.androidth.general.fragments.live.ayondo.FragmentAyondoModule;
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
                VerifyPhoneDialogFragment.class,
                VerifyEmailDialogFragment.class,
                LiveFormConfirmationFragment.class
        },
        library = true,
        complete = false
)
public class FragmentGameLiveModule
{
}
