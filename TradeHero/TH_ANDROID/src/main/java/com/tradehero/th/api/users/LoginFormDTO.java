package com.tradehero.th.api.users;

import com.tradehero.th.api.misc.DeviceType;

public class LoginFormDTO
{
    public final String deviceToken;
    public final DeviceType clientType;

    public int channelType;

    // min. version support
    public final String clientVersion;
    public final String device_access_token;

    public LoginFormDTO(String deviceToken, DeviceType deviceType, String clientVersion, String device_access_token)
    {
        this.deviceToken = deviceToken;
        this.clientVersion = clientVersion;
        this.clientType = deviceType;
        this.device_access_token = device_access_token;
    }
}
