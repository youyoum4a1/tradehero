package com.tradehero.th.api.competition;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.tradehero.th.api.competition.specific.ProviderSpecificsPopulator;
import java.io.IOException;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class ProviderCompactDTODeserialiser extends ProviderCompactDTODeserialiserBase<ProviderCompactDTO>
{
    //<editor-fold desc="Constructors">
    @Inject protected ProviderCompactDTODeserialiser(
            @NotNull ProviderSpecificsPopulator providerSpecificsPopulator)
    {
        super(providerSpecificsPopulator);
    }
    //</editor-fold>

    @Override protected ProviderCompactDTO justDeserialize(JsonParser jp, DeserializationContext ctxt) throws IOException
    {
        return innerMapper.readValue(jp, ProviderCompactDTO.class);
    }
}
