package com.tradehero.th.fragments.competition.zone.dto;

import android.support.annotation.NonNull;
import com.tradehero.th.api.competition.ProviderPrizePoolDTO;

public class CompetitionZonePrizePoolDTO extends CompetitionZoneDTO
{
    @NonNull public final ProviderPrizePoolDTO providerPrizePoolDTO;

    //<editor-fold desc="Constructors">
    public CompetitionZonePrizePoolDTO(@NonNull ProviderPrizePoolDTO providerPrizePoolDTO)
    {
        super(null, null);
        this.providerPrizePoolDTO = providerPrizePoolDTO;
    }
    //</editor-fold>

}
