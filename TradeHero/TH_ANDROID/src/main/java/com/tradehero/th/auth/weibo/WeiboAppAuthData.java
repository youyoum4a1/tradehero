package com.ayondo.academy.auth.weibo;

import android.support.annotation.NonNull;

public class WeiboAppAuthData
{
    @NonNull public final String appId;
    @NonNull public final String redirectUrl;
    @NonNull public final String scope;

    public WeiboAppAuthData(@NonNull String appId,
            @NonNull String redirectUrl,
            @NonNull String scope)
    {
        this.appId = appId;
        this.redirectUrl = redirectUrl;
        this.scope = scope;
    }
}
