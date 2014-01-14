package com.tradehero.th.models.intent.position;

import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.PortfolioId;

/**
 * Created by xavier on 1/10/14.
 */
public class OpenPortfolioIntent extends OnePortfolioIntent
{
    public static final String TAG = OpenPortfolioIntent.class.getSimpleName();

    //<editor-fold desc="Constructors">
    public OpenPortfolioIntent(PortfolioId portfolioId)
    {
        super(portfolioId);
    }
    //</editor-fold>

    @Override int getIntentActionResId()
    {
        return R.string.intent_action_portfolio_open;
    }
}
