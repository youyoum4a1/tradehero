package com.tradehero.th.models.provider;

import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderIdConstants;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by xavier on 1/29/14.
 */
@Singleton public class ProviderSpecificResourcesFactory
{
    public static final String TAG = ProviderSpecificResourcesFactory.class.getSimpleName();

    //<editor-fold desc="Constructors">
    @Inject public ProviderSpecificResourcesFactory()
    {
        super();
    }
    //</editor-fold>

    public ProviderSpecificResourcesDTO createResourcesDTO(ProviderDTO providerDTO)
    {
        ProviderSpecificResourcesDTO created = null;
        if (providerDTO != null)
        {
            created = createResourcesDTO(providerDTO.getProviderId());
        }
        return created;
    }

    public ProviderSpecificResourcesDTO createResourcesDTO(ProviderId providerId)
    {
        ProviderSpecificResourcesDTO created = null;
        if (providerId != null)
        {
            switch(providerId.key)
            {
                case ProviderIdConstants.PROVIDER_ID_MACQUARIE_WARRANTS:
                    created = getMacquarieWarrantHeroResources();
                    break;
            }
        }
        return created;
    }

    protected ProviderSpecificResourcesDTO getMacquarieWarrantHeroResources()
    {
        ProviderSpecificResourcesDTO resourcesDTO = new ProviderSpecificResourcesDTO();
        resourcesDTO.mainCompetitionFragmentTitleResId = R.string.competition_macquarie_warrant_hero_main_title;
        resourcesDTO.helpVideoListFragmentTitleResId = R.string.competition_macquarie_warrant_hero_help_video_title;
        resourcesDTO.helpVideoLinkBackgroundResId = R.drawable.btn_help_video_macquarie;
        resourcesDTO.timedHeaderLeaderboardTitleResId = R.string.competition_macquarie_warrant_hero_main_title;
        resourcesDTO.competitionPortfolioTitleResId = R.string.competition_macquarie_warrant_hero_main_title;
        resourcesDTO.securityListFragmentTitleResId = R.string.competition_macquarie_warrant_hero_security_list_title;

        return resourcesDTO;
    }
}
