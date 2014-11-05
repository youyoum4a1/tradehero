package com.tradehero.th.api.market;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ExchangeDTO extends ExchangeCompactDTO
{
    @Nullable public SectorDTOList sectors;

    //<editor-fold desc="Constructors">
    protected ExchangeDTO()
    {
        super();
    }

    public ExchangeDTO(
            int id,
            String name,
            String countryCode,
            double sumMarketCap,
            String desc,
            boolean isInternal,
            boolean isIncludedInTrending,
            boolean chartDataSource,
            @Nullable SectorDTOList sectors)
    {
        super(id, name, countryCode, sumMarketCap, desc, isInternal, isIncludedInTrending, chartDataSource);
        this.sectors = sectors;
    }

    public ExchangeDTO(@NonNull ExchangeDTO other)
    {
        super(other);
        this.sectors = other.sectors;
    }

    public ExchangeDTO(@NonNull Bundle bundle)
    {
        super(bundle);
    }
    //</editor-fold>
}
