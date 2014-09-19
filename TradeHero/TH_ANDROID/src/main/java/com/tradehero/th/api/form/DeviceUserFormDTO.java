package com.tradehero.th.api.form;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeviceUserFormDTO extends UserFormDTO
{
    @JsonProperty("device_access_token")
    public String deviceAccessToken;
}
