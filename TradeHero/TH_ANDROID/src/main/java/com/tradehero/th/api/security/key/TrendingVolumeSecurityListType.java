package com.tradehero.th.api.security.key;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TrendingVolumeSecurityListType extends TrendingSecurityListType
{
    //<editor-fold desc="Constructors">
    public TrendingVolumeSecurityListType(@NotNull TrendingSecurityListType other)
    {
        super(other);
    }

    public TrendingVolumeSecurityListType(@Nullable String exchange, @Nullable Integer page, @Nullable Integer perPage)
    {
        super(exchange, page, perPage);
    }

    public TrendingVolumeSecurityListType(@Nullable String exchange, @Nullable Integer page)
    {
        super(exchange, page);
    }

    public TrendingVolumeSecurityListType(@Nullable String exchange)
    {
        super(exchange);
    }

    public TrendingVolumeSecurityListType(@Nullable Integer page, @Nullable Integer perPage)
    {
        super(page, perPage);
    }

    public TrendingVolumeSecurityListType(@Nullable Integer page)
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
