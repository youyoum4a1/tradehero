package com.tradehero.th.api.security.key;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TrendingPriceSecurityListType extends TrendingSecurityListType
{
    //<editor-fold desc="Constructors">
    public TrendingPriceSecurityListType(@NotNull TrendingSecurityListType other)
    {
        super(other);
    }

    public TrendingPriceSecurityListType(@Nullable String exchange, @Nullable Integer page, @Nullable Integer perPage)
    {
        super(exchange, page, perPage);
    }

    public TrendingPriceSecurityListType(@Nullable String exchange, @Nullable Integer page)
    {
        super(exchange, page);
    }

    public TrendingPriceSecurityListType(@Nullable String exchange)
    {
        super(exchange);
    }

    public TrendingPriceSecurityListType(@Nullable Integer page, @Nullable Integer perPage)
    {
        super(page, perPage);
    }

    public TrendingPriceSecurityListType(@Nullable Integer page)
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
