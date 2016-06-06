package com.androidth.general.models.intent.security;

import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.R;
import com.androidth.general.api.security.SecurityId;
import com.androidth.general.api.security.SecurityIntegerId;
import com.androidth.general.fragments.dashboard.RootFragmentType;
import com.androidth.general.fragments.trade.AbstractBuySellFragment;
import com.androidth.general.fragments.trade.BuySellStockFragment;
import com.androidth.general.models.intent.THIntent;
import java.util.List;

public class SecurityPushBuyIntent extends THIntent
{
    //<editor-fold desc="Constructors">
    public SecurityPushBuyIntent(
            @NonNull Resources resources,
            @NonNull SecurityIntegerId securityIntegerId,
            @NonNull SecurityId securityId)
    {
        super(resources);
        setData(getSecurityActionUri(securityIntegerId, securityId));
    }
    //</editor-fold>

    @Override @NonNull public String getUriPath()
    {
        return getHostUriPath(resources, R.string.intent_host_security);
    }

    @Override public RootFragmentType getDashboardType()
    {
        //TODO when you click the link from user send private message "[$TSX:MM](tradehero://security/38756_TSX_MM)", will come here
        //throw new IllegalStateException("This intent is not tab based");
        THToast.show("This intent is not tab based");
        return null;
    }

    public Uri getSecurityActionUri(@NonNull SecurityIntegerId securityIntegerId, @NonNull SecurityId securityId)
    {
        return Uri.parse(getSecurityActionUriPath(securityIntegerId, securityId));
    }

    public String getSecurityActionUriPath(@NonNull SecurityIntegerId securityIntegerId, @NonNull SecurityId securityId)
    {
        return resources.getString(
                R.string.intent_security_push_buy_action,
                resources.getString(R.string.intent_scheme),
                resources.getString(R.string.intent_host_security),
                securityIntegerId.key,
                securityId.getExchange(),
                securityId.getSecuritySymbol());
    }

    public SecurityIntegerId getSecurityIntegerId()
    {
        return getSecurityIntegerId(resources, getData());
    }

    public static SecurityIntegerId getSecurityIntegerId(
            @NonNull Resources resources,
            @NonNull Uri data)
    {
        return getSecurityIntegerId(resources, data.getPathSegments());
    }

    public static SecurityIntegerId getSecurityIntegerId(
            @NonNull Resources resources,
            @NonNull List<String> pathSegments)
    {
        String[] splitElements = getSplitElements(pathSegments.get(resources.getInteger(R.integer.intent_security_push_buy_index_elements)));
        return new SecurityIntegerId(Integer.parseInt(splitElements[resources.getInteger(
                R.integer.intent_security_push_buy_split_index_security_num_key)]));
    }

    public SecurityId getSecurityId()
    {
        return getSecurityId(resources, getData());
    }

    public static SecurityId getSecurityId(
            @NonNull Resources resources,
            @NonNull Uri data)
    {
        return getSecurityId(resources, data.getPathSegments());
    }

    public static SecurityId getSecurityId(
            @NonNull Resources resources,
            @NonNull List<String> pathSegments)
    {
        String[] splitElements = getSplitElements(pathSegments.get(resources.getInteger(R.integer.intent_security_push_buy_index_elements)));
        return new SecurityId(
                splitElements[resources.getInteger(R.integer.intent_security_push_buy_split_index_security_exchange_key)],
                splitElements[resources.getInteger(R.integer.intent_security_push_buy_split_index_security_symbol_key)]);
    }

    public static String[] getSplitElements(String elements)
    {
        return elements.split("_");
    }

    @Override public Class<? extends Fragment> getActionFragment()
    {
        return BuySellStockFragment.class;
    }

    @Override public void populate(Bundle bundle)
    {
        super.populate(bundle);
        AbstractBuySellFragment.putRequisite(
                bundle,
                new AbstractBuySellFragment.Requisite(
                        getSecurityId(),
                        null, // TODO do better
                        0));
    }
}
