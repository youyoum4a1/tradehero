package com.tradehero.th.api.security.key;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TrendingAllSecurityListType extends TrendingSecurityListType
{
    //<editor-fold desc="Constructors">
    public TrendingAllSecurityListType(@NotNull TrendingSecurityListType other)
    {
        super(other);
    }

    public TrendingAllSecurityListType(@Nullable String exchange, @Nullable Integer page, @Nullable Integer perPage)
    {
        super(exchange, page, perPage);
    }

    public TrendingAllSecurityListType(@Nullable String exchange, @Nullable Integer page)
    {
        super(exchange, page);
    }

    public TrendingAllSecurityListType(@Nullable String exchange)
    {
        super(exchange);
    }

    public TrendingAllSecurityListType(@Nullable Integer page, @Nullable Integer perPage)
    {
        super(page, perPage);
    }

    public TrendingAllSecurityListType(@Nullable Integer page)
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
                ", page=" + getPage() +
                ", perPage=" + perPage +
                '}';
    }
}
