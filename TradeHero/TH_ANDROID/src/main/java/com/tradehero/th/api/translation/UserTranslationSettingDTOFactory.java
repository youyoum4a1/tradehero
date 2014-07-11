package com.tradehero.th.api.translation;

import android.content.Context;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.th.api.translation.bing.BingTranslationToken;
import com.tradehero.th.api.translation.bing.BingUserTranslationSettingDTO;
import java.io.IOException;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class UserTranslationSettingDTOFactory
{
    @NotNull private final Context applicationContext;
    @NotNull private final ObjectMapper objectMapper;

    //<editor-fold desc="Constructors">
    @Inject public UserTranslationSettingDTOFactory(
            @NotNull Context applicationContext,
            @NotNull ObjectMapper objectMapper)
    {
        this.applicationContext = applicationContext;
        this.objectMapper = objectMapper;
    }
    //</editor-fold>

    @SuppressWarnings("DuplicateThrows")
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

    @Nullable public UserTranslationSettingDTO createDefaultPerType(@NotNull TranslationToken translationToken)
    {
        if (translationToken instanceof BingTranslationToken)
        {
            return new BingUserTranslationSettingDTO(applicationContext.getResources().getConfiguration().locale.getLanguage());
        }
        Timber.e(new IllegalArgumentException(), "Unhandled token type", translationToken.getClass());
        return null;
    }
}
