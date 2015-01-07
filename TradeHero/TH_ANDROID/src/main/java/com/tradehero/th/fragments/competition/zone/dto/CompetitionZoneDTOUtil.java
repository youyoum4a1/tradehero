package com.tradehero.th.fragments.competition.zone.dto;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.AdDTO;
import com.tradehero.th.api.competition.CompetitionDTO;
import com.tradehero.th.api.competition.CompetitionDTORestrictionComparator;
import com.tradehero.th.api.competition.CompetitionPreSeasonDTO;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderDisplayCellDTO;
import com.tradehero.th.api.competition.ProviderPrizePoolDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOUtil;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.fragments.competition.CompetitionZoneListItemAdapter;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class CompetitionZoneDTOUtil
{
    @NonNull private final PortfolioCompactDTOUtil portfolioCompactDTOUtil;

    private double randomAd;

    //<editor-fold desc="Constructors">
    @Inject public CompetitionZoneDTOUtil(@NonNull PortfolioCompactDTOUtil portfolioCompactDTOUtil)
    {
        super();
        this.portfolioCompactDTOUtil = portfolioCompactDTOUtil;
        randomiseAd();
    }
    //</editor-fold>

    public void randomiseAd()
    {
        randomAd = Math.random();
    }

    public void populateLists(
            @NonNull Context context,
            @Nullable UserProfileCompactDTO portfolioUserProfileCompact,
            @Nullable ProviderDTO providerDTO,
            @Nullable List<CompetitionDTO> competitionDTOs,
            @Nullable List<ProviderDisplayCellDTO> providerDisplayCellDTOs,
            @Nullable List<CompetitionPreSeasonDTO> preSeasonDTOs,
            @Nullable List<ProviderPrizePoolDTO> providerPrizePoolDTOs,
            @NonNull List<Integer> preparedOrderedTypes,
            @NonNull List<CompetitionZoneDTO> preparedOrderedItems)
    {
        if (providerDTO != null)
        {
            if (providerDTO.hasAdvertisement())
            {
                preparedOrderedTypes.add(CompetitionZoneListItemAdapter.ITEM_TYPE_ADS);
                int randomAds = (int) (randomAd * providerDTO.advertisements.size());
                AdDTO pickedAdDTO = providerDTO.advertisements.get(randomAds);
                preparedOrderedItems.add(new CompetitionZoneAdvertisementDTO(null, null, 0, pickedAdDTO));
            }

            preparedOrderedTypes.add(CompetitionZoneListItemAdapter.ITEM_TYPE_HEADER);
            preparedOrderedItems.add(new CompetitionZoneDTO(providerDTO.ruleText, null));

            //prize pool
            if (providerPrizePoolDTOs == null)
            {
                preparedOrderedTypes.add(CompetitionZoneListItemAdapter.ITEM_TYPE_LOADING);
                preparedOrderedItems.add(new DummyLoadingCompetitionDTO());
            }
            else if (!providerPrizePoolDTOs.isEmpty())
            {
                for (ProviderPrizePoolDTO poolDTO : providerPrizePoolDTOs)
                {
                    preparedOrderedTypes.add(CompetitionZoneListItemAdapter.ITEM_TYPE_PRIZE_POOL);
                    preparedOrderedItems.add(new CompetitionZonePrizePoolDTO(poolDTO));
                }

                preparedOrderedTypes.add(CompetitionZoneListItemAdapter.ITEM_TYPE_HEADER);
                preparedOrderedItems.add(new CompetitionZoneDTO(null, null));
            }

            if (providerDTO.associatedPortfolio != null && portfolioUserProfileCompact != null)
            {
                String subtitle = portfolioCompactDTOUtil.getPortfolioSubtitle(context, providerDTO.associatedPortfolio, null);
                preparedOrderedTypes.add(CompetitionZoneListItemAdapter.ITEM_TYPE_PORTFOLIO);
                preparedOrderedItems.add(new CompetitionZonePortfolioDTO(
                        context.getString(R.string.provider_competition_portfolio_title),
                        subtitle,
                        portfolioUserProfileCompact));
            }

            if (preSeasonDTOs == null)
            {
                preparedOrderedTypes.add(CompetitionZoneListItemAdapter.ITEM_TYPE_LOADING);
                preparedOrderedItems.add(new DummyLoadingCompetitionDTO());
            }
            else
            {
                for (CompetitionPreSeasonDTO preSeasonDTO : preSeasonDTOs)
                {
                    preparedOrderedTypes.add(CompetitionZoneListItemAdapter.ITEM_TYPE_ZONE_ITEM);
                    preparedOrderedItems.add(new CompetitionZonePreSeasonDTO(preSeasonDTO));
                }
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
