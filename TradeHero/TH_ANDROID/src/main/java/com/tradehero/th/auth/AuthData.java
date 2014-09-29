package com.tradehero.th.auth;

import com.tradehero.th.api.social.SocialNetworkEnum;
import java.util.Date;

public class AuthData
{
    public final SocialNetworkEnum socialNetworkEnum;
    public final String accessToken;
    public final String password;
    public final Date expirationDate;

    public AuthData(SocialNetworkEnum socialNetworkEnum, Date expirationDate, String accessToken)
    {
        this(socialNetworkEnum, expirationDate, accessToken, null);
    }

    public AuthData(SocialNetworkEnum socialNetworkEnum, Date expirationDate, String accessToken, String password)
    {
        this.socialNetworkEnum = socialNetworkEnum;
        this.accessToken = accessToken;
        this.password = password;
        this.expirationDate = expirationDate;
    }

    public final String getTHToken()
    {
        return String.format("%s %s", socialNetworkEnum.getAuthHeader(), accessToken);
    }
}
