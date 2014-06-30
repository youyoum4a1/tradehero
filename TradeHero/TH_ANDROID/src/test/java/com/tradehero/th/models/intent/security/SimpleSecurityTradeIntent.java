package com.tradehero.th.models.intent.security;

import com.tradehero.thm.R;
import com.tradehero.th.api.security.SecurityId;

public class SimpleSecurityTradeIntent extends SecurityTradeIntent
{
    //<editor-fold desc="Constructors">
    protected SimpleSecurityTradeIntent(SecurityId securityId)
    {
        super(securityId);
    }
    //</editor-fold>

    @Override int getIntentActionResId()
    {
        return R.string.intent_action_portfolio_open;
    }
}
