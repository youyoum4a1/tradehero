package com.tradehero.th.api.users.signup;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.th.api.misc.DeviceType;
import com.tradehero.th.api.users.LoginSignUpFormDTO;

public class LoginSignUpFormWeiboDTO extends LoginSignUpFormDTO
{
    @JsonProperty("weibo_access_token")
    public String accessToken;

    //<editor-fold desc="Constructors">
    public LoginSignUpFormWeiboDTO(String deviceToken, DeviceType deviceType, String clientVersion)
    {
        super(deviceToken, deviceType, clientVersion);
    }
    //</editor-fold>
}
