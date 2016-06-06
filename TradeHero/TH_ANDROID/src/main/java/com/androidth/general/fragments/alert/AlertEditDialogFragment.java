package com.androidth.general.fragments.alert;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.androidth.general.api.alert.AlertId;
import com.androidth.general.network.service.AlertServiceWrapper;
import com.androidth.general.network.service.QuoteServiceWrapper;
import com.androidth.general.persistence.alert.AlertCacheRx;
import dagger.Lazy;
import javax.inject.Inject;

public class AlertEditDialogFragment extends BaseAlertEditDialogFragment
{
    private static final String BUNDLE_KEY_ALERT_ID_BUNDLE = AlertEditDialogFragment.class.getName() + ".alertId";

    @Inject protected AlertCacheRx alertCache;
    @Inject protected Lazy<AlertServiceWrapper> alertServiceWrapper;
    @Inject protected QuoteServiceWrapper quoteServiceWrapper;

    protected AlertId alertId;

    @NonNull public static AlertEditDialogFragment newInstance(@NonNull AlertId alertId)
    {
        AlertEditDialogFragment fragment = new AlertEditDialogFragment();
        Bundle args = new Bundle();
        putAlertId(args, alertId);
        fragment.setArguments(args);
        return fragment;
    }

    public static void putAlertId(@NonNull Bundle args, @NonNull AlertId alertId)
    {
        args.putBundle(BUNDLE_KEY_ALERT_ID_BUNDLE, alertId.getArgs());
    }

    @NonNull public static AlertId getAlertId(@NonNull Bundle args)
    {
        return new AlertId(args.getBundle(BUNDLE_KEY_ALERT_ID_BUNDLE));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        alertId = getAlertId(getArguments());
        viewHolder = new AlertEditFragmentHolder(
                getActivity(),
                getResources(),
                currentUserId,
                securityAlertCountingHelper,
                quoteServiceWrapper,
                alertCache,
                alertServiceWrapper,
                alertId);
    }
}
