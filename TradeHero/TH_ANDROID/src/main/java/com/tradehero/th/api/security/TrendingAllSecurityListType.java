package com.tradehero.th.api.security;

/** Created with IntelliJ IDEA. User: xavier Date: 10/8/13 Time: 3:19 PM To change this template use File | Settings | File Templates. */
public class TrendingAllSecurityListType extends TrendingSecurityListType
{
    //<editor-fold desc="Constructors">
    public TrendingAllSecurityListType(String exchange, Integer page, Integer perPage)
    {
        super(exchange, page, perPage);
    }

    public TrendingAllSecurityListType(String exchange, Integer page)
    {
        super(exchange, page);
    }

    public TrendingAllSecurityListType(String exchange)
    {
        super(exchange);
    }

    public TrendingAllSecurityListType(Integer page, Integer perPage)
    {
        super(page, perPage);
    }

    public TrendingAllSecurityListType(Integer page)
    {
        super(page);
    }

    public TrendingAllSecurityListType()
    {
        super();
    }
    //</editor-fold>

    @Override public boolean equals(TrendingSecurityListType other)
    {
        return (other instanceof TrendingAllSecurityListType) && super.equals(other);
    }

    @Override public int compareTo(TrendingSecurityListType another)
    {
        if (another == null)
        {
            return 1;
        }

        if (!TrendingAllSecurityListType.class.isInstance(another))
        {
            // TODO is it very expensive?
            return TrendingAllSecurityListType.class.getName().compareTo(another.getClass().getName());
        }

        return compareTo(TrendingAllSecurityListType.class.cast(another));
    }

    public int compareTo(TrendingAllSecurityListType another)
    {
        return super.compareTo(another);
    }

    @Override public String toString()
    {
        return "TrendingAllSecurityListType{" +
                "exchange='" + exchange + "'" +
                ", page=" + page +
                ", perPage=" + perPage +
                '}';
    }
}
