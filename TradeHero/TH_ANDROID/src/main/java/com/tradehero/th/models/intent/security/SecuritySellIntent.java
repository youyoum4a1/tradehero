package com.tradehero.th.models.intent.security;

import android.os.Bundle;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.trade.BuySellFragment;

/**
 * Created by xavier on 1/10/14.
 */
public class SecuritySellIntent extends SecurityTradeIntent
{
    public static final String TAG = SecuritySellIntent.class.getSimpleName();

    //<editor-fold desc="Constructors">
    public SecuritySellIntent(SecurityId securityId)
    {
        super(securityId);
    }
    //</editor-fold>

    @Override int getIntentActionResId()
    {
        return R.string.intent_action_security_sell;
    }

    @Override public void populate(Bundle bundle)
    {
        super.populate(bundle);
        bundle.putBoolean(BuySellFragment.BUNDLE_KEY_IS_BUY, false);
    }
}
