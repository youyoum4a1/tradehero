package com.androidth.general.network.service;

import android.support.annotation.NonNull;
import com.androidth.general.api.alert.AlertCompactDTO;
import com.androidth.general.api.alert.AlertCompactDTOList;
import com.androidth.general.api.alert.AlertDTO;
import com.androidth.general.api.alert.AlertFormDTO;
import com.androidth.general.api.alert.AlertId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.persistence.alert.AlertCacheRx;
import com.androidth.general.persistence.alert.AlertCompactListCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

@Singleton public class AlertServiceWrapper
{
    @NonNull private final AlertServiceRx alertServiceRx;
    @NonNull private final Lazy<AlertCompactListCacheRx> alertCompactListCache;
    @NonNull private final Lazy<AlertCacheRx> alertCache;

    //<editor-fold desc="Constructors">
    @Inject public AlertServiceWrapper(
            @NonNull AlertServiceRx alertServiceRx,
            @NonNull Lazy<AlertCompactListCacheRx> alertCompactListCache,
            @NonNull Lazy<AlertCacheRx> alertCache)
    {
        super();
        this.alertServiceRx = alertServiceRx;
        this.alertCompactListCache = alertCompactListCache;
        this.alertCache = alertCache;
    }
    //</editor-fold>

    //<editor-fold desc="Get Alerts">
    @NonNull public Observable<AlertCompactDTOList> getAlertsRx(@NonNull UserBaseKey userBaseKey)
    {
        return alertServiceRx.getAlerts(userBaseKey.key)
                .subscribeOn(Schedulers.io());//to avoid NetworkOnMainThreadException
    }
    //</editor-fold>

    private void basicCheck(@NonNull AlertId alertId)
    {
        if (alertId.userId == null)
        {
            throw new NullPointerException("alertId.userId cannot be null");
        }
        if (alertId.alertId == null)
        {
            throw new NullPointerException("alertId.alertId cannot be null");
        }
    }

    //<editor-fold desc="Get Alert">
    @NonNull public Observable<AlertDTO> getAlertRx(@NonNull AlertId alertId)
    {
        basicCheck(alertId);
        return this.alertServiceRx.getAlert(alertId.userId, alertId.alertId);
    }
    //</editor-fold>

    //<editor-fold desc="Create Alert">
    @NonNull public Observable<AlertCompactDTO> createAlertRx(@NonNull final UserBaseKey userBaseKey, @NonNull AlertFormDTO alertFormDTO)
    {
        return this.alertServiceRx.createAlert(userBaseKey.key, alertFormDTO)
                .doOnNext(new Action1<AlertCompactDTO>()
                {
                    @Override public void call(AlertCompactDTO alertCompactDTO)
                    {
                        alertCompactListCache.get().addCreated(userBaseKey, alertCompactDTO);
                        AlertCompactDTOList list = alertCompactListCache.get().getCachedValue(userBaseKey);
                        if (list != null)
                        {
                            alertCompactListCache.get().onNext(userBaseKey, list);
                        }
                    }
                });
    }
    //</editor-fold>

    //<editor-fold desc="Update Alert">
    @NonNull public Observable<AlertCompactDTO> updateAlertRx(@NonNull final AlertId alertId, @NonNull AlertFormDTO alertFormDTO)
    {
        basicCheck(alertId);
        alertCompactListCache.get().remove(alertId);
        return this.alertServiceRx.updateAlert(alertId.userId, alertId.alertId, alertFormDTO)
                .doOnNext(new Action1<AlertCompactDTO>()
                {
                    @Override public void call(AlertCompactDTO alertCompactDTO)
                    {
                        alertCache.get().invalidate(alertId);
                        alertCompactListCache.get().addCreated(alertId.getUserBaseKey(), alertCompactDTO);
                        AlertCompactDTOList list = alertCompactListCache.get().getCachedValue(alertId.getUserBaseKey());
                        if (list != null)
                        {
                            alertCompactListCache.get().onNext(alertId.getUserBaseKey(), list);
                        }
                    }
                });
    }
    //</editor-fold>
}
