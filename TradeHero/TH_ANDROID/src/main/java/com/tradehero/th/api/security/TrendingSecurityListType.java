package com.tradehero.th.api.security;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 5:07 PM To change this template use File | Settings | File Templates. */
public class TrendingSecurityListType extends SecurityListType
{
    public static final String TAG = TrendingSecurityListType.class.getSimpleName();

    @Override public int compareTo(SecurityListType securityListType)
    {
        if (securityListType instanceof TrendingSecurityListType)
        {
            return 0;
        }

        // TODO is it very expensive?
        return TrendingSecurityListType.class.getName().compareTo(securityListType.getClass().getName());
    }

    @Override public String makeKey()
    {
        return TrendingSecurityListType.class.getName();
    }

    @Override public String toString()
    {
        return String.format("[%s]", TAG);
    }
}
