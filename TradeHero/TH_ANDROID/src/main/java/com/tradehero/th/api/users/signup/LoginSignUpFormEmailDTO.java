package com.tradehero.th.api.users.signup;

import com.tradehero.th.api.misc.DeviceType;
import com.tradehero.th.api.users.LoginSignUpFormDTO;

public class LoginSignUpFormEmailDTO extends LoginSignUpFormDTO
{
    public boolean isEmailLogin = true;

    //<editor-fold desc="Constructors">
    public LoginSignUpFormEmailDTO(String deviceToken, DeviceType deviceType, String clientVersion, String device_access_token)
    {
        super(deviceToken, deviceType, clientVersion, device_access_token);
    }
    //</editor-fold>
}
