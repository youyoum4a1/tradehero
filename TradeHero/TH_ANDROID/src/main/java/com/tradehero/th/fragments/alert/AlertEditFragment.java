package com.tradehero.th.fragments.alert;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.th.R;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertFormDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.persistence.alert.AlertCacheRx;
import com.tradehero.th.rx.ToastOnErrorAction;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.functions.Action1;

public class AlertEditFragment extends BaseAlertEditFragment
{
    private static final String BUNDLE_KEY_ALERT_ID_BUNDLE = BaseAlertEditFragment.class.getName() + ".alertId";

    protected AlertId alertId;
    protected Subscription getAlertSubscription;
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

    @Override public void onStop()
    {
        unsubscribe(getAlertSubscription);
        getAlertSubscription = null;
        super.onStop();
    }

    @NonNull @Override protected Observable<AlertCompactDTO> saveAlertProperRx(AlertFormDTO alertFormDTO)
    {
        return alertServiceWrapper.get().updateAlertRx(
                alertId,
                alertFormDTO);
    }

    protected void linkWith(@NonNull AlertId alertId)
    {
        this.alertId = alertId;
        unsubscribe(getAlertSubscription);
        getAlertSubscription = AppObservable.bindFragment(this, alertCache.get(alertId))
                .map(new PairGetSecond<AlertId, AlertDTO>())
                .subscribe(
                        new Action1<AlertDTO>()
                        {
                            @Override public void call(AlertDTO alert)
                            {
                                linkWith(alert);
                            }
                        },
                        new ToastOnErrorAction());
    }

    protected void displayActionBarTitle()
    {
        setActionBarTitle(R.string.stock_alert_edit_alert);
    }
}
