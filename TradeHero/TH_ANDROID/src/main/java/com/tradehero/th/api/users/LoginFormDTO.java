package com.tradehero.th.api.users;

import com.tradehero.th.api.misc.ClientType;

/** Created with IntelliJ IDEA. User: xavier Date: 11/14/13 Time: 7:33 PM To change this template use File | Settings | File Templates. */
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

    public LoginFormDTO(String deviceToken, ClientType clientType, String clientVersion)
    {
        this(deviceToken, false, clientVersion);
        this.clientType = clientType.getServerValue();
    }
}
