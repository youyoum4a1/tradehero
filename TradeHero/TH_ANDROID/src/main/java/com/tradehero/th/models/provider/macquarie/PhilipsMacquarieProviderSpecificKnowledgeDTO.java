package com.tradehero.th.models.provider.macquarie;

import com.tradehero.th.models.provider.ProviderSpecificKnowledgeDTO;

/**
 * Created by xavier on 2/19/14.
 */
public class PhilipsMacquarieProviderSpecificKnowledgeDTO extends ProviderSpecificKnowledgeDTO
{
    public PhilipsMacquarieProviderSpecificKnowledgeDTO()
    {
        super();
        includeProviderPortfolioOnWarrants = true;
    }
}
