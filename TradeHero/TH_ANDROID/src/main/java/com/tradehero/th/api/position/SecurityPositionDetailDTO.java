package com.tradehero.th.api.position;

import android.support.annotation.Nullable;
import com.tradehero.th.api.competition.ProviderDTOList;

@Deprecated
public class SecurityPositionDetailDTO extends SecurityPositionDTO
{
    @Nullable public ProviderDTOList providers;

    //<editor-fold desc="Constructors">
    public SecurityPositionDetailDTO()
    {
    }
    //</editor-fold>
}
