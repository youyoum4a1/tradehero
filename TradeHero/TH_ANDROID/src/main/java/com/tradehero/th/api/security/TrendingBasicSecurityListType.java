package com.tradehero.th.api.security;

/** Created with IntelliJ IDEA. User: xavier Date: 10/8/13 Time: 3:19 PM To change this template use File | Settings | File Templates. */
public class TrendingBasicSecurityListType extends TrendingSecurityListType
{
    //<editor-fold desc="Constructors">
    public TrendingBasicSecurityListType()
    {
        super();
    }

    public TrendingBasicSecurityListType(String exchange)
    {
        super(exchange);
    }
    //</editor-fold>

    @Override public boolean equals(TrendingSecurityListType other)
    {
        return (other instanceof TrendingBasicSecurityListType) && super.equals(other);
    }
}
