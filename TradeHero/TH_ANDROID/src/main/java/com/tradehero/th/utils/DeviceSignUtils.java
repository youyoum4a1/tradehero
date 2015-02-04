package com.tradehero.th.utils;

import com.tradehero.th.auth.DeviceAuthenticationProvider;
import com.tradehero.th.base.THUser;

public class DeviceSignUtils
{
    private static DeviceAuthenticationProvider provider;

    public static void initialize()
    {
        provider = new DeviceAuthenticationProvider();
        THUser.registerAuthenticationProvider(provider);
    }
}
