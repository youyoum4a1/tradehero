package com.androidth.general.api.translation;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.androidth.general.common.annotation.ForApp;
import com.androidth.general.api.translation.bing.BingTranslationToken;
import com.androidth.general.api.translation.bing.BingUserTranslationSettingDTO;
import java.io.IOException;
import javax.inject.Inject;
import timber.log.Timber;

public class UserTranslationSettingDTOFactory
{
    @NonNull private final ObjectMapper objectMapper;
    @NonNull private final TranslatableLanguageDTOFactoryFactory translatableLanguageDTOFactoryFactory;

    //<editor-fold desc="Constructors">
    @Inject public UserTranslationSettingDTOFactory(
            @NonNull TranslatableLanguageDTOFactoryFactory translatableLanguageDTOFactoryFactory,
            @NonNull @ForApp ObjectMapper objectMapper)
    {
        this.translatableLanguageDTOFactoryFactory = translatableLanguageDTOFactoryFactory;
        this.objectMapper = objectMapper;
    }
    //</editor-fold>

    @SuppressWarnings("DuplicateThrows")
    @NonNull public UserTranslationSettingDTO create(@NonNull String serialised)
            throws JsonParseException, JsonMappingException, IOException
    {
        return objectMapper.readValue(serialised, UserTranslationSettingDTO.class);
    }

    @NonNull public String serialise(@NonNull UserTranslationSettingDTO settingDTO)
            throws JsonProcessingException
    {
        return objectMapper.writeValueAsString(settingDTO);
    }

    @Nullable public UserTranslationSettingDTO createDefaultPerType(
            @NonNull Resources resources,
            @NonNull TranslationToken translationToken)
    {
        TranslatableLanguageDTOFactory translatableLanguageDTOFactory = translatableLanguageDTOFactoryFactory.create(translationToken);
        String bestTranslatableMatch = UserTranslationSettingDTO.DEFAULT_LANGUAGE_CODE;
        if (translatableLanguageDTOFactory != null)
        {
            bestTranslatableMatch = translatableLanguageDTOFactory.getBestMatch(
                    resources,
                    resources.getConfiguration().locale.getLanguage(),
                    bestTranslatableMatch)
                    .code;
        }
        if (translationToken instanceof BingTranslationToken)
        {
            return new BingUserTranslationSettingDTO(bestTranslatableMatch);
        }
        Timber.e(new IllegalArgumentException(), "Unhandled token type", translationToken.getClass());
        return null;
    }
}
