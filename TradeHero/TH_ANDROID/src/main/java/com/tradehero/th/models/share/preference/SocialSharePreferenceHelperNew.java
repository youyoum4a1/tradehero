package com.tradehero.th.models.share.preference;

import com.tradehero.th.api.social.SocialNetworkEnum;
import java.util.HashMap;
import java.util.HashSet;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SocialSharePreferenceHelperNew
{
    @NotNull private final SocialShareSetPreference socialShareSetPreference;
    @NotNull private final SocialSharePreferenceDTOFactory socialSharePreferenceFactory;
    @NotNull private HashMap<SocialNetworkEnum, SocialSharePreferenceDTO> sharePreferencesMap;

    //<editor-fold desc="Constructors">
    @Inject public SocialSharePreferenceHelperNew(
            @NotNull SocialShareSetPreference socialShareSetPreference,
            @NotNull SocialSharePreferenceDTOFactory socialSharePreferenceDTOFactory)
    {
        super();
        this.socialSharePreferenceFactory = socialSharePreferenceDTOFactory;
        this.socialShareSetPreference = socialShareSetPreference;
        sharePreferencesMap = new HashMap<>();
        load();
    }
    //</editor-fold>

    public void load()
    {
        sharePreferencesMap.clear();

        for (@NotNull SocialSharePreferenceDTO socialSharePreferenceDTO : socialShareSetPreference.getSocialSharePreference())
        {
            sharePreferencesMap.put(socialSharePreferenceDTO.getSocialNetworkEnum(), socialSharePreferenceDTO);
        }
    }

    public boolean isShareEnabled(@NotNull SocialNetworkEnum socialNetworkEnum, boolean defaultValue)
    {
        @Nullable SocialSharePreferenceDTO socialSharePreferenceDTO = sharePreferencesMap.get(socialNetworkEnum);
        if (socialSharePreferenceDTO != null)
        {
            return socialSharePreferenceDTO.isShareEnabled();
        }
        return defaultValue;
    }

    public void updateSocialSharePreference(@NotNull SocialNetworkEnum networkEnum, boolean isShareEnabled)
    {
        @Nullable SocialSharePreferenceDTO socialSharePreferenceDTO = sharePreferencesMap.get(networkEnum);

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

    public void save()
    {
        //Save to preference
        socialShareSetPreference.setSocialSharePreference(new HashSet<>(sharePreferencesMap.values()));
    }
}
