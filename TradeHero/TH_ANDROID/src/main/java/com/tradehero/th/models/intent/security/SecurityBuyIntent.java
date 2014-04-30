package com.tradehero.th.models.intent.security;

import android.os.Bundle;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.trade.BuySellFragment;

public class SecurityBuyIntent extends SecurityTradeIntent
{
    //<editor-fold desc="Constructors">
    public SecurityBuyIntent(SecurityId securityId)
    {
        super(securityId);
    }
    //</editor-fold>

    @Override int getIntentActionResId()
    {
        return R.string.intent_action_security_buy;
    }

    @Override public void populate(Bundle bundle)
    {
        super.populate(bundle);
        bundle.putBoolean(BuySellFragment.BUNDLE_KEY_IS_BUY, true);
    }
}
