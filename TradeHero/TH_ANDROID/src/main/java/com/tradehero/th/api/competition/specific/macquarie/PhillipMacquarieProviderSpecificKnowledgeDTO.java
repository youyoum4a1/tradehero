package com.tradehero.th.api.competition.specific.macquarie;

import com.tradehero.th.api.competition.specific.ProviderSpecificKnowledgeDTO;

@Deprecated
public class PhillipMacquarieProviderSpecificKnowledgeDTO extends ProviderSpecificKnowledgeDTO
{
    public PhillipMacquarieProviderSpecificKnowledgeDTO()
    {
        super();
        includeProviderPortfolioOnWarrants = true;
    }
}
