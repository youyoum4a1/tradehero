package com.tradehero.th.api.competition.specific.macquarie;

import com.tradehero.th.api.competition.specific.ProviderSpecificKnowledgeDTO;

@Deprecated
public class MacquarieProviderSpecificKnowledgeDTO extends ProviderSpecificKnowledgeDTO
{
    public MacquarieProviderSpecificKnowledgeDTO()
    {
        super();
        includeProviderPortfolioOnWarrants = true;
    }
}
