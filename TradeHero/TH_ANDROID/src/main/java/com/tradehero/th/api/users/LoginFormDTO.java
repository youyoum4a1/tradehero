package com.tradehero.th.api.users;

/** Created with IntelliJ IDEA. User: xavier Date: 11/14/13 Time: 7:33 PM To change this template use File | Settings | File Templates. */
public class LoginFormDTO
{
    public static final String TAG = LoginFormDTO.class.getSimpleName();

    public String deviceToken;

    // min. version support
    public boolean clientiOS;
    public String clientVersion;
}
