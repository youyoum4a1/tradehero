package com.tradehero.th.api.position;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.api.competition.ProviderDTOList;
import com.tradehero.th.api.security.SecurityCompactDTO;

public class SecurityPositionDetailDTO extends SecurityPositionDTO
{
    @Nullable public ProviderDTOList providers;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration") // Necessary for deserialisation
    SecurityPositionDetailDTO()
    {
    }

    public SecurityPositionDetailDTO(
            @NonNull SecurityCompactDTO security,
            @Nullable PositionDTOCompactList positions,
            int firstTradeAllTime,
            @Nullable ProviderDTOList providers)
    {
        super(security, positions, firstTradeAllTime);
        this.providers = providers;
    }
    //</editor-fold>
}
