package com.tradehero.th.api.users.signup;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.th.api.misc.DeviceType;
import com.tradehero.th.api.users.LoginSignUpFormDTO;

public class LoginSignUpFormDeviceDTO extends LoginSignUpFormDTO
{
    @JsonProperty("device_access_token")
    public String deviceAccessToken;

    //<editor-fold desc="Constructors">
    public LoginSignUpFormDeviceDTO(String deviceToken, DeviceType deviceType, String clientVersion)
    {
        super(deviceToken, deviceType, clientVersion);
    }
    //</editor-fold>
}
