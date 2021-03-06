package com.tradehero.th.api.social;

import android.support.annotation.StringRes;

import com.tradehero.th.R;

/**
 * List of social network or 3rd party that provide authentication, this implementation is closely linked with https://github
 * .com/TradeHero/TH_ANDROID/blob/db4a33ee064f4e1c15a4b2b796165add706ab106/TradeHero/TH_ANDROID/res/values/attrs.xml#L110-120 by the order of login
 * mechanism.
 */
public enum SocialNetworkEnum
{
    TH("Basic", "TradeHero", R.string.app_name),
    FB("TH-Facebook", "Facebook", "facebook_access_token", R.string.facebook),
    TW("TH-Twitter", "Twitter", "twitter_access_token", "twitter_access_token_secret", R.string.twitter),
    LN("TH-LinkedIn", "LinkedIn", "linkedin_access_token", "linkedin_access_token_secret", R.string.linkedin),
    WECHAT("TH-WeChat", "WeChat", R.string.wechat),
    WB("TH-Weibo", "WeiBo", "weibo_access_token", R.string.sina_weibo),
    QQ("TH-QQ", "QQ", "qq_openid", "qq_access_token", R.string.tencent_qq);

    private final String authHeader;
    private final String name;
    private final String accessTokenName;
    private final String accessTokenSecretName;
    @StringRes public final int nameResId;

    SocialNetworkEnum(String authHeader,
            String name,
            @StringRes int nameResId)
    {
        this(authHeader, name, null, nameResId);
    }

    SocialNetworkEnum(String authHeader,
            String name,
            String accessTokenName,
            @StringRes int nameResId)
    {
        this(authHeader, name, accessTokenName, null, nameResId);
    }

    SocialNetworkEnum(String authHeader,
            String name,
            String accessTokenName,
            String accessTokenSecretName,
            @StringRes int nameResId)
    {
        this.authHeader = authHeader;
        this.name = name;
        this.accessTokenName = accessTokenName;
        this.accessTokenSecretName = accessTokenSecretName;
        this.nameResId = nameResId;
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
        if (index >= 0 && index < values().length)
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