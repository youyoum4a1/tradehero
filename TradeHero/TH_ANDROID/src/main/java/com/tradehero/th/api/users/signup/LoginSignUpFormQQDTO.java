package com.tradehero.th.api.users.signup;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.th.api.misc.DeviceType;
import com.tradehero.th.api.users.LoginSignUpFormDTO;

public class LoginSignUpFormQQDTO extends LoginSignUpFormDTO
{
    @JsonProperty("qq_access_token")
    public String accessToken;
    @JsonProperty("qq_openid")
    public String openId;

    //<editor-fold desc="Constructors">
    public LoginSignUpFormQQDTO(String deviceToken, DeviceType deviceType, String clientVersion, String device_access_token)
    {
        super(deviceToken, deviceType, clientVersion, device_access_token);
    }
    //</editor-fold>
}
