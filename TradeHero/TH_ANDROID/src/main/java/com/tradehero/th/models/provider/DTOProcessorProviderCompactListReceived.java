package com.tradehero.th.models.provider;

import com.tradehero.th.api.competition.ProviderCompactDTO;
import com.tradehero.th.api.competition.ProviderCompactDTOList;
import com.tradehero.th.models.DTOProcessor;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorProviderCompactListReceived
    extends DTOProcessorProviderCompactListReceivedBase<ProviderCompactDTO, ProviderCompactDTOList>
{
    public DTOProcessorProviderCompactListReceived(
            @NotNull DTOProcessor<ProviderCompactDTO> providerCompactProcessor)
    {
        super(providerCompactProcessor);
    }
}
