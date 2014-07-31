package com.tradehero.th.fragments.competition.zone.dto;

import android.content.Context;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.AdDTO;
import com.tradehero.th.api.competition.CompetitionDTO;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderDisplayCellDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOUtil;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.fragments.competition.CompetitionZoneListItemAdapter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class CompetitionZoneDTOUtil
{

    @Inject PortfolioCompactDTOUtil portfolioCompactDTOUtil;

    //<editor-fold desc="Constructors">
    @Inject public CompetitionZoneDTOUtil()
    {
        super();
    }
    //</editor-fold>

    public void populateLists(Context context,
            UserProfileCompactDTO portfolioUserProfileCompact,
            ProviderDTO providerDTO,
            List<CompetitionDTO> competitionDTOs,
            List<ProviderDisplayCellDTO> providerDisplayCellDTOs,
            List<Integer> preparedOrderedTypes,
            List<Object> preparedOrderedItems)
    {
        if (providerDTO != null)
        {
            if (providerDTO.hasAdvertisement())
            {
                preparedOrderedTypes.add(CompetitionZoneListItemAdapter.ITEM_TYPE_ADS);
                int randomAds = (int) (Math.random() * providerDTO.advertisements.size());
                AdDTO pickedAdDTO = providerDTO.advertisements.get(randomAds);
                preparedOrderedItems.add(new CompetitionZoneAdvertisementDTO(null, null, 0, pickedAdDTO));
            }

            preparedOrderedTypes.add(CompetitionZoneListItemAdapter.ITEM_TYPE_HEADER);
            preparedOrderedItems.add(new CompetitionZoneDTO(providerDTO.ruleText, null));

            if (providerDTO.associatedPortfolio != null)
            {
                String subtitle = portfolioCompactDTOUtil.getPortfolioSubtitle(context, providerDTO.associatedPortfolio, null);
                preparedOrderedTypes.add(CompetitionZoneListItemAdapter.ITEM_TYPE_PORTFOLIO);
                preparedOrderedItems.add(new CompetitionZonePortfolioDTO(
                        context.getString(R.string.provider_competition_portfolio_title),
                        subtitle,
                        portfolioUserProfileCompact));
            }

            if (providerDTO.hasHelpVideo)
            {
                preparedOrderedTypes.add(CompetitionZoneListItemAdapter.ITEM_TYPE_ZONE_ITEM);
                preparedOrderedItems.add(new CompetitionZoneVideoDTO(providerDTO.helpVideoText, null));
            }

            if (providerDisplayCellDTOs != null)
            {
                for (ProviderDisplayCellDTO providerDisplayCellDTO : providerDisplayCellDTOs)
                {
                    if (providerDisplayCellDTO != null)
                    {
                        preparedOrderedTypes.add(CompetitionZoneListItemAdapter.ITEM_TYPE_ZONE_ITEM);
                        preparedOrderedItems.add(new CompetitionZoneDisplayCellDTO(providerDisplayCellDTO));
                    }
                }
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
                Collections.sort(competitionDTOs, CompetitionDTO.RestrictionLeaderboardComparator);
                for (CompetitionDTO competitionDTO : competitionDTOs)
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

            preparedOrderedTypes.add(CompetitionZoneListItemAdapter.ITEM_TYPE_HEADER);
            preparedOrderedItems.add(new CompetitionZoneDTO(null, null));

            preparedOrderedTypes.add(CompetitionZoneListItemAdapter.ITEM_TYPE_LEGAL_MENTIONS);
            Timber.d("rules title " + context.getString(R.string.provider_competition_rules_title));
            preparedOrderedItems.add(new CompetitionZoneLegalDTO(
                    context.getString(R.string.provider_competition_rules_title),
                    context.getString(R.string.provider_competition_terms_title)));

            preparedOrderedTypes.add(CompetitionZoneListItemAdapter.ITEM_TYPE_HEADER);
            preparedOrderedItems.add(new CompetitionZoneDTO(null, null));
        }
    }
}
