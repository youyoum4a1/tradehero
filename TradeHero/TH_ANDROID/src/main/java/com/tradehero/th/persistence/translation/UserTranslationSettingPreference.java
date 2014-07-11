package com.tradehero.th.persistence.translation;

import android.content.SharedPreferences;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.tradehero.common.persistence.prefs.StringSetPreference;
import com.tradehero.th.api.translation.TranslationToken;
import com.tradehero.th.api.translation.UserTranslationSettingDTO;
import com.tradehero.th.api.translation.UserTranslationSettingDTOFactory;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UserTranslationSettingPreference extends StringSetPreference
{
    @NotNull private final UserTranslationSettingDTOFactory userTranslationSettingDTOFactory;

    //<editor-fold desc="Constructors">
    public UserTranslationSettingPreference(
            @NotNull UserTranslationSettingDTOFactory userTranslationSettingDTOFactory,
            @NotNull SharedPreferences preference,
            @NotNull String key,
            @NotNull Set<String> defaultValue)
    {
        super(preference, key, defaultValue);
        this.userTranslationSettingDTOFactory = userTranslationSettingDTOFactory;
    }
    //</editor-fold>

    @SuppressWarnings("DuplicateThrows")
    @NotNull public HashSet<UserTranslationSettingDTO> getSettingDTOs()
            throws JsonParseException, JsonMappingException, IOException
    {
        HashSet<UserTranslationSettingDTO> set = new HashSet<>();
        @NotNull Set<String> savedSet = get();
        for (@NotNull String saved : savedSet)
        {
            set.add(userTranslationSettingDTOFactory.create(saved));
        }
        return set;
    }

    @SuppressWarnings("DuplicateThrows")
    @Nullable public UserTranslationSettingDTO getOfSameTypeOrDefault(@NotNull TranslationToken translationToken)
            throws JsonParseException, JsonMappingException, IOException
    {
        UserTranslationSettingDTO defaultOne = userTranslationSettingDTOFactory.createDefaultPerType(translationToken);
        if (defaultOne == null)
        {
            return null;
        }
        return getOfSameTypeOrDefault(defaultOne);
    }

    @SuppressWarnings("DuplicateThrows")
    @NotNull public UserTranslationSettingDTO getOfSameTypeOrDefault(@NotNull UserTranslationSettingDTO defaultIfNotFound)
            throws JsonParseException, JsonMappingException, IOException
    {
        UserTranslationSettingDTO found = defaultIfNotFound;
        for (@NotNull UserTranslationSettingDTO saved : getSettingDTOs())
        {
            if (saved.getClass().equals(defaultIfNotFound.getClass()))
            {
                found = saved;
            }
        }
        return found;
    }

    public void setSettingDTOs(@Nullable Set<UserTranslationSettingDTO> settingDTOs)
            throws JsonProcessingException
    {
        Set<String> savedStrings = null;
        if (settingDTOs != null)
        {
            savedStrings = new HashSet<>();
            for (@NotNull UserTranslationSettingDTO settingDTO : settingDTOs)
            {
                savedStrings.add(userTranslationSettingDTOFactory.serialise(settingDTO));
            }
        }
        set(savedStrings);
    }
}
