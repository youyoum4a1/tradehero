package com.tradehero.th.fragments.competition.zone.dto;

import android.content.Context;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.CompetitionDTO;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.fragments.competition.CompetitionZoneListItemAdapter;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by xavier on 1/17/14.
 */
@Singleton public class CompetitionZoneDTOUtil
{
    public static final String TAG = CompetitionZoneDTOUtil.class.getSimpleName();

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
            preparedOrderedItems.add(new CompetitionZoneTradeNowDTO(null, null, providerDTO.tradeButtonImageUrl));

            preparedOrderedTypes.add(CompetitionZoneListItemAdapter.ITEM_TYPE_HEADER);
            preparedOrderedItems.add(new CompetitionZoneDTO(providerDTO.ruleText, null));

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

            if (providerDTO.wizardUrl != null && !providerDTO.wizardUrl.equals(""))
            {
                preparedOrderedTypes.add(CompetitionZoneListItemAdapter.ITEM_TYPE_ZONE_ITEM);
                preparedOrderedItems.add(new CompetitionZoneWizardDTO(context.getString(R.string.provider_competition_wizard_title), null));
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
            THLog.d(TAG, "rules title " + context.getString(R.string.provider_competition_rules_title));
            preparedOrderedItems.add(new CompetitionZoneLegalDTO(
                    context.getString(R.string.provider_competition_rules_title),
                    context.getString(R.string.provider_competition_terms_title)));
        }
    }
}
