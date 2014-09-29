package com.tradehero.th.api.users;

import com.tradehero.th.api.misc.DeviceType;

abstract public class LoginSignUpFormDTO extends LoginFormDTO
{
    public boolean useOnlyHeroCount;
    //public String device_access_token;

    public LoginSignUpFormDTO(String deviceToken, DeviceType deviceType, String clientVersion, String device_access_token)
    {
        super(deviceToken, deviceType, clientVersion, device_access_token);
    }
}
