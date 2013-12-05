package com.tradehero.th.api.security;

/** Created with IntelliJ IDEA. User: xavier Date: 10/8/13 Time: 3:19 PM To change this template use File | Settings | File Templates. */
public class TrendingBasicSecurityListType extends TrendingSecurityListType
{
    //<editor-fold desc="Constructors">
    public TrendingBasicSecurityListType(String exchange, Integer page, Integer perPage)
    {
        super(exchange, page, perPage);
    }

    public TrendingBasicSecurityListType(String exchange, Integer page)
    {
        super(exchange, page);
    }

    public TrendingBasicSecurityListType(String exchange)
    {
        super(exchange);
    }

    public TrendingBasicSecurityListType(Integer page, Integer perPage)
    {
        super(page, perPage);
    }

    public TrendingBasicSecurityListType(Integer page)
    {
        super(page);
    }

    public TrendingBasicSecurityListType()
    {
        super();
    }
    //</editor-fold>

    @Override public boolean equals(TrendingSecurityListType other)
    {
        return (other instanceof TrendingBasicSecurityListType) && super.equals(other);
    }

    @Override public int compareTo(TrendingSecurityListType another)
    {
        if (another == null)
        {
            return 1;
        }

        if (!TrendingBasicSecurityListType.class.isInstance(another))
        {
            // TODO is it very expensive?
            return TrendingBasicSecurityListType.class.getName().compareTo(another.getClass().getName());
        }

        return compareTo(TrendingBasicSecurityListType.class.cast(another));
    }

    public int compareTo(TrendingBasicSecurityListType another)
    {
        return super.compareTo(another);
    }
}
