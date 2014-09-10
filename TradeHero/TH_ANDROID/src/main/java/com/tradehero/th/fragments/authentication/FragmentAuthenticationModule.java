package com.tradehero.th.fragments.authentication;

import dagger.Module;

/**
 * Created by tho on 9/10/2014.
 */
@Module(
        injects = {
                SignInOrUpFragment.class,
                SignInFragment.class,
                SignUpFragment.class,
                EmailSignInOrUpFragment.class,
                EmailSignInFragment.class,
                EmailSignUpFragment.class
        },
        library = true,
        complete = false
)
public class FragmentAuthenticationModule
{
}
