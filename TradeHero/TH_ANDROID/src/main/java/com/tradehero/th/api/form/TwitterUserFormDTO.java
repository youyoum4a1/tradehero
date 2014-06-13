package com.tradehero.th.api.form;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TwitterUserFormDTO extends UserFormDTO
{
    @JsonProperty("twitter_access_token")
    public String accessToken;
    @JsonProperty("twitter_access_token_secret")
    public String accessTokenSecret;
}
