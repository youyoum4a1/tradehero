package com.tradehero.th.api.form;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QQUserFormDTO extends UserFormDTO
{
    @JsonProperty("qq_openid")
    public String openid;
    @JsonProperty("qq_access_token")
    public String accessToken;
}
