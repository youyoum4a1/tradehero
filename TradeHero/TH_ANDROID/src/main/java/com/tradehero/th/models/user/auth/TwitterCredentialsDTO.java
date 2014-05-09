package com.tradehero.th.models.user.auth;

public class TwitterCredentialsDTO implements CredentialsDTO
{
    public static final String TWITTER_AUTH_TYPE = "TH-Twitter";

    public TwitterCredentialsDTO()
    {
        super();
    }

    @Override public String getAuthType()
    {
        return TWITTER_AUTH_TYPE;
    }
}
