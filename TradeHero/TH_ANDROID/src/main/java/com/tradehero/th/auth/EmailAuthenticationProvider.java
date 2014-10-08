package com.tradehero.th.auth;

import android.app.Activity;
import com.tradehero.th.base.JSONCredentials;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.authentication.EmailSignInFragment;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import rx.Observable;

@Singleton
public class EmailAuthenticationProvider implements THAuthenticationProvider
{
    private static JSONCredentials credentials;
    private final Provider<DashboardNavigator> dashboardNavigatorProvider;

    @Inject public EmailAuthenticationProvider(Provider<DashboardNavigator> dashboardNavigatorProvider)
    {
        this.dashboardNavigatorProvider = dashboardNavigatorProvider;
    }

    public static void setCredentials (JSONCredentials credentials)
    {
        EmailAuthenticationProvider.credentials = credentials;
    }

    @Override public void authenticate(THAuthenticationCallback callback)
    {
        if (credentials == null)
        {
            callback.onError(new IllegalArgumentException("Credentials are null"));
        }
        else
        {
            callback.onSuccess(credentials);
        }
    }

    @Override public void deauthenticate()
    {
        // TODO do we need it for email authentication?
        // throw new UnsupportedOperationException();
    }

    @Override public boolean restoreAuthentication(JSONCredentials paramJSONObject)
    {
        // Do nothing
        return true;
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
}
