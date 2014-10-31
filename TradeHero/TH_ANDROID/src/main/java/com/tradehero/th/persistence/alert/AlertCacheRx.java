package com.tradehero.th.persistence.alert;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.network.service.AlertServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton @UserCache
public class AlertCacheRx extends BaseFetchDTOCacheRx<AlertId, AlertDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 100;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 10;

    @NotNull private final Lazy<AlertServiceWrapper> alertServiceWrapper;
    @NotNull private final Lazy<AlertCompactCacheRx> alertCompactCache;

    //<editor-fold desc="Constructors">
    @Inject public AlertCacheRx(
            @NotNull Lazy<AlertServiceWrapper> alertServiceWrapper,
            @NotNull Lazy<AlertCompactCacheRx> alertCompactCache,
            @NotNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
        this.alertServiceWrapper = alertServiceWrapper;
        this.alertCompactCache = alertCompactCache;
    }
    //</editor-fold>

    @Override @NotNull public Observable<AlertDTO> fetch(@NotNull AlertId key)
    {
        return this.alertServiceWrapper.get().getAlertRx(key);
    }

    @Override public void onNext(@NotNull AlertId key, @NotNull AlertDTO value)
    {
        alertCompactCache.get().onNext(key, value);
        super.onNext(key, value);
    }
}
