package com.tradehero.th.persistence.alert;

import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.th.api.alert.AlertCompactDTOList;
import com.tradehero.th.api.alert.AlertIdList;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.AlertServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class AlertCompactListCache extends StraightCutDTOCacheNew<UserBaseKey, AlertCompactDTOList, AlertIdList>
{
    public static final int DEFAULT_MAX_SIZE = 50;

    @NotNull private final AlertServiceWrapper alertServiceWrapper;
    @NotNull private final AlertCompactCache alertCompactCache;

    //<editor-fold desc="Constructors">
    @Inject public AlertCompactListCache(
            @NotNull AlertServiceWrapper alertServiceWrapper,
            @NotNull AlertCompactCache alertCompactCache)
    {
        super(DEFAULT_MAX_SIZE);
        this.alertServiceWrapper = alertServiceWrapper;
        this.alertCompactCache = alertCompactCache;
    }
    //</editor-fold>

    @Override @NotNull public AlertCompactDTOList fetch(@NotNull UserBaseKey key) throws Throwable
    {
        return alertServiceWrapper.getAlerts(key);
    }

    @NotNull @Override protected AlertIdList cutValue(@NotNull UserBaseKey key, @NotNull AlertCompactDTOList value)
    {
        alertCompactCache.put(key, value);
        return new AlertIdList(key, value);
    }

    @Nullable @Override protected AlertCompactDTOList inflateValue(@NotNull UserBaseKey key, @Nullable AlertIdList cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }
        @NotNull AlertCompactDTOList value = alertCompactCache.get(cutValue);
        if (value.hasNullItem())
        {
            return null;
        }
        return null;
    }
}
