package com.tradehero.th.api.form;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WeiboUserFormDTO extends UserFormDTO
{
    @JsonProperty("weibo_access_token")
    public String accessToken;
}
