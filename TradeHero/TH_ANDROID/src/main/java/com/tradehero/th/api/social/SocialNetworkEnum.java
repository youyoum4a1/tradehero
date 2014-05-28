package com.tradehero.th.api.social;

import com.fasterxml.jackson.annotation.JsonValue;
import com.tradehero.th.models.user.auth.*;

public enum SocialNetworkEnum
{
    FB(FacebookCredentialsDTO.FACEBOOK_AUTH_TYPE, "Facebook"),
    LN(LinkedinCredentialsDTO.LINKEDIN_AUTH_TYPE, "LinkedIn"),
    TH(EmailCredentialsDTO.EMAIL_AUTH_TYPE, "TradeHero"),
    TW(TwitterCredentialsDTO.TWITTER_AUTH_TYPE, "Twitter"),
    WEIBO(WeiboCredentialsDTO.WEIBO_AUTH_TYPE, "WB"),
    WECHAT(WeChatCredentialsDTO.WECHAT_AUTH_TYPE, "WeChat");

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

    @JsonValue
    public String getName()
    {
        return name;
    }

    @Override
    public String toString() {
        // TODO need to improve
        if ("WB".equals(name))
        {
            return name;
        }
        return super.toString();
    }
}