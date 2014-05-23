package com.tradehero.th.auth;

import android.util.Base64;
import com.tradehero.th.api.form.UserFormFactory;
import com.tradehero.th.models.user.auth.EmailCredentialsDTO;
import com.tradehero.th.base.JSONCredentials;
import org.json.JSONException;

public class EmailAuthenticationProvider implements THAuthenticationProvider
{
    private static JSONCredentials credentials;

    public EmailAuthenticationProvider()
    {
    }

    public EmailAuthenticationProvider(JSONCredentials credentials)
    {
        setCredentials (credentials);
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
        if (credentials == null || !credentials.has(UserFormFactory.KEY_EMAIL) || !credentials.has(UserFormFactory.KEY_PASSWORD))
        {
            throw new IllegalArgumentException("Credentials or Email or Password is null");
        }
        String authHeaderParameter = null;
        try
        {
            authHeaderParameter = Base64.encodeToString(
                    String.format("%1$s:%2$s", credentials.get(UserFormFactory.KEY_EMAIL), credentials.get(UserFormFactory.KEY_PASSWORD)).getBytes(),
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
}
