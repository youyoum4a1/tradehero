package com.tradehero.th.fragments.competition.zone.dto;

import com.tradehero.th.api.competition.ProviderPrizePoolDTO;

public class CompetitionZonePrizePoolDTO extends CompetitionZoneDTO
{
    public final ProviderPrizePoolDTO providerPrizePoolDTO;

    //<editor-fold desc="Constructors">
    public CompetitionZonePrizePoolDTO(ProviderPrizePoolDTO providerPrizePoolDTO)
    {
        super(null, null);
        this.providerPrizePoolDTO = providerPrizePoolDTO;
    }
    //</editor-fold>

}
