package com.tradehero.th.fragments.onboarding.sector;

import android.support.annotation.NonNull;
import com.tradehero.th.api.market.SectorDTO;

class OnBoardSectorDTO
{
    boolean selected;
    @NonNull final SectorDTO sector;

    OnBoardSectorDTO(
            boolean selected,
            @NonNull SectorDTO sectorDTO)
    {
        this.selected = selected;
        this.sector = sectorDTO;
    }
    //</editor-fold>
}
