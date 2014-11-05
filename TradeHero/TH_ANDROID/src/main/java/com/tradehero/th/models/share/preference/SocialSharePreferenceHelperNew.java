package com.tradehero.th.models.share.preference;

import com.tradehero.th.api.social.SocialNetworkEnum;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class SocialSharePreferenceHelperNew
{
    @NonNull private final SocialShareSetPreference socialShareSetPreference;
    @NonNull private final SocialSharePreferenceDTOFactory socialSharePreferenceFactory;
    @NonNull private HashMap<SocialNetworkEnum, SocialSharePreferenceDTO> sharePreferencesMap;

    //<editor-fold desc="Constructors">
    @Inject public SocialSharePreferenceHelperNew(
            @NonNull SocialShareSetPreference socialShareSetPreference,
            @NonNull SocialSharePreferenceDTOFactory socialSharePreferenceDTOFactory)
    {
        super();
        this.socialSharePreferenceFactory = socialSharePreferenceDTOFactory;
        this.socialShareSetPreference = socialShareSetPreference;
        sharePreferencesMap = new HashMap<>();
        load();
    }
    //</editor-fold>

    public void reload()
    {
        sharePreferencesMap.clear();
        load();
    }

    public void load()
    {
        for (SocialSharePreferenceDTO socialSharePreferenceDTO : socialShareSetPreference.getSocialSharePreference())
        {
            sharePreferencesMap.put(socialSharePreferenceDTO.getSocialNetworkEnum(), socialSharePreferenceDTO);
        }
    }

    public boolean isShareEnabled(@NonNull SocialNetworkEnum socialNetworkEnum, boolean defaultValue)
    {
        SocialSharePreferenceDTO socialSharePreferenceDTO = sharePreferencesMap.get(socialNetworkEnum);
        if (socialSharePreferenceDTO != null)
        {
            return socialSharePreferenceDTO.isShareEnabled();
        }
        else
        {
            sharePreferencesMap.put(socialNetworkEnum, socialSharePreferenceFactory.create(socialNetworkEnum, defaultValue));
            return defaultValue;
        }
    }

    public void updateSocialSharePreference(@NonNull SocialNetworkEnum networkEnum, boolean isShareEnabled)
    {
        SocialSharePreferenceDTO socialSharePreferenceDTO = sharePreferencesMap.get(networkEnum);

        if (socialSharePreferenceDTO != null)
        {
            socialSharePreferenceDTO.setIsShareEnabled(isShareEnabled);
        }
        else
        {
            //Create and add
            sharePreferencesMap.put(networkEnum, socialSharePreferenceFactory.create(networkEnum, isShareEnabled));
        }
    }

    @NonNull public List<SocialNetworkEnum> getAllEnabledSharePreferences()
    {
        List<SocialNetworkEnum> enabled = new ArrayList<>();
        for (Map.Entry<SocialNetworkEnum, SocialSharePreferenceDTO> entry : sharePreferencesMap.entrySet())
        {
            SocialNetworkEnum networkEnum = entry.getKey();
            SocialSharePreferenceDTO socialSharePreferenceDTO = entry.getValue();
            if (socialSharePreferenceDTO != null && socialSharePreferenceDTO.isShareEnabled())
            {
                enabled.add(networkEnum);
            }
        }
        return enabled;
    }

    public void save()
    {
        //Save to preference
        socialShareSetPreference.setSocialSharePreference(new HashSet<>(sharePreferencesMap.values()));
    }
}
