package com.tradehero.th.api.position;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import javax.inject.Inject;

public class PositionDTOJacksonModule extends SimpleModule
{
    @Inject public PositionDTOJacksonModule(JsonDeserializer<PositionDTO> positionDTODeserializer)
    {
        super("PolymorphicPositionDTODeserializerModule",
                new Version(1, 0, 0, null, null, null));
        addDeserializer(PositionDTO.class, positionDTODeserializer);
    }
}
