package com.tradehero.th.api.social;

import com.tradehero.th.models.user.auth.EmailCredentialsDTO;
import com.tradehero.th.models.user.auth.FacebookCredentialsDTO;
import com.tradehero.th.models.user.auth.LinkedinCredentialsDTO;
import com.tradehero.th.models.user.auth.TwitterCredentialsDTO;
import com.tradehero.th.models.user.auth.WeChatCredentialsDTO;

public enum SocialNetworkEnum
{
    FB(FacebookCredentialsDTO.FACEBOOK_AUTH_TYPE, "Facebook"),
    LN(LinkedinCredentialsDTO.LINKEDIN_AUTH_TYPE, "LinkedIn"),
    TH(EmailCredentialsDTO.EMAIL_AUTH_TYPE, "TradeHero"),
    TW(TwitterCredentialsDTO.TWITTER_AUTH_TYPE, "Twitter"),
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

    public String getName()
    {
        return name;
    }
}