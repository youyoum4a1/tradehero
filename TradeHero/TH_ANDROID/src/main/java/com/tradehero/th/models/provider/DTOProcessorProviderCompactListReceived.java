package com.tradehero.th.models.provider;

import android.support.annotation.NonNull;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderDTOList;
import com.tradehero.th.models.DTOProcessor;

public class DTOProcessorProviderCompactListReceived
    extends DTOProcessorProviderCompactListReceivedBase<ProviderDTO, ProviderDTOList>
{
    public DTOProcessorProviderCompactListReceived(
            @NonNull DTOProcessor<ProviderDTO> providerCompactProcessor)
    {
        super(providerCompactProcessor);
    }
}
