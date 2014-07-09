package com.tradehero.th.models.share.preference;

import com.tradehero.th.api.social.SocialNetworkEnum;
import java.util.HashMap;
import java.util.HashSet;
import javax.inject.Inject;

public class SocialSharePreferenceHelperNew
{
    private final SocialShareSetPreference socialShareSetPreference;
    private final SocialSharePreferenceDTOFactory socialSharePreferenceFactory;
    private HashMap<SocialNetworkEnum, SocialSharePreferenceDTO> hashSharePreferencesSet;

    @Inject public SocialSharePreferenceHelperNew(SocialShareSetPreference socialShareSetPreference,
            SocialSharePreferenceDTOFactory socialSharePreferenceDTOFactory)
    {
        super();
        this.socialSharePreferenceFactory = socialSharePreferenceDTOFactory;
        this.socialShareSetPreference = socialShareSetPreference;
        hashSharePreferencesSet = new HashMap<>();
        load();
    }

    public void load()
    {
        hashSharePreferencesSet.clear();

        for (SocialSharePreferenceDTO socialSharePreferenceDTO : socialShareSetPreference.getSocialSharePreference())
        {
            hashSharePreferencesSet.put(socialSharePreferenceDTO.getSocialNetworkEnum(), socialSharePreferenceDTO);
        }
    }

    public boolean isShareEnabled(SocialNetworkEnum socialNetworkEnum, boolean defaultValue)
    {
        SocialSharePreferenceDTO socialSharePreferenceDTO = hashSharePreferencesSet.get(socialNetworkEnum);
        if (socialSharePreferenceDTO != null)
        {
            return socialSharePreferenceDTO.isShareEnabled();
        }
        return defaultValue;
    }

    public void updateSocialSharePreference(SocialNetworkEnum networkEnum, boolean isShareEnabled)
    {
        SocialSharePreferenceDTO socialSharePreferenceDTO = hashSharePreferencesSet.get(networkEnum);

        if (socialSharePreferenceDTO != null)
        {
            socialSharePreferenceDTO.setIsShareEnabled(isShareEnabled);
        }
        else
        {
            //Create and add
            hashSharePreferencesSet.put(networkEnum, socialSharePreferenceFactory.create(networkEnum, isShareEnabled));
        }
    }

    public void save()
    {
        //Save to preference
        socialShareSetPreference.setSocialSharePreference(new HashSet<>(hashSharePreferencesSet.values()));
    }
}
