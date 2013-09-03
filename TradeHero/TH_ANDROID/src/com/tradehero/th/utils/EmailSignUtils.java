package com.tradehero.th.utils;

import com.tradehero.th.auth.EmailAuthenticationProvider;
import com.tradehero.th.base.THUser;

/** Created with IntelliJ IDEA. User: xavier Date: 9/3/13 Time: 12:29 PM To change this template use File | Settings | File Templates. */
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
