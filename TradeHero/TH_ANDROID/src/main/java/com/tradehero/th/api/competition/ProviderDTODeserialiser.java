package com.tradehero.th.api.competition;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.tradehero.th.api.competition.specific.ProviderSpecificsPopulator;
import java.io.IOException;
import javax.inject.Inject;

public class ProviderDTODeserialiser extends StdDeserializer<ProviderDTO>
{
    @NonNull protected final ObjectMapper innerMapper;

    //<editor-fold desc="Constructors">
    @Inject protected ProviderDTODeserialiser(@NonNull ObjectMapper objectMapper)
    {
        super(ProviderDTO.class);
        this.innerMapper = objectMapper;
    }
    //</editor-fold>

    @Override public ProviderDTO deserialize(
            JsonParser jp,
            DeserializationContext ctxt) throws IOException
    {
        ProviderDTO providerDTO = innerMapper.readValue(jp, ProviderDTO.class);
        ProviderSpecificsPopulator.populate(providerDTO);
        return providerDTO;
    }
}
