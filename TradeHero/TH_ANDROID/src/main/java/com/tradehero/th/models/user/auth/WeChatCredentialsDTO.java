package com.tradehero.th.models.user.auth;

public class WeChatCredentialsDTO implements CredentialsDTO
{
    public static final String WECHAT_AUTH_TYPE = "TH-Wehat";

    public WeChatCredentialsDTO()
    {
        super();
    }

    @Override public String getAuthType()
    {
        return WECHAT_AUTH_TYPE;
    }
}
