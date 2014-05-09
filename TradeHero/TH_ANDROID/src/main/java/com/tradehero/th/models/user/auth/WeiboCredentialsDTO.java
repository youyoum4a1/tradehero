package com.tradehero.th.models.user.auth;

public class WeiboCredentialsDTO implements CredentialsDTO
{
    public static final String WEIBO_AUTH_TYPE = "TH-Weibo";

    public WeiboCredentialsDTO()
    {
        super();
    }

    @Override public String getAuthType()
    {
        return WEIBO_AUTH_TYPE;
    }
}
