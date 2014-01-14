package com.tradehero.th.models.intent.watchlist;

import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.PortfolioId;

/**
 * Created by xavier on 1/10/14.
 */
public class WatchlistOpenIntent extends OneWatchlistIntent
{
    public static final String TAG = WatchlistOpenIntent.class.getSimpleName();

    //<editor-fold desc="Constructors">
    public WatchlistOpenIntent(PortfolioId portfolioId)
    {
        super(portfolioId);
    }
    //</editor-fold>

    @Override int getIntentActionResId()
    {
        return R.string.intent_action_portfolio_open;
    }
}
