package com.tradehero.th.auth;

import com.tradehero.th.api.social.SocialNetworkEnum;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AuthData
{
    public final SocialNetworkEnum socialNetworkEnum;
    public final String accessToken;
    public final String accessTokenSecret;
    public final String password;
    public final Date expirationDate;

    public AuthData(SocialNetworkEnum socialNetworkEnum, Date expirationDate, String accessToken)
    {
        this(socialNetworkEnum, expirationDate, accessToken, null);
    }

    public AuthData(SocialNetworkEnum socialNetworkEnum, Date expirationDate, String accessToken, String accessTokenSecret)
    {
        this(socialNetworkEnum, expirationDate, accessToken, accessTokenSecret, null);
    }

    public AuthData(SocialNetworkEnum socialNetworkEnum, Date expirationDate, String accessToken, String accessTokenSecret, String password)
    {
        this.socialNetworkEnum = socialNetworkEnum;
        this.accessToken = accessToken;
        this.accessTokenSecret = accessTokenSecret;
        this.password = password;
        this.expirationDate = expirationDate;
    }

    public final Map<String, String> getTokenMap()
    {
        Map<String, String> tokenMap = new HashMap<>();
        if (socialNetworkEnum.getAccessTokenName() != null)
        {
            tokenMap.put(socialNetworkEnum.getAccessTokenName(), accessToken);
        }
        if (socialNetworkEnum.getAccessTokenSecretName() != null)
        {
            tokenMap.put(socialNetworkEnum.getAccessTokenSecretName(), accessTokenSecret);
        }
        return Collections.unmodifiableMap(tokenMap);
    }

    public final String getTHToken()
    {
        return String.format("%s %s", socialNetworkEnum.getAuthHeader(), accessToken);
    }
}
