package com.tradehero.th.models.user.auth;

public class FacebookCredentialsDTO implements CredentialsDTO
{
    public static final String FACEBOOK_AUTH_TYPE = "TH-Facebook";

    public FacebookCredentialsDTO()
    {
        super();
    }

    @Override public String getAuthType()
    {
        return FACEBOOK_AUTH_TYPE;
    }
}
