package com.tradehero.th.models.provider;

import org.jetbrains.annotations.Nullable;

public class ProviderSpecificKnowledgeDTO
{
    @Nullable public Boolean includeProviderPortfolioOnWarrants;
    @Nullable public Boolean hasWizard;

    //<editor-fold desc="Constructors">
    public ProviderSpecificKnowledgeDTO()
    {
        super();
    }
    //</editor-fold>
}
