package com.androidth.general.auth.tencent_qq;

import com.fasterxml.jackson.annotation.JsonProperty;

class QQAppAuthData
{
    public static final String JSON_KEY_OPEN_ID = "openid";
    public static final String JSON_KEY_EXPIRES_IN = "expires_in";
    public static final String JSON_KEY_ACCESS_TOKEN = "access_token";

    @JsonProperty(JSON_KEY_OPEN_ID)
    public String openId;
    @JsonProperty(JSON_KEY_EXPIRES_IN)
    public String expiresIn;
    @JsonProperty(JSON_KEY_ACCESS_TOKEN)
    public String accessToken;
}
