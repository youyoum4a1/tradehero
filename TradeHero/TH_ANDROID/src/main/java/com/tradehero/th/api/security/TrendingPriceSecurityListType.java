package com.tradehero.th.api.security;

/** Created with IntelliJ IDEA. User: xavier Date: 10/8/13 Time: 3:19 PM To change this template use File | Settings | File Templates. */
public class TrendingPriceSecurityListType extends TrendingSecurityListType
{
    //<editor-fold desc="Constructors">
    public TrendingPriceSecurityListType()
    {
        super();
    }

    public TrendingPriceSecurityListType(String exchange)
    {
        super(exchange);
    }
    //</editor-fold>

    @Override public boolean equals(TrendingSecurityListType other)
    {
        return (other instanceof TrendingPriceSecurityListType) && super.equals(other);
    }
}
