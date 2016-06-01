package com.ayondo.academy.api.security.key;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class TrendingVolumeSecurityListType extends TrendingSecurityListType
{
    //<editor-fold desc="Constructors">
    public TrendingVolumeSecurityListType(@Nullable String exchange, @Nullable Integer page, @Nullable Integer perPage)
    {
        super(exchange, page, perPage);
    }
    //</editor-fold>

    @Override protected boolean equalFields(@NonNull TrendingSecurityListType other)
    {
        return super.equalFields(other)
                && other instanceof TrendingVolumeSecurityListType;
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
            return TrendingVolumeSecurityListType.class.getName().compareTo(((Object) another).getClass().getName());
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
