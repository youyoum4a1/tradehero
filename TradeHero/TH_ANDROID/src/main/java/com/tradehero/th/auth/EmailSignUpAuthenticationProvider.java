package com.tradehero.th.auth;

import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.authentication.EmailSignInOrUpFragment;
import com.tradehero.th.fragments.authentication.EmailSignUpFragment;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class EmailSignUpAuthenticationProvider extends EmailAuthenticationProvider
{
    @Inject public EmailSignUpAuthenticationProvider(Provider<DashboardNavigator> dashboardNavigatorProvider)
    {
        super(dashboardNavigatorProvider);
    }

    @Override protected Class<? extends EmailSignInOrUpFragment> getAuthenticationFragment()
    {
        return EmailSignUpFragment.class;
    }
}
