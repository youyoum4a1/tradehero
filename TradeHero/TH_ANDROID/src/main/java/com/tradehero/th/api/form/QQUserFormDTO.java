package com.tradehero.th.api.form;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QQUserFormDTO extends UserFormDTO
{
    @JsonProperty("qq_open_id")
    public String openId;
    @JsonProperty("qq_access_token")
    public String accessToken;
}
