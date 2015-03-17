package com.tradehero.th.fragments.alert;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuInflater;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.network.service.AlertServiceWrapper;
import com.tradehero.th.network.service.QuoteServiceWrapper;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import dagger.Lazy;
import javax.inject.Inject;

@Deprecated
public class AlertCreateFragment extends BaseAlertEditFragment
{
    private static final String BUNDLE_KEY_SECURITY_ID_BUNDLE = BaseAlertEditFragment.class.getName() + ".securityId";

    @Inject protected SecurityCompactCacheRx securityCompactCache;
    @Inject protected Lazy<AlertServiceWrapper> alertServiceWrapper;
    @Inject protected QuoteServiceWrapper quoteServiceWrapper;

    SecurityId securityId;

    public static void putSecurityId(@NonNull Bundle args, @NonNull SecurityId securityId)
    {
        args.putBundle(BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
    }

    @NonNull public static SecurityId getSecurityId(@NonNull Bundle args)
    {
        return new SecurityId(args.getBundle(BUNDLE_KEY_SECURITY_ID_BUNDLE));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        securityId = getSecurityId(getArguments());
        viewHolder = new AlertCreateFragmentHolder(
                getActivity(),
                getResources(),
                currentUserId,
                securityAlertCountingHelper,
                quoteServiceWrapper,
                securityCompactCache,
                alertServiceWrapper,
                securityId);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarTitle(R.string.stock_alert_add_alert);
    }
}
