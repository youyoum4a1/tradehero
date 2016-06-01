package com.ayondo.academy.fragments.alert;

import android.app.Activity;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.tradehero.common.rx.PairGetSecond;
import com.ayondo.academy.api.alert.AlertCompactDTO;
import com.ayondo.academy.api.alert.AlertDTO;
import com.ayondo.academy.api.alert.AlertFormDTO;
import com.ayondo.academy.api.alert.AlertId;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.models.alert.SecurityAlertCountingHelper;
import com.ayondo.academy.network.service.AlertServiceWrapper;
import com.ayondo.academy.network.service.QuoteServiceWrapper;
import com.ayondo.academy.persistence.alert.AlertCacheRx;
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
