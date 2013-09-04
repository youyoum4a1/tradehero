package com.tradehero.th.auth;

import com.tradehero.th.api.form.UserFormFactory;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

/** Created with IntelliJ IDEA. User: xavier Date: 9/3/13 Time: 12:06 PM To change this template use File | Settings | File Templates. */
public class EmailAuthenticationProvider implements THAuthenticationProvider
{
    public static final String EMAIL_AUTH_TYPE = "Basic";
    private static JSONObject credentials;

    public EmailAuthenticationProvider()
    {
    }

    public EmailAuthenticationProvider(JSONObject credentials)
    {
        setCredentials (credentials);
    }

    public static void setCredentials (JSONObject credentials)
    {
        EmailAuthenticationProvider.credentials = credentials;
    }

    @Override public String getAuthType()
    {
        return EMAIL_AUTH_TYPE;
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
            authHeaderParameter = new String (Base64.encodeBase64(String.format("%1$s:%2$s", credentials.get(UserFormFactory.KEY_EMAIL), credentials.get(UserFormFactory.KEY_PASSWORD)).getBytes()));
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
        throw new UnsupportedOperationException();
    }

    @Override public boolean restoreAuthentication(JSONObject paramJSONObject)
    {
        // Do nothing
        return true;
    }

    @Override public void cancel()
    {
        throw new UnsupportedOperationException();
    }
}
