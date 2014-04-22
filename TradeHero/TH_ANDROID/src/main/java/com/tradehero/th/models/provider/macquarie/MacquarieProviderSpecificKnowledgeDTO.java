package com.tradehero.th.models.provider.macquarie;

import com.tradehero.th.models.provider.ProviderSpecificKnowledgeDTO;

public class MacquarieProviderSpecificKnowledgeDTO extends ProviderSpecificKnowledgeDTO
{
    public MacquarieProviderSpecificKnowledgeDTO()
    {
        super();
        includeProviderPortfolioOnWarrants = true;
    }
}
