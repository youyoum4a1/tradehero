package com.androidth.general.fragments.alert;

import android.app.Activity;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.androidth.general.common.rx.PairGetSecond;
import com.androidth.general.api.alert.AlertCompactDTO;
import com.androidth.general.api.alert.AlertDTO;
import com.androidth.general.api.alert.AlertFormDTO;
import com.androidth.general.api.alert.AlertId;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.models.alert.SecurityAlertCountingHelper;
import com.androidth.general.network.service.AlertServiceWrapper;
import com.androidth.general.network.service.QuoteServiceWrapper;
import com.androidth.general.persistence.alert.AlertCacheRx;
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
            @NonNull QuoteServiceWrapper quoteServiceWrapper,
            @NonNull AlertCacheRx alertCache,
            @NonNull Lazy<AlertServiceWrapper> alertServiceWrapper,
            @NonNull AlertId alertId)
    {
        super(activity, resources, currentUserId, securityAlertCountingHelper, quoteServiceWrapper);
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
