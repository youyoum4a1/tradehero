package com.androidth.general.models.share.preference;

import android.support.annotation.NonNull;
import com.androidth.general.api.social.SocialNetworkEnum;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.inject.Inject;

public class SocialSharePreferenceHelper
{
    @NonNull private final SocialShareSetPreference socialShareSetPreference;
    @NonNull private static Hashtable<SocialNetworkEnum, SocialSharePreferenceDTO> sharePreferencesMap = null;

    private static AtomicBoolean isInitialized = new AtomicBoolean();

    //<editor-fold desc="Constructors">
    @Inject public SocialSharePreferenceHelper(
            @NonNull SocialShareSetPreference socialShareSetPreference)
    {
        super();
        this.socialShareSetPreference = socialShareSetPreference;
        if (!isInitialized.getAndSet(true))
        {
            sharePreferencesMap = new Hashtable<>();
        }
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
            sharePreferencesMap.put(socialNetworkEnum, SocialSharePreferenceDTOFactory.create(socialNetworkEnum, defaultValue));
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
            sharePreferencesMap.put(networkEnum, SocialSharePreferenceDTOFactory.create(networkEnum, isShareEnabled));
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
