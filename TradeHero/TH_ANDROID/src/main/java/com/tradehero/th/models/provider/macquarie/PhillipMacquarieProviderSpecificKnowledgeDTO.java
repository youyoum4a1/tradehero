package com.tradehero.th.models.provider.macquarie;

import com.tradehero.th.models.provider.ProviderSpecificKnowledgeDTO;

/**
 * Created by xavier on 2/19/14.
 */
public class PhillipMacquarieProviderSpecificKnowledgeDTO extends ProviderSpecificKnowledgeDTO
{
    public PhillipMacquarieProviderSpecificKnowledgeDTO()
    {
        super();
        includeProviderPortfolioOnWarrants = true;
    }
}
