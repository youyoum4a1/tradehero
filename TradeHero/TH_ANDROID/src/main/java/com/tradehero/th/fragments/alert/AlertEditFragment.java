package com.tradehero.th.fragments.alert;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuInflater;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.th.R;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertFormDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.persistence.alert.AlertCacheRx;
import javax.inject.Inject;
import rx.Observable;

public class AlertEditFragment extends BaseAlertEditFragment
{
    private static final String BUNDLE_KEY_ALERT_ID_BUNDLE = BaseAlertEditFragment.class.getName() + ".alertId";

    @Inject protected AlertCacheRx alertCache;

    protected AlertId alertId;

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
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarTitle(R.string.stock_alert_edit_alert);
    }

    @NonNull @Override protected Observable<AlertDTO> getAlertObservable()
    {
        return alertCache.getOne(alertId)
            .map(new PairGetSecond<AlertId, AlertDTO>());
    }

    @NonNull @Override protected Observable<AlertCompactDTO> saveAlertProperRx(AlertFormDTO alertFormDTO)
    {
        return alertServiceWrapper.get().updateAlertRx(
                alertId,
                alertFormDTO);
    }
}
