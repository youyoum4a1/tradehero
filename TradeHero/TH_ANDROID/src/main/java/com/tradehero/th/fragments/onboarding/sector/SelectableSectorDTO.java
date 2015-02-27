package com.tradehero.th.fragments.onboarding.sector;

import android.support.annotation.NonNull;
import com.tradehero.common.api.SelectableDTO;
import com.tradehero.th.api.market.SectorCompactDTO;

public class SelectableSectorDTO extends SelectableDTO<SectorCompactDTO>
{
    //<editor-fold desc="Constructors">
    public SelectableSectorDTO(@NonNull SectorCompactDTO sectorDTO, boolean selected)
    {
        super(sectorDTO, selected);
    }
    //</editor-fold>
}
