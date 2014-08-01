package com.tradehero.th.api.competition;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import javax.inject.Inject;

public class ProviderCompactDTOJacksonModule extends SimpleModule
{
    @Inject public ProviderCompactDTOJacksonModule(JsonDeserializer<ProviderCompactDTO> providerCompactDTODeserialiser)
    {
        super("SetSpecificsProviderCompactDTODeserializerModule",
                new Version(1, 0, 0, null, null, null));
        addDeserializer(ProviderCompactDTO.class, providerCompactDTODeserialiser);
    }
}
