package com.tradehero.th.api.users.signup;

import com.tradehero.th.api.misc.DeviceType;
import com.tradehero.th.api.users.LoginSignUpFormDTO;

public class LoginSignUpFormDeviceDTO extends LoginSignUpFormDTO
{
    //@JsonProperty("device_access_token")
    //public String deviceAccessToken;

    //<editor-fold desc="Constructors">
    public LoginSignUpFormDeviceDTO(String deviceToken, DeviceType deviceType, String clientVersion, String device_access_token)
    {
        super(deviceToken, deviceType, clientVersion, device_access_token);
    }
    //</editor-fold>
}
