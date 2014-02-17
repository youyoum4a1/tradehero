package com.tradehero.th.api.security.key;

/** Created with IntelliJ IDEA. User: xavier Date: 10/8/13 Time: 3:19 PM To change this template use File | Settings | File Templates. */
public class TrendingPriceSecurityListType extends TrendingSecurityListType
{
    //<editor-fold desc="Constructors">
    public TrendingPriceSecurityListType(TrendingSecurityListType other)
    {
        super(other);
    }

    public TrendingPriceSecurityListType(String exchange, Integer page, Integer perPage)
    {
        super(exchange, page, perPage);
    }

    public TrendingPriceSecurityListType(String exchange, Integer page)
    {
        super(exchange, page);
    }

    public TrendingPriceSecurityListType(String exchange)
    {
        super(exchange);
    }

    public TrendingPriceSecurityListType(Integer page, Integer perPage)
    {
        super(page, perPage);
    }

    public TrendingPriceSecurityListType(Integer page)
    {
        super(page);
    }

    public TrendingPriceSecurityListType()
    {
        super();
    }
    //</editor-fold>

    @Override public boolean equals(TrendingSecurityListType other)
    {
        return (other instanceof TrendingPriceSecurityListType) && super.equals(other);
    }

    @Override public int compareTo(TrendingSecurityListType another)
    {
        if (another == null)
        {
            return 1;
        }

        if (!TrendingPriceSecurityListType.class.isInstance(another))
        {
            // TODO is it very expensive?
            return TrendingPriceSecurityListType.class.getName().compareTo(another.getClass().getName());
        }

        return compareTo(TrendingPriceSecurityListType.class.cast(another));
    }

    public int compareTo(TrendingPriceSecurityListType another)
    {
        return super.compareTo(another);
    }

    @Override public String toString()
    {
        return "TrendingPriceSecurityListType{" +
                "exchange='" + exchange + "'" +
                ", page=" + getPage() +
                ", perPage=" + perPage +
                '}';
    }
}
