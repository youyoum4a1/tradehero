package com.tradehero.th.fragments.authentication;

import dagger.Module;

@Module(
        injects = {
                EmailSignInFragment.class,
                EmailSignUpFragment.class,
                GuideAuthenticationFragment.class,
        },
        library = true,
        complete = false
)
public class FragmentAuthenticationModule
{
}
