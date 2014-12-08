package com.tradehero.th.fragments.authentication;

import dagger.Component;

/**
 * Created by tho on 9/10/2014.
 */
@Component
public interface FragmentAuthenticationComponent
{
    void injectSignInOrUpFragment(SignInOrUpFragment signInOrUpFragment);
    void injectEmailSignInFragment(EmailSignInFragment emailSignInFragment);
    void injectEmailSignUpFragment(EmailSignUpFragment emailSignUpFragment);
}
