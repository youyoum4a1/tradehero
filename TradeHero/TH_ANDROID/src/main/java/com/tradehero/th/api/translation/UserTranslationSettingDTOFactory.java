package com.tradehero.th.api.translation;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class UserTranslationSettingDTOFactory
{
    @NotNull private final ObjectMapper objectMapper;

    //<editor-fold desc="Constructors">
    @Inject public UserTranslationSettingDTOFactory(
            @NotNull ObjectMapper objectMapper)
    {
        this.objectMapper = objectMapper;
    }
    //</editor-fold>

    @NotNull public UserTranslationSettingDTO create(@NotNull String serialised)
            throws JsonParseException, JsonMappingException, IOException
    {
        return objectMapper.readValue(serialised, UserTranslationSettingDTO.class);
    }

    @NotNull public String serialise(@NotNull UserTranslationSettingDTO settingDTO)
            throws JsonProcessingException
    {
        return objectMapper.writeValueAsString(settingDTO);
    }
}
