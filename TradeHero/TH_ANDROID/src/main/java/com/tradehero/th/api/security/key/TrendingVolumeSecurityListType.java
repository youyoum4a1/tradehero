package com.tradehero.th.api.security.key;

/** Created with IntelliJ IDEA. User: xavier Date: 10/8/13 Time: 3:19 PM To change this template use File | Settings | File Templates. */
public class TrendingVolumeSecurityListType extends TrendingSecurityListType
{
    //<editor-fold desc="Constructors">
    public TrendingVolumeSecurityListType(TrendingSecurityListType other)
    {
        super(other);
    }

    public TrendingVolumeSecurityListType(String exchange, Integer page, Integer perPage)
    {
        super(exchange, page, perPage);
    }

    public TrendingVolumeSecurityListType(String exchange, Integer page)
    {
        super(exchange, page);
    }

    public TrendingVolumeSecurityListType(String exchange)
    {
        super(exchange);
    }

    public TrendingVolumeSecurityListType(Integer page, Integer perPage)
    {
        super(page, perPage);
    }

    public TrendingVolumeSecurityListType(Integer page)
    {
        super(page);
    }

    public TrendingVolumeSecurityListType()
    {
        super();
    }
    //</editor-fold>

    @Override public boolean equals(TrendingSecurityListType other)
    {
        return (other instanceof TrendingVolumeSecurityListType) && super.equals(other);
    }

    @Override public int compareTo(TrendingSecurityListType another)
    {
        if (another == null)
        {
            return 1;
        }

        if (!TrendingVolumeSecurityListType.class.isInstance(another))
        {
            // TODO is it very expensive?
            return TrendingVolumeSecurityListType.class.getName().compareTo(another.getClass().getName());
        }

        return compareTo(TrendingVolumeSecurityListType.class.cast(another));
    }

    public int compareTo(TrendingVolumeSecurityListType another)
    {
        return super.compareTo(another);
    }

    @Override public String toString()
    {
        return "TrendingVolumeSecurityListType{" +
                "exchange='" + exchange + "'" +
                ", page=" + getPage() +
                ", perPage=" + perPage +
                '}';
    }
}
