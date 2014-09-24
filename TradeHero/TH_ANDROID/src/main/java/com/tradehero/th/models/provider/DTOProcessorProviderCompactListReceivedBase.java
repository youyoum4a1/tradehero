package com.tradehero.th.models.provider;

import com.tradehero.th.api.competition.BaseProviderDTOList;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.models.DTOProcessor;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorProviderCompactListReceivedBase<
        ProviderCompactDTOType extends ProviderDTO,
        ProviderCompactDTOListType extends BaseProviderDTOList<? extends ProviderCompactDTOType>>
        implements DTOProcessor<ProviderCompactDTOListType>
{
    @NotNull private final DTOProcessor<ProviderCompactDTOType> providerCompactProcessor;

    //<editor-fold desc="Constructors">
    public DTOProcessorProviderCompactListReceivedBase(
            @NotNull DTOProcessor<ProviderCompactDTOType> providerCompactProcessor)
    {
        this.providerCompactProcessor = providerCompactProcessor;
    }
    //</editor-fold>

    @Override public ProviderCompactDTOListType process(ProviderCompactDTOListType value)
    {
        if (value != null)
        {
            for (ProviderCompactDTOType providerCompactDTO : value)
            {
                providerCompactProcessor.process(providerCompactDTO);
            }
        }
        return value;
    }
}
