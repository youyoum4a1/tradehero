package com.tradehero.th.api.market;

import com.tradehero.common.persistence.DTO;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 10/18/13 Time: 5:54 PM To change this template use File | Settings | File Templates. */
public class ExchangeDTO implements DTO
{
    public int id;
    public String name;

    public double sumMarketCap;
    public List<SectorDTO> sectors;

    public String desc;
    public boolean isInternal;
    public boolean isIncludedInTrending;

    //<editor-fold desc="Constructors">
    public ExchangeDTO()
    {
        super();
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
        this.id = other.id;
        this.name = other.name;
        this.sumMarketCap = other.sumMarketCap;
        this.sectors = other.sectors;
        this.desc = other.desc;
        this.isInternal = other.isInternal;
        this.isIncludedInTrending = other.isIncludedInTrending;
    }
    //</editor-fold>

    public ExchangeIntegerId getExchangeIntegerId()
    {
        return new ExchangeIntegerId(id);
    }

    public ExchangeStringId getExchangeStringId()
    {
        return new ExchangeStringId(name);
    }

    @Override public boolean equals(Object other)
    {
        if (other == null || !(other instanceof ExchangeDTO))
        {
            return false;
        }
        if (name != null)
        {
            return name.equals(((ExchangeDTO) other).name);
        }
        return ((ExchangeDTO) other).name == null;
    }
}
