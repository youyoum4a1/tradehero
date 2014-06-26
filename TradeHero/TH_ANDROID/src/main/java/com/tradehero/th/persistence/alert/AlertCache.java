package com.tradehero.th.persistence.alert;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.network.service.AlertServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class AlertCache extends StraightDTOCacheNew<AlertId, AlertDTO>
{
    public static final int DEFAULT_MAX_SIZE = 100;

    @NotNull private final Lazy<AlertServiceWrapper> alertServiceWrapper;
    @NotNull private final Lazy<AlertCompactCache> alertCompactCache;

    //<editor-fold desc="Constructors">
    @Inject public AlertCache(
            @NotNull Lazy<AlertServiceWrapper> alertServiceWrapper,
            @NotNull Lazy<AlertCompactCache> alertCompactCache)
    {
        super(DEFAULT_MAX_SIZE);
        this.alertServiceWrapper = alertServiceWrapper;
        this.alertCompactCache = alertCompactCache;
    }
    //</editor-fold>

    @Override @NotNull public AlertDTO fetch(@NotNull AlertId key) throws Throwable
    {
        return this.alertServiceWrapper.get().getAlert(key);
    }

    @Override @Nullable public AlertDTO put(@NotNull AlertId key, @NotNull AlertDTO value)
    {
        alertCompactCache.get().put(key, value);
        return super.put(key, value);
    }
}
