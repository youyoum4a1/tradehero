package com.tradehero.th.fragments.competition.zone.dto;

import android.content.Context;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.AdDTO;
import com.tradehero.th.api.competition.CompetitionDTO;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.fragments.competition.CompetitionZoneListItemAdapter;
import com.tradehero.th.models.provider.ProviderSpecificResourcesDTO;
import com.tradehero.th.models.provider.ProviderSpecificResourcesFactory;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

@Singleton public class CompetitionZoneDTOUtil
{
    @Inject ProviderSpecificResourcesFactory providerSpecificResourcesFactory;

    @Inject public CompetitionZoneDTOUtil()
    {
    }

    public void populateLists(Context context,
            UserProfileCompactDTO portfolioUserProfileCompact,
            ProviderDTO providerDTO,
            List<CompetitionDTO> competitionDTOs,
            List<Integer> preparedOrderedTypes,
            List<Object> preparedOrderedItems)
    {
        if (providerDTO != null)
        {
            preparedOrderedTypes.add(CompetitionZoneListItemAdapter.ITEM_TYPE_TRADE_NOW);

            ProviderSpecificResourcesDTO providerSpecificResourcesDTO = providerSpecificResourcesFactory.createResourcesDTO(providerDTO);
            if (providerSpecificResourcesDTO != null && providerSpecificResourcesDTO.tradeNowBtnImageResId > 0)
            {
                preparedOrderedItems.add(new CompetitionZoneTradeNowDTO(null, null, providerSpecificResourcesDTO.tradeNowBtnImageResId, providerDTO.tradeButtonImageUrl));
            }
            else
            {
                preparedOrderedItems.add(new CompetitionZoneTradeNowDTO(null, null, 0, providerDTO.tradeButtonImageUrl));
            }

            preparedOrderedTypes.add(CompetitionZoneListItemAdapter.ITEM_TYPE_HEADER);
            preparedOrderedItems.add(new CompetitionZoneDTO(providerDTO.ruleText, null));

            preparedOrderedTypes.add(CompetitionZoneListItemAdapter.ITEM_TYPE_ADS);
            if (providerDTO.hasAdvertisement())
            {
                int randomAds = (int) (Math.random() * providerDTO.advertisements.size());
                AdDTO pickedAdDTO = providerDTO.advertisements.get(randomAds);
                preparedOrderedItems.add(new CompetitionZoneAdvertisementDTO(null, null, 0, pickedAdDTO));
            }

            if (providerDTO.associatedPortfolio != null)
            {
                preparedOrderedTypes.add(CompetitionZoneListItemAdapter.ITEM_TYPE_PORTFOLIO);
                preparedOrderedItems.add(new CompetitionZonePortfolioDTO(
                        context.getString(R.string.provider_competition_portfolio_title),
                        context.getString(R.string.provider_competition_portfolio_description),
                        portfolioUserProfileCompact));
            }

            if (providerDTO.hasHelpVideo)
            {
                preparedOrderedTypes.add(CompetitionZoneListItemAdapter.ITEM_TYPE_ZONE_ITEM);
                preparedOrderedItems.add(new CompetitionZoneVideoDTO(providerDTO.helpVideoText, null));
            }

            if (providerDTO.hasWizard())
            {
                preparedOrderedTypes.add(CompetitionZoneListItemAdapter.ITEM_TYPE_ZONE_ITEM);

                String wizardTitle = providerDTO.wizardTitle != null && !providerDTO.wizardTitle.isEmpty() ?
                        providerDTO.wizardTitle : context.getString(R.string.provider_competition_wizard_title);
                preparedOrderedItems.add(new CompetitionZoneWizardDTO(
                        wizardTitle, null, providerDTO.wizardImageUrl, providerDTO.wizardUrl));
            }

            preparedOrderedTypes.add(CompetitionZoneListItemAdapter.ITEM_TYPE_HEADER);
            preparedOrderedItems.add(new CompetitionZoneDTO(null, null));

            if (competitionDTOs != null)
            {
                for (CompetitionDTO competitionDTO: competitionDTOs)
                {
                    if (competitionDTO != null)
                    {
                        preparedOrderedTypes.add(CompetitionZoneListItemAdapter.ITEM_TYPE_LEADERBOARD);
                        preparedOrderedItems.add(new CompetitionZoneLeaderboardDTO(
                                competitionDTO.name,
                                competitionDTO.leaderboard != null ? competitionDTO.leaderboard.desc : "",
                                competitionDTO));
                    }
                }
            }

            preparedOrderedTypes.add(CompetitionZoneListItemAdapter.ITEM_TYPE_LEGAL_MENTIONS);
            Timber.d("rules title " + context.getString(R.string.provider_competition_rules_title));
            preparedOrderedItems.add(new CompetitionZoneLegalDTO(
                    context.getString(R.string.provider_competition_rules_title),
                    context.getString(R.string.provider_competition_terms_title)));
        }
    }
}
