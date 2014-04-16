package com.tradehero.th.models.intent.interactor;

import com.tradehero.th.R;
import com.tradehero.th.billing.googleplay.THIABBillingInteractor;

/**
 * Created with IntelliJ IDEA. User: tho Date: 2/24/14 Time: 5:52 PM Copyright (c) TradeHero
 */
public class ResetPortfolioIntent extends InteractorIntent
{
    //<editor-fold desc="Constructors">
    public ResetPortfolioIntent()
    {
        super();
    }
    //</editor-fold>

    @Override public String getUriPath()
    {
        return getHostUriPath(R.string.intent_host_reset_portfolio);
    }

    @Override protected int getInteractorAction()
    {
        return THIABBillingInteractor.ACTION_RESET_PORTFOLIO;
    }
}
