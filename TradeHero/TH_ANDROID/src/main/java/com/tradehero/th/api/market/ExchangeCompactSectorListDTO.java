package com.ayondo.academy.api.market;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.DTO;
import java.util.ArrayList;
import java.util.List;

public class ExchangeCompactSectorListDTO implements DTO
{
    @NonNull public final ExchangeCompactDTOList exchanges;
    @NonNull public final Iterable<? extends SectorCompactDTO> sectors;

    //<editor-fold desc="Constructors">
    public ExchangeCompactSectorListDTO(@NonNull ExchangeCompactDTOList exchanges, @NonNull Iterable<? extends SectorCompactDTO> sectors)
    {
        this.exchanges = exchanges;
        this.sectors = sectors;
    }
    //</editor-fold>

    @NonNull public List<SectorId> getSectorIds()
    {
        List<SectorId> list = new ArrayList<>();
        for (SectorCompactDTO compactDTO : sectors)
        {
            list.add(compactDTO.getSectorId());
        }
        return list;
    }
}
