package com.tradehero.th.fragments.onboarding.sector;

import android.support.annotation.NonNull;
import com.tradehero.common.api.SelectableDTO;
import com.tradehero.th.api.market.SectorDTO;

public class SelectableSectorDTO extends SelectableDTO<SectorDTO>
{
    //<editor-fold desc="Constructors">
    SelectableSectorDTO(@NonNull SectorDTO sectorDTO, boolean selected)
    {
        super(sectorDTO, selected);
    }
    //</editor-fold>
}
