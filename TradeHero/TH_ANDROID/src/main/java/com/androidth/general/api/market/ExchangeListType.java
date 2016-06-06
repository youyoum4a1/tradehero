package com.androidth.general.api.market;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.common.persistence.AbstractPrimitiveDTOKey;
import com.androidth.general.common.persistence.AbstractStringDTOKey;

public final class ExchangeListType extends AbstractStringDTOKey
{
    public final static String BUNDLE_KEY_KEY = ExchangeListType.class.getName() + ".key";
    public final static String DEFAULT_KEY = "All";

    @Nullable public final Integer topNStocks;

    //<editor-fold desc="Constructors">
    public ExchangeListType()
    {
        super(DEFAULT_KEY);
        this.topNStocks = null;
    }

    public ExchangeListType(@Nullable Integer topNStocks)
    {
        super(DEFAULT_KEY);
        this.topNStocks = topNStocks;
    }
    //</editor-fold>

    @NonNull @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }

    @Override public int hashCode()
    {
        return super.hashCode() ^ (topNStocks == null ? 0 : topNStocks.hashCode());
    }

    @Override protected boolean equals(@NonNull AbstractPrimitiveDTOKey other)
    {
        return super.equals(other)
                && other instanceof ExchangeListType
                && equalFields((ExchangeListType) other);
    }

    protected boolean equalFields(@NonNull ExchangeListType other)
    {
        return super.equals(other)
                && (other.topNStocks == null ? topNStocks == null : other.topNStocks.equals(topNStocks));
    }
}
