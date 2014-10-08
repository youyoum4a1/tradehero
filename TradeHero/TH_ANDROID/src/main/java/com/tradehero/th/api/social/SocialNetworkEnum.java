package com.tradehero.th.api.social;

/**
 * List of social network or 3rd party that provide authentication, this implementation is closely linked with https://github
 * .com/TradeHero/TH_ANDROID/blob/db4a33ee064f4e1c15a4b2b796165add706ab106/TradeHero/TH_ANDROID/res/values/attrs.xml#L110-120 by the order of login
 * mechanism.
 */
public enum SocialNetworkEnum
{
    TH("Basic", "TradeHero"),
    TH_SIGNUP("Basic", "TradeHero"),
    FB("TH-Facebook", "Facebook", "facebook_access_token"),
    TW("TH-Twitter", "Twitter", "twitter_access_token", "twitter_access_token_secret"),
    LN("TH-LinkedIn", "LinkedIn", "linkedin_access_token", "linkedin_access_token_secret"),
    WECHAT("TH-WeChat", "WeChat"),
    WB("TH-Weibo", "WeiBo", "weibo_access_token"),
    QQ("TH-QQ", "QQ", "qq_access_token", "qq_openid");

    private final String authHeader;
    private final String name;
    private final String accessTokenName;
    private final String accessTokenSecretName;

    SocialNetworkEnum(String authHeader, String name)
    {
        this(authHeader, name, null);
    }

    SocialNetworkEnum(String authHeader, String name, String accessTokenName)
    {
        this(authHeader, name, accessTokenName, null);
    }

    SocialNetworkEnum(String authHeader, String name, String accessTokenName, String accessTokenSecretName)
    {
        this.authHeader = authHeader;
        this.name = name;
        this.accessTokenName = accessTokenName;
        this.accessTokenSecretName = accessTokenSecretName;
    }

    public String getAuthHeader()
    {
        return authHeader;
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

    public static SocialNetworkEnum fromIndex(int index)
    {
        if (index >= 0 && index <= values().length)
        {
            return values()[index];
        }
        throw new IllegalArgumentException("There is no value for index " + index);
    }

    public static SocialNetworkEnum fromAuthHeader(String authHeader)
    {
        for (SocialNetworkEnum socialNetworkEnum: values())
        {
            if (socialNetworkEnum.authHeader.equals(authHeader))
            {
                return socialNetworkEnum;
            }
        }
        throw new IllegalArgumentException("There is no value with authentication header: " + authHeader);
    }

    public String getAccessTokenName()
    {
        return accessTokenName;
    }

    public String getAccessTokenSecretName()
    {
        return accessTokenSecretName;
    }
}