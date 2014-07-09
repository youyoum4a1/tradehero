package com.tradehero.th.models.share.preference;

import android.content.SharedPreferences;
import com.tradehero.common.persistence.prefs.StringSetPreference;
import com.tradehero.th.api.social.SocialNetworkEnum;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import timber.log.Timber;

public class SocialShareSetPreference extends StringSetPreference
{

    @NotNull private final SocialSharePreferenceDTOFactory socialSharePreferenceDTOFactory;

    public SocialShareSetPreference(
            @NotNull SocialSharePreferenceDTOFactory socialSharePreferenceDTOFactory,
            @NotNull SharedPreferences preference,
            @NotNull String key,
            @NotNull Set<String> defaultValue)
    {
        super(preference, key, defaultValue);
        this.socialSharePreferenceDTOFactory = socialSharePreferenceDTOFactory;
    }

    public Set<SocialSharePreferenceDTO> getSocialSharePreference()
    {
        Set<SocialSharePreferenceDTO> socialSharePreferenceDTOs = new HashSet<>();
        Set<String> stringSet = get();
        for (@NotNull String jsonString : stringSet)
        {
            try
            {
                socialSharePreferenceDTOs.add(socialSharePreferenceDTOFactory.create(jsonString));
            } catch (JSONException e)
            {
                Timber.e(e, "Parsing savedToken: %s", jsonString);
            }
        }
        return socialSharePreferenceDTOs;
    }

    public void setSocialSharePreference(Set<SocialSharePreferenceDTO> socialSharePreferenceDTOSet)
    {
        Set<String> savedStrings = null;
        if (socialSharePreferenceDTOSet != null)
        {
            savedStrings = new HashSet<>();
            for (@NotNull SocialSharePreferenceDTO socialSharePreferenceDTO : socialSharePreferenceDTOSet)
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
