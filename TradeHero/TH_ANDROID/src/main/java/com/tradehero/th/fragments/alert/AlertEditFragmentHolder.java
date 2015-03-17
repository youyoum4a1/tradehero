package com.tradehero.th.fragments.alert;

import android.app.Activity;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertFormDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.models.alert.SecurityAlertCountingHelper;
import com.tradehero.th.network.service.AlertServiceWrapper;
import com.tradehero.th.persistence.alert.AlertCacheRx;
import dagger.Lazy;
import rx.Observable;

public class AlertEditFragmentHolder extends BaseAlertEditFragmentHolder
{
    @NonNull protected final AlertCacheRx alertCache;
    @NonNull protected final Lazy<AlertServiceWrapper> alertServiceWrapper;
    @NonNull protected final AlertId alertId;

    //<editor-fold desc="Constructors">
    public AlertEditFragmentHolder(
            @NonNull Activity activity,
            @NonNull Resources resources,
            @NonNull CurrentUserId currentUserId,
            @NonNull SecurityAlertCountingHelper securityAlertCountingHelper,
            @NonNull AlertCacheRx alertCache,
            @NonNull Lazy<AlertServiceWrapper> alertServiceWrapper,
            @NonNull AlertId alertId)
    {
        super(activity, resources, currentUserId, securityAlertCountingHelper);
        this.alertCache = alertCache;
        this.alertServiceWrapper = alertServiceWrapper;
        this.alertId = alertId;
    }
    //</editor-fold>

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
