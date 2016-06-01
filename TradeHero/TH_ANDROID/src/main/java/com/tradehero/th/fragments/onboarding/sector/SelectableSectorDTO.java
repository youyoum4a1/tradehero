package com.ayondo.academy.fragments.onboarding.sector;

import android.support.annotation.NonNull;
import com.tradehero.common.api.SelectableDTO;
import com.ayondo.academy.api.market.SectorDTO;

public class SelectableSectorDTO extends SelectableDTO<SectorDTO>
{
    //<editor-fold desc="Constructors">
    public SelectableSectorDTO(@NonNull SectorDTO sectorDTO, boolean selected)
    {
        super(sectorDTO, selected);
    }
    //</editor-fold>
}
