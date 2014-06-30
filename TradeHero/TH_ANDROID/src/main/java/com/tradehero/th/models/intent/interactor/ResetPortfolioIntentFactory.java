package com.tradehero.th.models.intent.interactor;

import com.tradehero.thm.R;
import javax.inject.Inject;

public class ResetPortfolioIntentFactory extends InteractorIntentFactory
{
    @Inject public ResetPortfolioIntentFactory()
    {
        super();
    }

    @Override public String getHost()
    {
        return getString(R.string.intent_host_reset_portfolio);
    }
}
