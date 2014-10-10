package com.tradehero.th.api.form;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WechatUserFormDTO extends UserFormDTO
{
    @JsonProperty("wc_openid")
    public String openid;
    @JsonProperty("wc_access_token")
    public String accessToken;
}
