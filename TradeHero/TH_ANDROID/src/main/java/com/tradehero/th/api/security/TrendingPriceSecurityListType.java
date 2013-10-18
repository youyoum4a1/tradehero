package com.tradehero.th.api.security;

/** Created with IntelliJ IDEA. User: xavier Date: 10/8/13 Time: 3:19 PM To change this template use File | Settings | File Templates. */
public class TrendingPriceSecurityListType extends TrendingSecurityListType
{
    //<editor-fold desc="Constructors">
    public TrendingPriceSecurityListType()
    {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public TrendingPriceSecurityListType(String exchange)
    {
        super(exchange);    //To change body of overridden methods use File | Settings | File Templates.
    }
    //</editor-fold>

    @Override public boolean equals(TrendingSecurityListType other)
    {
        if (!(other instanceof TrendingPriceSecurityListType))
        {
            return false;
        }
        return equals((TrendingPriceSecurityListType) other);
    }

    public boolean equals(TrendingPriceSecurityListType other)
    {
        return super.equals(other);
    }

    @Override public int compareTo(SecurityListType securityListType)
    {
        if (securityListType == null)
        {
            return 1;
        }

        if (securityListType instanceof TrendingPriceSecurityListType)
        {
            return super.compareTo(securityListType);
        }
        return TrendingPriceSecurityListType.class.getName().compareTo(securityListType.getClass().getName());
    }

    @Override public String makeKey()
    {
        return String.format("%s:%s", TrendingPriceSecurityListType.class.getName(), getExchange());
    }
}
