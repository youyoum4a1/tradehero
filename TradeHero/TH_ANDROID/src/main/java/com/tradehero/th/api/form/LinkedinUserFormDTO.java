package com.tradehero.th.api.form;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LinkedinUserFormDTO extends UserFormDTO
{
    @JsonProperty("linkedin_access_token")
    public String accessToken;
    @JsonProperty("linkedin_access_token_secret")
    public String accessTokenSecret;
}
