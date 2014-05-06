package com.tradehero.th.api.users;

import com.tradehero.th.api.misc.DeviceType;


public class LoginFormDTO
{
    public static final String TAG = LoginFormDTO.class.getSimpleName();

    public String deviceToken;

    @Deprecated
    public boolean clientiOS;

    public int clientType;

    // min. version support
    public String clientVersion;

    public LoginFormDTO(String deviceToken, boolean clientiOS, String clientVersion)
    {
        this.deviceToken = deviceToken;
        this.clientiOS = clientiOS;
        this.clientVersion = clientVersion;
    }

    public LoginFormDTO(String deviceToken, DeviceType deviceType, String clientVersion)
    {
        this(deviceToken, false, clientVersion);
        this.clientType = deviceType.getServerValue();
    }
}
