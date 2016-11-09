package com.androidth.general.fragments.kyc;

import com.androidth.general.fragments.base.BaseLiveFragmentUtil;
import com.androidth.general.fragments.base.TrendingLiveFragmentUtil;
import com.androidth.general.fragments.kyc.FragmentAyondoModule;
import com.androidth.general.fragments.trade.Live1BWebLoginDialogFragment;

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
                LiveFormConfirmationFragment.class,
                Live1BWebLoginDialogFragment.class
        },
        library = true,
        complete = false
)
public class FragmentGameLiveModule
{
}
