package com.tradehero.th.api.social;

import com.tradehero.th.models.user.auth.EmailCredentialsDTO;
import com.tradehero.th.models.user.auth.QQCredentialsDTO;
import com.tradehero.th.models.user.auth.WeChatCredentialsDTO;
import com.tradehero.th.models.user.auth.WeiboCredentialsDTO;

public enum SocialNetworkEnum
{
    TH(EmailCredentialsDTO.EMAIL_AUTH_TYPE, "TradeHero"),
    WECHAT(WeChatCredentialsDTO.WECHAT_AUTH_TYPE, "WeChat"),
    WB(WeiboCredentialsDTO.WEIBO_AUTH_TYPE, "WeiBo"),
    QQ(QQCredentialsDTO.QQ_AUTH_TYPE, "QQ");

    private final String authenticationHeader;
    private final String name;

    SocialNetworkEnum(String authenticationHeader, String name)
    {
        this.authenticationHeader = authenticationHeader;
        this.name = name;
    }

    //@JsonValue
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