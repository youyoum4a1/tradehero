package com.tradehero.th.auth;

import com.tradehero.th.base.JSONCredentials;
import com.tradehero.th.models.user.auth.DeviceCredentialsDTO;
import org.json.JSONException;

public class DeviceAuthenticationProvider implements THAuthenticationProvider
{
    public static final String KEY_ACCESS_TOKEN = "device_access_token";
    private static JSONCredentials credentials;

    public DeviceAuthenticationProvider()
    {
    }

    public DeviceAuthenticationProvider(JSONCredentials credentials)
    {
        setCredentials (credentials);
    }

    public static void setCredentials (JSONCredentials credentials)
    {
        DeviceAuthenticationProvider.credentials = credentials;
    }

    @Override public String getAuthType()
    {
        return DeviceCredentialsDTO.DEVICE_AUTH_TYPE;
    }

    @Override public String getAuthHeader()
    {
        return String.format("%1$s %2$s", getAuthType(), getAuthHeaderParameter());
    }

    @Override public String getAuthHeaderParameter()
    {
        String token = "";
        try
        {
            token = (String)credentials.get(DeviceAuthenticationProvider.KEY_ACCESS_TOKEN);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return token;
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
