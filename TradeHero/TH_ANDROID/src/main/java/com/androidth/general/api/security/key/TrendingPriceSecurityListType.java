package com.androidth.general.api.security.key;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class TrendingPriceSecurityListType extends TrendingSecurityListType
{
    //<editor-fold desc="Constructors">
    public TrendingPriceSecurityListType(@Nullable String exchange, @Nullable Integer page, @Nullable Integer perPage)
    {
        super(exchange, page, perPage);
    }
    //</editor-fold>

    @Override protected boolean equalFields(@NonNull TrendingSecurityListType other)
    {
        return super.equalFields(other)
                && other instanceof TrendingPriceSecurityListType;
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
            return TrendingPriceSecurityListType.class.getName().compareTo(((Object) another).getClass().getName());
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
