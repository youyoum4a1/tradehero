package com.androidth.general.auth;

import android.util.Base64;
import com.androidth.general.api.social.SocialNetworkEnum;
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
    public final String email;

    //<editor-fold desc="Constructors">
    public AuthData(String email, String password)
    {
        this.socialNetworkEnum = SocialNetworkEnum.TH;
        this.password = password;
        this.expirationDate = null;
        this.accessToken = Base64.encodeToString(
                String.format("%s:%s", email, password).getBytes(),
                Base64.NO_WRAP);
        this.email = email;
        this.accessTokenSecret = null;
    }

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
        this.email = null;
    }
    //</editor-fold>

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
        StringBuilder sb = new StringBuilder(socialNetworkEnum.getAuthHeader())
                .append(" ")
                .append(accessToken);
        if (accessTokenSecret != null)
        {
            sb.append(":").append(accessTokenSecret);
        }
        return sb.toString();
    }

    @Override public String toString()
    {
        return "AuthData{" +
                "socialNetworkEnum=" + socialNetworkEnum +
                ", accessToken='" + accessToken + '\'' +
                ", accessTokenSecret='" + accessTokenSecret + '\'' +
                ", password='" + password + '\'' +
                ", expirationDate=" + expirationDate +
                ", email='" + email + '\'' +
                '}';
    }
}
