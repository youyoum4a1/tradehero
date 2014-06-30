package com.tradehero.th.models.intent.interactor;

import com.tradehero.thm.R;
import com.tradehero.th.billing.googleplay.THIABBillingInteractor;

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
