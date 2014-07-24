package com.tradehero.th.models.provider;

import com.tradehero.th.api.competition.ProviderCompactDTO;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.models.DTOProcessor;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorProviderReceived implements DTOProcessor<ProviderDTO>
{
    @NotNull private final DTOProcessor<ProviderCompactDTO> providerCompactProcessor;

    //<editor-fold desc="Constructors">
    public DTOProcessorProviderReceived(
            @NotNull DTOProcessor<ProviderCompactDTO> providerCompactProcessor)
    {
        this.providerCompactProcessor = providerCompactProcessor;
    }
    //</editor-fold>

    @Override public ProviderDTO process(ProviderDTO value)
    {
        providerCompactProcessor.process(value);
        return value;
    }
}
