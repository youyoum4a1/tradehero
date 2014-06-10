package com.tradehero.th.models.intent.position;

import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.PortfolioId;

public class SimpleOnePortfolioIntent extends OnePortfolioIntent
{
    //<editor-fold desc="Constructors">
    protected SimpleOnePortfolioIntent(PortfolioId portfolioId)
    {
        super(portfolioId);
    }
    //</editor-fold>

    @Override int getIntentActionResId()
    {
        return R.string.intent_action_security_buy;
    }
}
