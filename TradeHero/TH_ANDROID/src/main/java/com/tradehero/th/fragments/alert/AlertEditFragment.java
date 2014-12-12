package com.tradehero.th.fragments.alert;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertFormDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.persistence.alert.AlertCacheRx;
import javax.inject.Inject;
import rx.Observable;
import rx.android.observables.AndroidObservable;

public class AlertEditFragment extends BaseAlertEditFragment
{
    private static final String BUNDLE_KEY_ALERT_ID_BUNDLE = BaseAlertEditFragment.class.getName() + ".alertId";

    protected AlertId alertId;
    @Inject protected AlertCacheRx alertCache;

    public static void putAlertId(@NonNull Bundle args, @NonNull AlertId alertId)
    {
        args.putBundle(BUNDLE_KEY_ALERT_ID_BUNDLE, alertId.getArgs());
    }

    @NonNull public static AlertId getAlertId(@NonNull Bundle args)
    {
        return new AlertId(args.getBundle(BUNDLE_KEY_ALERT_ID_BUNDLE));
    }

    @Override public void onResume()
    {
        super.onResume();
        linkWith(getAlertId(getArguments()));
    }

    @Override @NonNull protected Observable<AlertCompactDTO> saveAlertProperRx(AlertFormDTO alertFormDTO)
    {
        return alertServiceWrapper.get().updateAlertRx(
                alertId,
                alertFormDTO);
    }

    protected void linkWith(AlertId alertId)
    {
        this.alertId = alertId;
        subscriptions.add(AndroidObservable.bindFragment(this, alertCache.get(alertId))
                .subscribe(
                        pair -> linkWith(pair.second),
                        error -> THToast.show(new THException(error))));
    }

    protected void displayActionBarTitle()
    {
        setActionBarTitle(R.string.stock_alert_edit_alert);
    }
}
