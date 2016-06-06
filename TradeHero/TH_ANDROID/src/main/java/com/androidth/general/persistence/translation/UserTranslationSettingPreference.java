package com.androidth.general.persistence.translation;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.androidth.general.common.persistence.prefs.StringSetPreference;
import com.androidth.general.api.translation.TranslationToken;
import com.androidth.general.api.translation.UserTranslationSettingDTO;
import com.androidth.general.api.translation.UserTranslationSettingDTOFactory;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class UserTranslationSettingPreference extends StringSetPreference
{
    @NonNull private final Resources resources;
    @NonNull private final UserTranslationSettingDTOFactory userTranslationSettingDTOFactory;

    //<editor-fold desc="Constructors">
    public UserTranslationSettingPreference(
            @NonNull Resources resources,
            @NonNull UserTranslationSettingDTOFactory userTranslationSettingDTOFactory,
            @NonNull SharedPreferences preference,
            @NonNull String key,
            @NonNull Set<String> defaultValue)
    {
        super(preference, key, defaultValue);
        this.resources = resources;
        this.userTranslationSettingDTOFactory = userTranslationSettingDTOFactory;
    }
    //</editor-fold>

    @SuppressWarnings("DuplicateThrows")
    @NonNull public HashSet<UserTranslationSettingDTO> getSettingDTOs()
            throws JsonParseException, JsonMappingException, IOException
    {
        HashSet<UserTranslationSettingDTO> set = new HashSet<>();
        Set<String> savedSet = get();
        for (String saved : savedSet)
        {
            set.add(userTranslationSettingDTOFactory.create(saved));
        }
        return set;
    }

    @SuppressWarnings("DuplicateThrows")
    @Nullable public UserTranslationSettingDTO getOfSameTypeOrDefault(@NonNull TranslationToken translationToken)
            throws JsonParseException, JsonMappingException, IOException
    {
        UserTranslationSettingDTO defaultOne = userTranslationSettingDTOFactory.createDefaultPerType(resources, translationToken);
        if (defaultOne == null)
        {
            return null;
        }
        return getOfSameTypeOrDefault(defaultOne);
    }

    @SuppressWarnings("DuplicateThrows")
    @NonNull public UserTranslationSettingDTO getOfSameTypeOrDefault(@NonNull UserTranslationSettingDTO defaultIfNotFound)
            throws JsonParseException, JsonMappingException, IOException
    {
        UserTranslationSettingDTO found = defaultIfNotFound;
        for (UserTranslationSettingDTO saved : getSettingDTOs())
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
            for (UserTranslationSettingDTO settingDTO : settingDTOs)
            {
                savedStrings.add(userTranslationSettingDTOFactory.serialise(settingDTO));
            }
        }
        set(savedStrings);
    }

    public void addOrReplaceSettingDTO(@NonNull UserTranslationSettingDTO settingDTO)
            throws JsonProcessingException
    {
        //noinspection ConstantConditions
        if (settingDTO == null)
        {
            // We want a crash here, including during tests.
            throw new NullPointerException();
        }
        HashSet<UserTranslationSettingDTO> existing = new HashSet<>();
        try
        {
            // Here we catch in order to overwrite if reading failed.
            existing = getSettingDTOs();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        existing.remove(settingDTO);
        existing.add(settingDTO);
        setSettingDTOs(existing);
    }
}
