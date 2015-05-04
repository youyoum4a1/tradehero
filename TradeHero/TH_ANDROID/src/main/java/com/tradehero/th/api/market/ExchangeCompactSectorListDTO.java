package com.tradehero.th.api.market;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.DTO;

public class ExchangeCompactSectorListDTO implements DTO
{
    @NonNull public final ExchangeCompactDTOList exchanges;
    @NonNull public final SectorCompactDTOList sectors;

    //<editor-fold desc="Constructors">
    public ExchangeCompactSectorListDTO(@NonNull ExchangeCompactDTOList exchanges, @NonNull SectorCompactDTOList sectors)
    {
        this.exchanges = exchanges;
        this.sectors = sectors;
    }
    //</editor-fold>
}
