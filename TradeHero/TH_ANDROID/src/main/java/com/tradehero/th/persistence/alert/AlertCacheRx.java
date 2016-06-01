package com.ayondo.academy.persistence.alert;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.api.alert.AlertDTO;
import com.ayondo.academy.api.alert.AlertId;
import com.ayondo.academy.network.service.AlertServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class AlertCacheRx extends BaseFetchDTOCacheRx<AlertId, AlertDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 100;

    @NonNull private final Lazy<AlertServiceWrapper> alertServiceWrapper;
    @NonNull private final Lazy<AlertCompactCacheRx> alertCompactCache;

    //<editor-fold desc="Constructors">
    @Inject public AlertCacheRx(
            @NonNull Lazy<AlertServiceWrapper> alertServiceWrapper,
            @NonNull Lazy<AlertCompactCacheRx> alertCompactCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.alertServiceWrapper = alertServiceWrapper;
        this.alertCompactCache = alertCompactCache;
    }
    //</editor-fold>

    @Override @NonNull public Observable<AlertDTO> fetch(@NonNull AlertId key)
    {
        return this.alertServiceWrapper.get().getAlertRx(key);
    }

    @Override public void onNext(@NonNull AlertId key, @NonNull AlertDTO value)
    {
        alertCompactCache.get().onNext(key, value);
        super.onNext(key, value);
    }
}
