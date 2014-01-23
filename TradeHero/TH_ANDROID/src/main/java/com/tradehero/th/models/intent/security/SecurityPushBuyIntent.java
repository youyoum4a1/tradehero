package com.tradehero.th.models.intent.security;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIntegerId;
import com.tradehero.th.fragments.dashboard.DashboardTabType;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.models.intent.THIntent;
import java.util.List;

/**
 * Created by xavier on 1/23/14.
 */
public class SecurityPushBuyIntent extends THIntent
{
    public static final String TAG = SecurityPushBuyIntent.class.getSimpleName();

    public SecurityPushBuyIntent(SecurityIntegerId securityIntegerId, SecurityId securityId)
    {
        super();
        setData(getSecurityActionUri(securityIntegerId, securityId));
    }

    @Override public String getUriPath()
    {
        return getHostUriPath(R.string.intent_host_security);
    }

    @Override public DashboardTabType getDashboardType()
    {
        throw new IllegalStateException("This intent is not tab based");
    }

    public Uri getSecurityActionUri(SecurityIntegerId securityIntegerId, SecurityId securityId)
    {
        return Uri.parse(getSecurityActionUriPath(securityIntegerId, securityId));
    }

    public String getSecurityActionUriPath(SecurityIntegerId securityIntegerId, SecurityId securityId)
    {
        return getString(
                R.string.intent_security_push_buy_action,
                getString(R.string.intent_scheme),
                getString(R.string.intent_host_security),
                securityIntegerId.key,
                securityId.exchange,
                securityId.securitySymbol);
    }

    public SecurityIntegerId getSecurityIntegerId()
    {
        return getSecurityIntegerId(getData());
    }

    public static SecurityIntegerId getSecurityIntegerId(Uri data)
    {
        return getSecurityIntegerId(data.getPathSegments());
    }

    public static SecurityIntegerId getSecurityIntegerId(List<String> pathSegments)
    {
        String[] splitElements = getSplitElements(pathSegments.get(getInteger(R.integer.intent_security_push_buy_index_elements)));
        return new SecurityIntegerId(Integer.parseInt(splitElements[getInteger(R.integer.intent_security_push_buy_split_index_security_num_key)]));
    }

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
        String[] splitElements = getSplitElements(pathSegments.get(getInteger(R.integer.intent_security_push_buy_index_elements)));
        return new SecurityId(
                splitElements[getInteger(R.integer.intent_security_push_buy_split_index_security_exchange_key)],
                splitElements[getInteger(R.integer.intent_security_push_buy_split_index_security_symbol_key)]);
    }

    public static String[] getSplitElements(String elements)
    {
        return elements.split("_");
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
