package com.tradehero.th.api.competition;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.tradehero.th.api.competition.specific.ProviderSpecificsPopulator;
import java.io.IOException;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class ProviderDTODeserialiser extends StdDeserializer<ProviderDTO>
{
    @NotNull private final ObjectMapper innerMapper;
    @NotNull protected final ProviderSpecificsPopulator providerSpecificsPopulator;

    //<editor-fold desc="Constructors">
    @Inject protected ProviderDTODeserialiser(
            @NotNull ProviderSpecificsPopulator providerSpecificsPopulator)
    {
        super(ProviderDTO.class);
        this.innerMapper = new ObjectMapper();
        this.providerSpecificsPopulator = providerSpecificsPopulator;
    }
    //</editor-fold>

    @Override public ProviderDTO deserialize(
            JsonParser jp,
            DeserializationContext ctxt) throws IOException
    {
        ProviderDTO providerDTO = innerMapper.readValue(jp, ProviderDTO.class);
        providerSpecificsPopulator.populate(providerDTO);
        return providerDTO;
    }
}
