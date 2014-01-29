package com.tradehero.th.models.provider;

import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderIdConstants;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by xavier on 1/29/14.
 */
@Singleton public class ProviderSpecificKnowledgeFactory
{
    public static final String TAG = ProviderSpecificKnowledgeFactory.class.getSimpleName();

    @Inject public ProviderSpecificKnowledgeFactory()
    {
        super();
    }

    public ProviderSpecificKnowledgeDTO createKnowledge(ProviderId providerId)
    {
        ProviderSpecificKnowledgeDTO created = null;

        if (providerId != null)
        {
            switch (providerId.key)
            {
                case ProviderIdConstants.PROVIDER_ID_MACQUARIE_WARRANTS:
                    created = new ProviderSpecificKnowledgeDTO();
                    created.includeProviderPortfolioOnWarrants = true;
                    break;
            }
        }

        return created;
    }
}
