package com.tradehero.th.models.intent.position;

import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.PortfolioId;

/**
 * Created by xavier on 1/14/14.
 */
public class SimpleOnePortfolioIntent extends OnePortfolioIntent
{
    public static final String TAG = SimpleOnePortfolioIntent.class.getSimpleName();

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
