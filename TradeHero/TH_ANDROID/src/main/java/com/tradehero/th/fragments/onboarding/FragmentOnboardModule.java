package com.tradehero.th.fragments.onboarding;

import com.tradehero.th.fragments.onboarding.pref.FragmentOnboardPrefModule;
import dagger.Module;

/**
 * Created by tho on 9/16/2014.
 */
@Module(
        includes = {
                FragmentOnboardPrefModule.class,
        },
        injects = {
                OnBoardDialogFragment.class
        },
        library = true,
        complete = false
)
public class FragmentOnboardModule
{
}
