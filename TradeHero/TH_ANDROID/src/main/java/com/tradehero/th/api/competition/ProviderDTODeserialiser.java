package com.tradehero.th.api.competition;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.th.api.competition.specific.ProviderSpecificsPopulator;
import java.io.IOException;
import javax.inject.Inject;
import android.support.annotation.NonNull;

public class ProviderDTODeserialiser extends ProviderDTODeserialiserBase<ProviderDTO>
{
    //<editor-fold desc="Constructors">
    @Inject protected ProviderDTODeserialiser(
            @NonNull ObjectMapper objectMapper,
            @NonNull ProviderSpecificsPopulator providerSpecificsPopulator)
    {
        super(objectMapper, providerSpecificsPopulator);
    }
    //</editor-fold>

    @Override protected ProviderDTO justDeserialize(JsonParser jp, DeserializationContext ctxt) throws IOException
    {
        return innerMapper.readValue(jp, ProviderDTO.class);
    }
}
