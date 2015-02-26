package com.tradehero.th.api.market;

import com.tradehero.common.persistence.DTO;

public class ExchangeCompactSectorListDTO implements DTO
{
    public ExchangeCompactDTOList exchanges;
    public SectorDTOList sectors;

    public ExchangeCompactSectorListDTO()
    {
    }

    public ExchangeCompactSectorListDTO(ExchangeCompactDTOList exchanges, SectorDTOList sectors)
    {
        this.exchanges = exchanges;
        this.sectors = sectors;
    }
}
