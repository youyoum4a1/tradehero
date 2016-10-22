package com.androidth.general.fragments.competition.zone.dto;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;

import com.androidth.general.R;
import com.androidth.general.api.competition.AdDTO;
import com.androidth.general.api.competition.CompetitionDTO;
import com.androidth.general.api.competition.CompetitionDTORestrictionComparator;
import com.androidth.general.api.competition.CompetitionPreSeasonDTO;
import com.androidth.general.api.competition.ProviderDTO;
import com.androidth.general.api.competition.ProviderDisplayCellDTO;
import com.androidth.general.api.competition.ProviderPrizePoolDTO;
import com.androidth.general.api.portfolio.AssetClass;
import com.androidth.general.api.portfolio.PortfolioCompactDTOUtil;
import com.androidth.general.api.users.UserProfileCompactDTO;
import com.androidth.general.fragments.competition.CompetitionZoneListItemAdapter;
import com.androidth.general.fragments.leaderboard.LeaderboardType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class CompetitionZoneDTOUtil
{
    private double randomAd;

    //<editor-fold desc="Constructors">
    @Inject public CompetitionZoneDTOUtil()
    {
        super();
        randomiseAd();
    }
    //</editor-fold>

    public void randomiseAd()
    {
        randomAd = Math.random();
    }

    @NonNull public List<Pair<Integer, CompetitionZoneDTO>> makeList(
            @NonNull Context context,
            @Nullable UserProfileCompactDTO portfolioUserProfileCompact,
            @Nullable ProviderDTO providerDTO,
            @Nullable List<CompetitionDTO> competitionDTOs,
            @Nullable List<ProviderDisplayCellDTO> providerDisplayCellDTOs,
            @Nullable List<CompetitionPreSeasonDTO> preSeasonDTOs,
            @Nullable List<ProviderPrizePoolDTO> providerPrizePoolDTOs)
    {
        List<Pair<Integer, CompetitionZoneDTO>> list = new ArrayList<>();

        if (providerDTO != null)
        {
            if (providerDTO.hasAdvertisement())
            {
                int randomAds = (int) (randomAd * providerDTO.advertisements.size());
                AdDTO pickedAdDTO = providerDTO.advertisements.get(randomAds);
                list.add(Pair.create(
                        CompetitionZoneListItemAdapter.ITEM_TYPE_ADS,
                        new CompetitionZoneAdvertisementDTO(context, pickedAdDTO, providerDTO.getProviderId())));
            }

            list.add(Pair.create(
                    CompetitionZoneListItemAdapter.ITEM_TYPE_HEADER,
                    new CompetitionZoneDTO(providerDTO.ruleText, null, null, R.drawable.default_image)));

            //prize pool
            if (providerPrizePoolDTOs == null)
            {
                //list.add(Pair.create(
                //        CompetitionZoneListItemAdapter.ITEM_TYPE_LOADING,
                //        (CompetitionZoneDTO) new DummyLoadingCompetitionDTO()));
            }
            else if (!providerPrizePoolDTOs.isEmpty())
            {
                for (ProviderPrizePoolDTO poolDTO : providerPrizePoolDTOs)
                {
                    list.add(Pair.create(
                            CompetitionZoneListItemAdapter.ITEM_TYPE_PRIZE_POOL,
                            new CompetitionZonePrizePoolDTO(context.getResources(), poolDTO)));
                }

                list.add(Pair.create(
                        CompetitionZoneListItemAdapter.ITEM_TYPE_HEADER,
                        new CompetitionZoneDTO(null, null, null, R.drawable.default_image)));
            }

            if (providerDTO.associatedPortfolio != null && portfolioUserProfileCompact != null)
            {
                String subtitle = PortfolioCompactDTOUtil.getPortfolioSubtitle(context.getResources(), providerDTO.associatedPortfolio, null);
                list.add(Pair.create(
                        CompetitionZoneListItemAdapter.ITEM_TYPE_PORTFOLIO,
                        new CompetitionZonePortfolioDTO(
                                context.getString(R.string.provider_competition_portfolio_title),
                                subtitle,
                                portfolioUserProfileCompact)));
            }

            if (preSeasonDTOs == null)
            {
                //list.add(Pair.create(
                //        CompetitionZoneListItemAdapter.ITEM_TYPE_LOADING,
                //        (CompetitionZoneDTO) new DummyLoadingCompetitionDTO()));
            }
            else
            {
                for (CompetitionPreSeasonDTO preSeasonDTO : preSeasonDTOs)
                {
                    list.add(Pair.create(
                            CompetitionZoneListItemAdapter.ITEM_TYPE_ZONE_ITEM,
                            new CompetitionZonePreSeasonDTO(preSeasonDTO)));
                }
            }

            if (providerDisplayCellDTOs != null)
            {
                List<String> wizards = new ArrayList<>();
                for (ProviderDisplayCellDTO providerDisplayCellDTO : providerDisplayCellDTOs)
                {
                    if (providerDisplayCellDTO != null)
                    {
                        if (providerDisplayCellDTO.redirectUrl != null
                                && providerDisplayCellDTO.redirectUrl.toLowerCase().contains("wizard"))
                        {
                            CompetitionZoneWizardDTO wizardDTO = new CompetitionZoneWizardDTO(providerDisplayCellDTO, context.getResources());
                            list.add(Pair.create(
                                    CompetitionZoneListItemAdapter.ITEM_TYPE_WIZARD,
                                    wizardDTO));
                            String webUrl = wizardDTO.getWebUrl();
                            if (webUrl != null)
                            {
                                wizards.add(webUrl.toLowerCase());
                            }
                        }
                        else
                        {
                            list.add(Pair.create(
                                    CompetitionZoneListItemAdapter.ITEM_TYPE_ZONE_ITEM,
                                    new CompetitionZoneDisplayCellDTO(providerDisplayCellDTO)));
                        }
                    }
                }

                if (providerDTO.wizardUrl != null
                        && providerDTO.wizardTitle != null
                        && !wizards.contains(providerDTO.wizardUrl.toLowerCase()))
                {
                    list.add(Pair.create(
                            CompetitionZoneListItemAdapter.ITEM_TYPE_WIZARD,
                            new CompetitionZoneWizardDTO(
                                    providerDTO.wizardTitle,
                                    null,
                                    providerDTO.wizardImageUrl,
                                    providerDTO.wizardUrl)));
                }
            }
            else
            {
                list.add(Pair.create(
                        CompetitionZoneListItemAdapter.ITEM_TYPE_LOADING,
                        new DummyLoadingCompetitionDTO()));
            }

            list.add(Pair.create(
                    CompetitionZoneListItemAdapter.ITEM_TYPE_HEADER,
                    new CompetitionZoneDTO(null, null, null, R.drawable.default_image)));

            if (competitionDTOs != null)
            {
                AssetClass assetClass = (providerDTO.associatedPortfolio != null && providerDTO.associatedPortfolio.assetClass != null)
                        ? providerDTO.associatedPortfolio.assetClass
                        : AssetClass.STOCKS;
                LeaderboardType leaderboardType = AssetClass.FX.equals(assetClass) ? LeaderboardType.FX : null;
                Collections.sort(competitionDTOs, new CompetitionDTORestrictionComparator());
                for (CompetitionDTO competitionDTO : competitionDTOs)
                {
                    if (competitionDTO != null)
                    {
                        list.add(Pair.create(
                                CompetitionZoneListItemAdapter.ITEM_TYPE_LEADERBOARD,
                                new CompetitionZoneLeaderboardDTO(
                                        context.getResources(),
                                        competitionDTO.name,
                                        competitionDTO.leaderboard != null ? competitionDTO.leaderboard.desc : "",
                                        competitionDTO,
                                        leaderboardType)));
                    }
                }
            }
            else
            {
                list.add(Pair.create(
                        CompetitionZoneListItemAdapter.ITEM_TYPE_LOADING,
                        new DummyLoadingCompetitionDTO()));
            }

            list.add(Pair.create(
                    CompetitionZoneListItemAdapter.ITEM_TYPE_HEADER,
                    new CompetitionZoneDTO(null, null, null, R.drawable.default_image)));

            Timber.d("rules title " + context.getString(R.string.provider_competition_rules_title));
            list.add(Pair.create(
                    CompetitionZoneListItemAdapter.ITEM_TYPE_LEGAL_MENTIONS,
                    new CompetitionZoneLegalDTO(
                            context.getString(R.string.provider_competition_rules_title),
                            context.getString(R.string.provider_competition_terms_title))));

            list.add(Pair.create(
                    CompetitionZoneListItemAdapter.ITEM_TYPE_HEADER,
                    new CompetitionZoneDTO(null, null, null, R.drawable.default_image)));
        }

        return list;
    }

    @NonNull public List<Pair<Integer, CompetitionZoneDTO>> makeList(
            @NonNull Context context,
            @Nullable ProviderDTO providerDTO)
    {
        List<Pair<Integer, CompetitionZoneDTO>> list = new ArrayList<>();

        if (providerDTO != null)
        {
            if (providerDTO.hasAdvertisement())
            {
                int randomAds = (int) (randomAd * providerDTO.advertisements.size());
                AdDTO pickedAdDTO = providerDTO.advertisements.get(randomAds);
                pickedAdDTO.providerId = providerDTO.id;
                list.add(Pair.create(
                        CompetitionZoneListItemAdapter.ITEM_TYPE_ADS,
                        new CompetitionZoneAdvertisementDTO(context, pickedAdDTO, providerDTO.getProviderId())));
            }

            //2016-08-01T00:00:00 show only 10 characters
            list.add(Pair.create(
                    CompetitionZoneListItemAdapter.ITEM_TYPE_HEADER,
                    new CompetitionZoneDTO("Competition starts in "+providerDTO.tradeNowStartDateUtc.substring(0,10), null, null, 0)));

            list.add(Pair.create(
                    CompetitionZoneListItemAdapter.ITEM_TYPE_LEGAL_MENTIONS,
                    new CompetitionZoneLegalDTO(
                            context.getString(R.string.provider_competition_rules_title),
                            context.getString(R.string.provider_competition_terms_title))));

        }

        return list;
    }
}
