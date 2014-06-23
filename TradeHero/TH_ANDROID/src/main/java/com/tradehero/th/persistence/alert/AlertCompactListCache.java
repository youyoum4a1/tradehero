package com.tradehero.th.persistence.alert;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertIdList;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.AlertServiceWrapper;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class AlertCompactListCache extends StraightDTOCacheNew<UserBaseKey, AlertIdList>
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

    @Override @Nullable public AlertIdList fetch(@NotNull UserBaseKey key) throws Throwable
    {
        return putInternal(key, alertServiceWrapper.getAlerts(key));
    }

    @Contract("_, null -> null; _, !null -> !null")
    protected AlertIdList putInternal(@NotNull UserBaseKey key, @Nullable List<AlertCompactDTO> fleshedValues)
    {
        AlertIdList alertIds = null;
        if (fleshedValues != null)
        {
            alertIds = new AlertIdList(key, fleshedValues);
            //alertCompactCache.invalidateAll();
            alertCompactCache.put(key, fleshedValues);
            put(key, alertIds);
        }
        return alertIds;
    }

    @Override public void invalidateAll()
    {
        super.invalidateAll();
    }
}
