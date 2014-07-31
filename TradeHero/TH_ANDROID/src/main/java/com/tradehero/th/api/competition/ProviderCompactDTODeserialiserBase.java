package com.tradehero.th.api.competition;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.tradehero.th.api.competition.specific.ProviderSpecificsPopulator;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

abstract public class ProviderCompactDTODeserialiserBase<ProviderCompactDTOType extends ProviderCompactDTO>
        extends StdDeserializer<ProviderCompactDTOType>
{
    @NotNull protected final ObjectMapper innerMapper;
    @NotNull protected final ProviderSpecificsPopulator providerSpecificsPopulator;

    //<editor-fold desc="Constructors">
    protected ProviderCompactDTODeserialiserBase(
            @NotNull ObjectMapper objectMapper,
            @NotNull ProviderSpecificsPopulator providerSpecificsPopulator)
    {
        super(ProviderDTO.class);
        this.innerMapper = objectMapper;
        this.innerMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.providerSpecificsPopulator = providerSpecificsPopulator;
    }
    //</editor-fold>

    @Override public ProviderCompactDTOType deserialize(
            JsonParser jp,
            DeserializationContext ctxt) throws IOException
    {
        ProviderCompactDTOType providerDTO = justDeserialize(jp, ctxt);
        providerSpecificsPopulator.populate(providerDTO);
        return providerDTO;
    }

    abstract protected ProviderCompactDTOType justDeserialize(
            JsonParser jp,
            DeserializationContext ctxt) throws IOException;
}
