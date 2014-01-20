package com.tradehero.th.fragments.competition.zone;

import android.content.Context;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderDTO;
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

    public void populateLists(Context context, ProviderDTO providerDTO, List<Integer> preparedOrderedTypes, List<Object> preparedOrderedItems)
    {
        if (providerDTO != null)
        {
            preparedOrderedTypes.add(CompetitionZoneListItemAdapter.ITEM_TYPE_TRADE_NOW);
            preparedOrderedItems.add(new CompetitionZoneTradeNowDTO(null, null, providerDTO.tradeButtonImageUrl));

            preparedOrderedTypes.add(CompetitionZoneListItemAdapter.ITEM_TYPE_HEADER);
            preparedOrderedItems.add(new CompetitionZoneDTO(providerDTO.ruleText, null));

            if (providerDTO.associatedPortfolio != null)
            {
                preparedOrderedTypes.add(CompetitionZoneListItemAdapter.ITEM_TYPE_ZONE_ITEM);
                preparedOrderedItems.add(new CompetitionZonePortfolioDTO(
                        context.getString(R.string.provider_competition_portfolio_title),
                        context.getString(R.string.provider_competition_portfolio_description)));
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

            // TODO add competitions

            preparedOrderedTypes.add(CompetitionZoneListItemAdapter.ITEM_TYPE_LEGAL_MENTIONS);
            THLog.d(TAG, "rules title " + context.getString(R.string.provider_competition_rules_title));
            preparedOrderedItems.add(new CompetitionZoneLegalDTO(
                    context.getString(R.string.provider_competition_rules_title),
                    context.getString(R.string.provider_competition_terms_title)));
        }
    }
}
