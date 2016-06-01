package com.ayondo.academy.fragments.live.ayondo;

import dagger.Module;

@Module(
        injects = {
                LiveSignUpStepBaseAyondoFragment.class,
                LiveSignUpStep1AyondoFragment.class,
                LiveSignUpStep2AyondoFragment.class,
                LiveSignUpStep3AyondoFragment.class,
                LiveSignUpStep4AyondoFragment.class,
                LiveSignUpStep5AyondoFragment.class,
        },
        library = true,
        complete = false
)
public class FragmentAyondoModule
{
}
