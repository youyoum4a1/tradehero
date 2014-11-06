package com.tradehero.th.fragments.competition.zone.dto;

import android.content.Context;

import com.tradehero.th.R;
import com.tradehero.th.api.competition.AdDTO;
import com.tradehero.th.api.competition.CompetitionDTO;
import com.tradehero.th.api.competition.CompetitionDTORestrictionComparator;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderDisplayCellDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOUtil;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.fragments.competition.CompetitionZoneListItemAdapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import timber.log.Timber;

public class CompetitionZoneDTOUtil
{
    @NonNull private final PortfolioCompactDTOUtil portfolioCompactDTOUtil;

    //<editor-fold desc="Constructors">
    @Inject public CompetitionZoneDTOUtil(@NonNull PortfolioCompactDTOUtil portfolioCompactDTOUtil)
    {
        super();
        this.portfolioCompactDTOUtil = portfolioCompactDTOUtil;
    }
    //</editor-fold>

    public void populateLists(
            @NonNull Context context,
            @Nullable UserProfileCompactDTO portfolioUserProfileCompact,
            @Nullable ProviderDTO providerDTO,
            @Nullable List<CompetitionDTO> competitionDTOs,
            @Nullable List<ProviderDisplayCellDTO> providerDisplayCellDTOs,
            @NonNull List<Integer> preparedOrderedTypes,
            @NonNull List<CompetitionZoneDTO> preparedOrderedItems)
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

            preparedOrderedTypes.add(CompetitionZoneListItemAdapter.ITEM_TYPE_PRIZE_POOL);
            preparedOrderedItems.add(new CompetitionZoneLegalDTO(
                    context.getString(R.string.provider_competition_rules_title),
                    context.getString(R.string.provider_competition_terms_title)));

            if (providerDTO.associatedPortfolio != null && portfolioUserProfileCompact != null)
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
            else
            {
                preparedOrderedTypes.add(CompetitionZoneListItemAdapter.ITEM_TYPE_LOADING);
                preparedOrderedItems.add(new DummyLoadingCompetitionDTO());
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
                Collections.sort(competitionDTOs, new CompetitionDTORestrictionComparator());
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
            else
            {
                preparedOrderedTypes.add(CompetitionZoneListItemAdapter.ITEM_TYPE_LOADING);
                preparedOrderedItems.add(new DummyLoadingCompetitionDTO());
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
