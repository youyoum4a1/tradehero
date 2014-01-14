package com.tradehero.th.models.intent.watchlist;

import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.models.intent.position.OnePortfolioIntent;

/**
 * Created by xavier on 1/14/14.
 */
public class SimpleOneWatchlistIntent extends OneWatchlistIntent
{
    public static final String TAG = SimpleOneWatchlistIntent.class.getSimpleName();

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
