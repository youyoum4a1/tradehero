package com.tradehero.th.models.intent.watchlist;

import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.models.intent.position.OnePortfolioIntent;

/**
 * Created by xavier on 1/10/14.
 */
public class WatchListOpenIntent extends OneWatchlistIntent
{
    public static final String TAG = WatchListOpenIntent.class.getSimpleName();

    //<editor-fold desc="Constructors">
    public WatchListOpenIntent(PortfolioId portfolioId)
    {
        super(portfolioId);
    }
    //</editor-fold>

    @Override int getIntentActionResId()
    {
        return R.string.intent_action_portfolio_open;
    }
}
