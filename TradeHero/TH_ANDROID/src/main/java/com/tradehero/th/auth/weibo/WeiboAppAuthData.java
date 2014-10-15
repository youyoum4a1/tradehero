package com.tradehero.th.auth.weibo;

public class WeiboAppAuthData
{
    public String appId;
    public String redirectUrl;
    public String scope;

    public WeiboAppAuthData(String appId, String redirectUrl, String scope)
    {
        this.appId = appId;
        this.redirectUrl = redirectUrl;
        this.scope = scope;
    }
}
