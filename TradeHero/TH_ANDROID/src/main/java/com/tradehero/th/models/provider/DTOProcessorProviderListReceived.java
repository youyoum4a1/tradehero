package com.tradehero.th.models.provider;

import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderDTOList;
import com.tradehero.th.models.DTOProcessor;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorProviderListReceived
    extends DTOProcessorProviderCompactListReceivedBase<ProviderDTO, ProviderDTOList>
{
    public DTOProcessorProviderListReceived(
            @NotNull DTOProcessor<ProviderDTO> providerProcessor)
    {
        super(providerProcessor);
    }
}
