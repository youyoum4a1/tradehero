package com.tradehero.th.api.social;

public enum SocialNetworkEnum
{
    TH("Basic", "TradeHero"),
    FB("TH-Facebook", "Facebook"),
    TW("TH-Twitter", "Twitter"),
    LN("TH-LinkedIn", "LinkedIn"),
    WECHAT("TH-WeChat", "WeChat"),
    WB("TH-Weibo", "WeiBo"),
    QQ("TH-QQ", "QQ");

    private final String authHeader;
    private final String name;

    SocialNetworkEnum(String authHeader, String name)
    {
        this.authHeader = authHeader;
        this.name = name;
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
}