package com.ayondo.academy.models.share.preference;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.persistence.prefs.StringSetPreference;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONException;
import timber.log.Timber;

public class SocialShareSetPreference extends StringSetPreference
{
    //<editor-fold desc="Constructors">
    public SocialShareSetPreference(
            @NonNull SharedPreferences preference,
            @NonNull String key,
            @NonNull Set<String> defaultValue)
    {
        super(preference, key, defaultValue);
    }
    //</editor-fold>

    @NonNull public Set<SocialSharePreferenceDTO> getSocialSharePreference()
    {
        Set<SocialSharePreferenceDTO> socialSharePreferenceDTOs = new HashSet<>();
        Set<String> stringSet = get();
        for (String jsonString : stringSet)
        {
            try
            {
                socialSharePreferenceDTOs.add(SocialSharePreferenceDTOFactory.create(jsonString));
            } catch (JSONException e)
            {
                Timber.e(e, "Parsing savedToken: %s", jsonString);
            }
        }
        return socialSharePreferenceDTOs;
    }

    public void setSocialSharePreference(@Nullable Set<SocialSharePreferenceDTO> socialSharePreferenceDTOSet)
    {
        Set<String> savedStrings = null;
        if (socialSharePreferenceDTOSet != null)
        {
            savedStrings = new HashSet<>();
            for (SocialSharePreferenceDTO socialSharePreferenceDTO : socialSharePreferenceDTOSet)
            {
                try
                {
                    savedStrings.add(socialSharePreferenceDTO.toJSONObject().toString());
                } catch (JSONException e)
                {
                    Timber.e(e, "Failed to save share preference %s", socialSharePreferenceDTO);
                }
            }
        }
        set(savedStrings);
    }
}
