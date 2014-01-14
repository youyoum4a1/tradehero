package com.tradehero.th.models.intent.security;

import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityId;

/**
 * Created by xavier on 1/14/14.
 */
public class SimpleSecurityTradeIntent extends SecurityTradeIntent
{
    public static final String TAG = SimpleSecurityTradeIntent.class.getSimpleName();

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
