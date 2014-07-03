package com.tradehero.th.api.security.key;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TrendingSecurityListType extends SecurityListType
{
    // Null is for all exchanges
    @Nullable public final String exchange;

    //<editor-fold desc="Constructors">
    protected TrendingSecurityListType(@NotNull TrendingSecurityListType other)
    {
        super(other);
        this.exchange = other.exchange;
    }

    public TrendingSecurityListType(@Nullable String exchange, @Nullable Integer page, @Nullable Integer perPage)
    {
        super(page, perPage);
        this.exchange = exchange;
    }

    public TrendingSecurityListType(@Nullable String exchange, @Nullable Integer page)
    {
        super(page);
        this.exchange = exchange;
    }

    public TrendingSecurityListType(@Nullable String exchange)
    {
        super();
        this.exchange = exchange;
    }

    public TrendingSecurityListType(@Nullable Integer page, @Nullable Integer perPage)
    {
        this(null, page, perPage);
    }

    public TrendingSecurityListType(@Nullable Integer page)
    {
        this((String) null, page);
    }

    public TrendingSecurityListType()
    {
        this((String) null);
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^
                (exchange == null ? 0 : exchange.hashCode());
    }

    @Override public boolean equals(SecurityListType other)
    {
        return TrendingSecurityListType.class.isInstance(other) && equals(TrendingSecurityListType.class.cast(other));
    }

    public boolean equals(TrendingSecurityListType other)
    {
        return super.equals(other) &&
                (exchange == null ? other.exchange == null : exchange.equals(other.exchange));
    }

    //<editor-fold desc="Comparable">
    @Override public int compareTo(SecurityListType another)
    {
        if (another == null)
        {
            return 1;
        }

        if (!TrendingSecurityListType.class.isInstance(another))
        {
            // TODO is it very expensive?
            return TrendingSecurityListType.class.getName().compareTo(another.getClass().getName());
        }

        return compareTo(TrendingSecurityListType.class.cast(another));
    }

    public int compareTo(TrendingSecurityListType trendingSecurityListType)
    {
        if (trendingSecurityListType == null)
        {
            return 1;
        }
        int exchangeCompare = exchange.compareTo(trendingSecurityListType.exchange);
        if (exchangeCompare != 0)
        {
            return exchangeCompare;
        }
        return super.compareTo(trendingSecurityListType);
    }
    //</editor-fold>

    @Override public String toString()
    {
        return "TrendingSecurityListType{" +
                "exchange='" + exchange + "'" +
                ", page=" + getPage() +
                ", perPage=" + perPage +
                '}';
    }
}
