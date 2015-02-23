package com.tradehero.th.api.market;

import com.tradehero.common.persistence.DTO;

public class ExchangeSectorListDTO implements DTO
{
    public ExchangeDTOList exchanges;
    public SectorDTOList sectors;

    public ExchangeSectorListDTO()
    {
    }

    public ExchangeSectorListDTO(ExchangeDTOList exchanges, SectorDTOList sectors)
    {
        this.exchanges = exchanges;
        this.sectors = sectors;
    }
}
