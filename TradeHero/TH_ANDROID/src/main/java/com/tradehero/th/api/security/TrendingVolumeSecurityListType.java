package com.tradehero.th.api.security;

/** Created with IntelliJ IDEA. User: xavier Date: 10/8/13 Time: 3:19 PM To change this template use File | Settings | File Templates. */
public class TrendingVolumeSecurityListType extends TrendingSecurityListType
{
    //<editor-fold desc="Constructors">
    public TrendingVolumeSecurityListType()
    {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public TrendingVolumeSecurityListType(String exchange)
    {
        super(exchange);    //To change body of overridden methods use File | Settings | File Templates.
    }
    //</editor-fold>

    @Override public int compareTo(SecurityListType securityListType)
    {
        if (securityListType == null)
        {
            return 1;
        }

        if (securityListType instanceof TrendingVolumeSecurityListType)
        {
            return super.compareTo(securityListType);
        }
        return TrendingVolumeSecurityListType.class.getName().compareTo(securityListType.getClass().getName());
    }
}
