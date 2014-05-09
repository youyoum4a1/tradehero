package com.tradehero.th.models.user.auth;

public class LinkedinCredentialsDTO implements CredentialsDTO
{
    public static final String LINKEDIN_AUTH_TYPE = "TH-LinkedIn";

    public LinkedinCredentialsDTO()
    {
        super();
    }

    @Override public String getAuthType()
    {
        return LINKEDIN_AUTH_TYPE;
    }
}
