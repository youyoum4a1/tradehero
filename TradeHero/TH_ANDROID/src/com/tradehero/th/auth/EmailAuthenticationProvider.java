package com.tradehero.th.auth;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

/** Created with IntelliJ IDEA. User: xavier Date: 9/3/13 Time: 12:06 PM To change this template use File | Settings | File Templates. */
public class EmailAuthenticationProvider implements THAuthenticationProvider
{
    public static final String EMAIL_AUTH_TYPE = "Basic";

    private String email;
    private String password;

    public EmailAuthenticationProvider()
    {
    }

    public EmailAuthenticationProvider(String email, String password)
    {
        setCredentials (email, password);
    }

    public void setCredentials (String email, String password)
    {
        this.email = email;
        this.password = password;
    }

    @Override public String getAuthType()
    {
        return EMAIL_AUTH_TYPE;
    }

    @Override public String getAuthHeader()
    {
        return String.format("%1$s %2$s", getAuthType(), computeAuthHeaderParameter());
    }

    private String computeAuthHeaderParameter()
    {
        if (email == null || password == null)
        {
            throw new IllegalArgumentException("Email or Password is null");
        }
        return new String (Base64.encodeBase64(String.format("%1$s:%2$s", email, password).getBytes()));
    }

    @Override public void authenticate(THAuthenticationCallback callback)
    {
        throw new UnsupportedOperationException();
    }

    @Override public void deauthenticate()
    {
        throw new UnsupportedOperationException();
    }

    @Override public boolean restoreAuthentication(JSONObject paramJSONObject)
    {
        throw new UnsupportedOperationException();
    }

    @Override public void cancel()
    {
        throw new UnsupportedOperationException();
    }
}
