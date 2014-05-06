package com.tradehero.th.api.users;

import com.tradehero.th.api.misc.DeviceType;

public class LoginFormDTO
{
    public final String deviceToken;

    public final DeviceType clientType;

    // min. version support
    public final String clientVersion;

    public LoginFormDTO(String deviceToken, DeviceType deviceType, String clientVersion)
    {
        this.deviceToken = deviceToken;
        this.clientVersion = clientVersion;
        this.clientType = deviceType;
    }
}
