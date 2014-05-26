package com.tradehero.th.api.form;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FacebookUserFormDTO extends UserFormDTO
{
    @JsonProperty("facebook_access_token")
    public String accessToken;
}
