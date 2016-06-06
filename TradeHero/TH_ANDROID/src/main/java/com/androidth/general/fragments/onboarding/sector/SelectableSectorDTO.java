package com.androidth.general.fragments.onboarding.sector;

import android.support.annotation.NonNull;
import com.androidth.general.common.api.SelectableDTO;
import com.androidth.general.api.market.SectorDTO;

public class SelectableSectorDTO extends SelectableDTO<SectorDTO>
{
    //<editor-fold desc="Constructors">
    public SelectableSectorDTO(@NonNull SectorDTO sectorDTO, boolean selected)
    {
        super(sectorDTO, selected);
    }
    //</editor-fold>
}
