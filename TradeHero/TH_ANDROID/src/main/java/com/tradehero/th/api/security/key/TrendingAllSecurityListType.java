package com.ayondo.academy.api.security.key;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class TrendingAllSecurityListType extends TrendingSecurityListType
{
    //<editor-fold desc="Constructors">
    public TrendingAllSecurityListType(@Nullable String exchange, @Nullable Integer page, @Nullable Integer perPage)
    {
        super(exchange, page, perPage);
    }
    //</editor-fold>

    @Override protected boolean equalFields(@NonNull TrendingSecurityListType other)
    {
        return super.equalFields(other)
                && (other instanceof TrendingAllSecurityListType);
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
            return TrendingAllSecurityListType.class.getName().compareTo(((Object) another).getClass().getName());
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
