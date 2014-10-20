package com.tradehero.th.api.position;

import android.support.annotation.Nullable;
import com.tradehero.th.api.competition.ProviderDTOList;
import com.tradehero.th.api.security.SecurityCompactDTO;
import org.jetbrains.annotations.NotNull;

public class SecurityPositionDetailDTO extends SecurityPositionDTO
{
    @NotNull public ProviderDTOList providers;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration") // Necessary for deserialisation
    SecurityPositionDetailDTO()
    {
    }

    public SecurityPositionDetailDTO(
            @NotNull SecurityCompactDTO security,
            @Nullable PositionDTOCompactList positions,
            int firstTradeAllTime,
            @NotNull ProviderDTOList providers)
    {
        super(security, positions, firstTradeAllTime);
        this.providers = providers;
    }
    //</editor-fold>
}
