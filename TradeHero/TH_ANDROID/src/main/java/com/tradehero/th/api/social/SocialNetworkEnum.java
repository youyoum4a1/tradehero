package com.tradehero.th.api.social;

import com.tradehero.th.auth.SocialAuthenticationProvider;

/** Created with IntelliJ IDEA. User: tho Date: 11/19/13 Time: 5:25 PM Copyright (c) TradeHero */
public enum SocialNetworkEnum
{
    FB(SocialAuthenticationProvider.FACEBOOK_AUTH_TYPE, "Facebook"),
    LN(SocialAuthenticationProvider.LINKEDIN_AUTH_TYPE, "LinkedIn"),
    TH(SocialAuthenticationProvider.TRADEHERO_AUTH_TYPE, "TradeHero"),
    TW(SocialAuthenticationProvider.TWITTER_AUTH_TYPE, "Twitter"),
    WECHAT(SocialAuthenticationProvider.WECHAT_AUTH_TYPE, "WeChat");

    private final String authenticationHeader;
    private final String name;

    SocialNetworkEnum(String authenticationHeader, String name)
    {
        this.authenticationHeader = authenticationHeader;
        this.name = name;
    }

    public String getAuthenticationHeader()
    {
        return authenticationHeader;
    }

    public String getName()
    {
        return name;
    }
}