package com.tradehero.th.models.provider;

import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderDTOList;
import com.tradehero.th.models.DTOProcessor;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorProviderListReceived implements DTOProcessor<ProviderDTOList>
{
    @NotNull private final DTOProcessor<ProviderDTO> providerProcessor;

    //<editor-fold desc="Constructors">
    public DTOProcessorProviderListReceived(
            @NotNull DTOProcessor<ProviderDTO> providerProcessor)
    {
        this.providerProcessor = providerProcessor;
    }
    //</editor-fold>

    @Override public ProviderDTOList process(ProviderDTOList value)
    {
        if (value != null)
        {
            for (ProviderDTO providerDTO : value)
            {
                providerProcessor.process(providerDTO);
            }
        }
        return value;
    }
}
