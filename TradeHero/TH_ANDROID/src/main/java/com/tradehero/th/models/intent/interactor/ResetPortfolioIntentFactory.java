package com.tradehero.th.models.intent.interactor;

import com.tradehero.th.R;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: tho Date: 2/24/14 Time: 6:18 PM Copyright (c) TradeHero
 */
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
