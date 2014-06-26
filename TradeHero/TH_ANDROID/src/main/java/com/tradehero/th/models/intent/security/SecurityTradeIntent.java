package com.tradehero.th.models.intent.security;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.models.intent.trending.TrendingIntent;
import java.util.List;

abstract public class SecurityTradeIntent extends TrendingIntent
{
    //<editor-fold desc="Constructors">
    protected SecurityTradeIntent(SecurityId securityId)
    {
        super();
        setData(getSecurityActionUri(securityId));
    }
    //</editor-fold>

    public Uri getSecurityActionUri(SecurityId securityId)
    {
        return Uri.parse(getSecurityActionUriPath(securityId));
    }

    public String getSecurityActionUriPath(SecurityId securityId)
    {
        return getString(
                getIntentActionUriResId(),
                getString(R.string.intent_scheme),
                getString(R.string.intent_host_trending),
                getString(getIntentActionResId()),
                securityId.getExchange(),
                securityId.getSecuritySymbol());
    }

    public int getIntentActionUriResId()
    {
        return R.string.intent_uri_action_trade_security;
    }

    abstract int getIntentActionResId();

    public SecurityId getSecurityId()
    {
        return getSecurityId(getData());
    }

    public static SecurityId getSecurityId(Uri data)
    {
        return getSecurityId(data.getPathSegments());
    }

    public static SecurityId getSecurityId(List<String> pathSegments)
    {
        return new SecurityId(
                pathSegments.get(getInteger(R.integer.intent_uri_action_trade_security_path_index_exchange)),
                pathSegments.get(getInteger(R.integer.intent_uri_action_trade_security_path_index_security)));
    }

    @Override public Class<? extends Fragment> getActionFragment()
    {
        return BuySellFragment.class;
    }

    @Override public void populate(Bundle bundle)
    {
        super.populate(bundle);
        bundle.putBundle(BuySellFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, getSecurityId().getArgs());
    }
}
