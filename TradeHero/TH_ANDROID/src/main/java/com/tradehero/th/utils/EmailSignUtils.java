package com.tradehero.th.utils;

import com.tradehero.th.auth.EmailAuthenticationProvider;
import com.tradehero.th.base.THUser;


public class EmailSignUtils
{
    private static EmailAuthenticationProvider provider;
    private static boolean isInitialized = false;

    public static void initialize()
    {
        provider = new EmailAuthenticationProvider();
        THUser.registerAuthenticationProvider(provider);
        isInitialized = true;
    }
}
