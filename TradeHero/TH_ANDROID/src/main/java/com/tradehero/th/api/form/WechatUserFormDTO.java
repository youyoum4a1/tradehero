package com.tradehero.th.api.form;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WechatUserFormDTO extends UserFormDTO
{
    @JsonProperty("wechat_openid")
    public String openid;
    @JsonProperty("wechat_access_token")
    public String accessToken;
}
