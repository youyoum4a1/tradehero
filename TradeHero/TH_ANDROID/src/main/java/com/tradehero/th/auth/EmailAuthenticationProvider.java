package com.tradehero.th.auth;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Base64;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.base.JSONCredentials;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.authentication.EmailSignInFragment;
import com.tradehero.th.fragments.authentication.EmailSignUpFragment;
import com.tradehero.th.models.user.auth.EmailCredentialsDTO;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import org.json.JSONException;
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

    @Override public String getAuthType()
    {
        return EmailCredentialsDTO.EMAIL_AUTH_TYPE;
    }

    @Override public String getAuthHeader()
    {
        return String.format("%1$s %2$s", getAuthType(), getAuthHeaderParameter());
    }

    @Override public String getAuthHeaderParameter()
    {
        if (credentials == null || !credentials.has(UserFormDTO.KEY_EMAIL) || !credentials.has(UserFormDTO.KEY_PASSWORD))
        {
            throw new IllegalArgumentException("Credentials or Email or Password is null");
        }
        String authHeaderParameter;
        try
        {
            authHeaderParameter = Base64.encodeToString(
                    String.format("%1$s:%2$s", credentials.get(UserFormDTO.KEY_EMAIL), credentials.get(UserFormDTO.KEY_PASSWORD)).getBytes(),
                    Base64.NO_WRAP);
        }
        catch (JSONException e)
        {
            throw new IllegalArgumentException(e);
        }
        return authHeaderParameter;
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
        Fragment emailSignInFragment = dashboardNavigatorProvider.get().pushFragment(getAuthenticationFragment());
        if (emailSignInFragment instanceof EmailSignInFragment)
        {
            return ((EmailSignInFragment) emailSignInFragment).obtainAuthData();
        }
        return ((EmailSignUpFragment) emailSignInFragment).obtainAuthData();
    }

    protected Class<? extends Fragment> getAuthenticationFragment()
    {
        return EmailSignInFragment.class;
    }
}
