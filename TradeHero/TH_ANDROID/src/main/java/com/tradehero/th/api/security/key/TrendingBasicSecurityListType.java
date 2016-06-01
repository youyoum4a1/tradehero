package com.ayondo.academy.api.security.key;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class TrendingBasicSecurityListType extends TrendingSecurityListType
{
    //<editor-fold desc="Constructors">
    public TrendingBasicSecurityListType(@Nullable String exchange, @Nullable Integer page, @Nullable Integer perPage)
    {
        super(exchange, page, perPage);
    }

    public TrendingBasicSecurityListType()
    {
        super();
    }
    //</editor-fold>

    @Override protected boolean equalFields(@NonNull TrendingSecurityListType other)
    {
        return super.equalFields(other)
                && other instanceof TrendingBasicSecurityListType;
    }

    @Override public int compareTo(TrendingSecurityListType another)
    {
        if (another == null)
        {
            return 1;
        }

        if (!TrendingBasicSecurityListType.class.isInstance(another))
        {
            // TODO is it very expensive?
            return TrendingBasicSecurityListType.class.getName().compareTo(((Object) another).getClass().getName());
        }

        return compareTo(TrendingBasicSecurityListType.class.cast(another));
    }

    public int compareTo(TrendingBasicSecurityListType another)
    {
        return super.compareTo(another);
    }

    @Override public String toString()
    {
        return "TrendingBasicSecurityListType{" +
                "exchange='" + exchange + "'" +
                ", page=" + getPage() +
                ", perPage=" + perPage +
                '}';
    }
}
