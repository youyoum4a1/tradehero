package com.tradehero.th.api.competition;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import javax.inject.Inject;

public class ProviderDTOJacksonModule extends SimpleModule
{
    @Inject public ProviderDTOJacksonModule(JsonDeserializer<ProviderDTO> providerDTODeserialiser)
    {
        super("SetSpecificsProviderDTODeserializerModule",
                new Version(1, 0, 0, null, null, null));
        addDeserializer(ProviderDTO.class, providerDTODeserialiser);
    }
}
