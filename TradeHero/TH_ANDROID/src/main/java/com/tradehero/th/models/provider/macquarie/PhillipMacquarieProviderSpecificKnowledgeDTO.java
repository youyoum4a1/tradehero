package com.tradehero.th.models.provider.macquarie;

import com.tradehero.th.models.provider.ProviderSpecificKnowledgeDTO;

public class PhillipMacquarieProviderSpecificKnowledgeDTO extends ProviderSpecificKnowledgeDTO
{
    public PhillipMacquarieProviderSpecificKnowledgeDTO()
    {
        super();
        includeProviderPortfolioOnWarrants = true;
    }
}
