package com.tradehero.th.api.security;

/** Created with IntelliJ IDEA. User: xavier Date: 10/8/13 Time: 3:19 PM To change this template use File | Settings | File Templates. */
public class TrendingBasicSecurityListType extends TrendingSecurityListType
{
    //<editor-fold desc="Constructors">
    public TrendingBasicSecurityListType()
    {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public TrendingBasicSecurityListType(String exchange)
    {
        super(exchange);    //To change body of overridden methods use File | Settings | File Templates.
    }
    //</editor-fold>

    @Override public boolean equals(TrendingSecurityListType other)
    {
        if (!(other instanceof TrendingBasicSecurityListType))
        {
            return false;
        }
        return equals((TrendingBasicSecurityListType) other);
    }

    public boolean equals(TrendingBasicSecurityListType other)
    {
        return super.equals(other);
    }

    @Override public int compareTo(SecurityListType securityListType)
    {
        if (securityListType == null)
        {
            return 1;
        }

        if (securityListType instanceof TrendingBasicSecurityListType)
        {
            return super.compareTo(securityListType);
        }
        return TrendingBasicSecurityListType.class.getName().compareTo(securityListType.getClass().getName());
    }

    @Override public String makeKey()
    {
        return String.format("%s:%s", TrendingBasicSecurityListType.class.getName(), getExchange());
    }
}
