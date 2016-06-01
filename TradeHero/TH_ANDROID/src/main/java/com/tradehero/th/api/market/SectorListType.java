package com.ayondo.academy.api.market;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.DTOKey;

public final class SectorListType implements DTOKey
{
    public static final int DEFAULT_TOP_N_STOCKS = 6;

    public final int topNStocks;

    //<editor-fold desc="Constructors">

    public SectorListType()
    {
        this(DEFAULT_TOP_N_STOCKS);
    }

    public SectorListType(int topNStocks)
    {
        this.topNStocks = topNStocks;
    }
    //</editor-fold>

    @Override public boolean equals(@NonNull Object other)
    {
        return other instanceof SectorListType
                && topNStocks == ((SectorListType) other).topNStocks;
    }
}
