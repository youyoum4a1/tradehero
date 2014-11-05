package com.tradehero.th.fragments.alert;

import android.os.Bundle;
import android.util.Pair;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertFormDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.persistence.alert.AlertCacheRx;
import javax.inject.Inject;
import android.support.annotation.NonNull;
import rx.Observer;
import rx.android.observables.AndroidObservable;
import timber.log.Timber;

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
        linkWith(getAlertId(getArguments()), true);
    }

    @Override protected void saveAlertProper(AlertFormDTO alertFormDTO)
    {
        AndroidObservable.bindFragment(this, alertServiceWrapper.get().updateAlertRx(
                alertId,
                alertFormDTO))
                .subscribe(createAlertUpdateObserver());
    }

    protected void linkWith(AlertId alertId, boolean andDisplay)
    {
        this.alertId = alertId;
        AndroidObservable.bindFragment(this, alertCache.get(alertId))
                .subscribe(createAlertCacheObserver());
        if (andDisplay)
        {
        }
    }

    protected void displayActionBarTitle()
    {
        setActionBarTitle(R.string.stock_alert_edit_alert);
    }

    protected Observer<Pair<AlertId, AlertDTO>> createAlertCacheObserver()
    {
        return new AlertEditFragmentAlertCacheObserver();
    }

    protected class AlertEditFragmentAlertCacheObserver implements Observer<Pair<AlertId, AlertDTO>>
    {
        @Override public void onNext(Pair<AlertId, AlertDTO> pair)
        {
            linkWith(pair.second, true);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(new THException(e));
            Timber.e("Failed to get alertDTO", e);
        }
    }
}
