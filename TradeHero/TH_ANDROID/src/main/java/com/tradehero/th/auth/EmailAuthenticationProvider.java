package com.tradehero.th.auth;

import android.app.Activity;
import android.content.Intent;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.authentication.EmailSignInFragment;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import rx.Observable;

@Singleton
public class EmailAuthenticationProvider implements THAuthenticationProvider
{
    private final Provider<DashboardNavigator> dashboardNavigatorProvider;

    @Inject public EmailAuthenticationProvider(Provider<DashboardNavigator> dashboardNavigatorProvider)
    {
        this.dashboardNavigatorProvider = dashboardNavigatorProvider;
    }

    @Override public void cancel()
    {
        throw new UnsupportedOperationException();
    }

    @Override public Observable<AuthData> logIn(Activity activity)
    {
        EmailSignInFragment emailSignInFragment = dashboardNavigatorProvider.get().pushFragment(EmailSignInFragment.class);
        return emailSignInFragment.obtainAuthData();
    }

    @Override public void logout()
    {
        // do nothing
    }
}
