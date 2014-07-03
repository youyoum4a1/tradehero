package com.tradehero.th.models.provider;

import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderDTOList;
import com.tradehero.th.models.DTOProcessor;
import java.util.ListIterator;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorReceivedProviderDTOList implements DTOProcessor<ProviderDTOList>
{
    @NotNull private final DTOProcessor<ProviderDTO> providerProcessor;

    //<editor-fold desc="Constructors">
    public DTOProcessorReceivedProviderDTOList(
            @NotNull ProviderSpecificKnowledgeFactory providerSpecificKnowledgeFactory,
            @NotNull ProviderSpecificResourcesFactory providerSpecificResourcesFactory)
    {
        super();
        this.providerProcessor = new DTOProcessorReceivedProviderDTO(
                providerSpecificKnowledgeFactory,
                providerSpecificResourcesFactory);
    }
    //</editor-fold>

    @Override public ProviderDTOList process(ProviderDTOList value)
    {
        if (value != null)
        {
            ListIterator<ProviderDTO> iterator = value.listIterator();
            while (iterator.hasNext())
            {
                providerProcessor.process(iterator.next());
            }
        }
        return value;
    }
}
