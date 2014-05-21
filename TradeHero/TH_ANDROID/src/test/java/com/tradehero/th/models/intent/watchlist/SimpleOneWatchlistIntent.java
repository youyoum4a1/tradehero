package com.tradehero.th.models.intent.watchlist;

import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.PortfolioId;

public class SimpleOneWatchlistIntent extends OneWatchlistIntent
{
    //<editor-fold desc="Constructors">
    protected SimpleOneWatchlistIntent(PortfolioId portfolioId)
    {
        super(portfolioId);
    }
    //</editor-fold>

    @Override int getIntentActionResId()
    {
        return R.string.intent_action_security_buy;
    }
}
