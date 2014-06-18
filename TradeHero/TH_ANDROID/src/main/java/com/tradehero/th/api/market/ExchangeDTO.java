package com.tradehero.th.api.market;

import android.os.Bundle;
import java.util.List;

public class ExchangeDTO extends ExchangeCompactDTO
{
    public List<SectorDTO> sectors;

    //<editor-fold desc="Constructors">
    public ExchangeDTO()
    {
        super();
    }

    public ExchangeDTO(
            int id, String name,
            String countryCode,
            double sumMarketCap,
            String desc,
            boolean isInternal,
            boolean isIncludedInTrending,
            boolean chartDataSource,
            List<SectorDTO> sectors)
    {
        super(id, name, countryCode, sumMarketCap, desc, isInternal, isIncludedInTrending, chartDataSource);
        this.sectors = sectors;
    }

    public ExchangeDTO(int id, String name, double sumMarketCap, List<SectorDTO> sectors, String desc, boolean isInternal,
            boolean isIncludedInTrending)
    {
        this.id = id;
        this.name = name;
        this.sumMarketCap = sumMarketCap;
        this.sectors = sectors;
        this.desc = desc;
        this.isInternal = isInternal;
        this.isIncludedInTrending = isIncludedInTrending;
    }

    public ExchangeDTO(ExchangeDTO other)
    {
        super(other);
        this.sectors = other.sectors;
    }

    public ExchangeDTO(Bundle bundle)
    {
        super(bundle);
    }
    //</editor-fold>
}
