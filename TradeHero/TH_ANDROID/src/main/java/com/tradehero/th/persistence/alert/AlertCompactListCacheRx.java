package com.tradehero.th.persistence.alert;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.common.utils.CollectionUtils;
import com.tradehero.th.api.alert.AlertCompactDTOList;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.AlertServiceWrapper;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;
import timber.log.Timber;

@Singleton @UserCache
public class AlertCompactListCacheRx extends BaseFetchDTOCacheRx<UserBaseKey, AlertCompactDTOList>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 50;
    public static final int DEFAULT_MAX_SUBJECt_SIZE = 5;

    @NotNull private final AlertServiceWrapper alertServiceWrapper;
    @NotNull private final AlertCompactCacheRx alertCompactCache;

    //<editor-fold desc="Constructors">
    @Inject public AlertCompactListCacheRx(
            @NotNull AlertServiceWrapper alertServiceWrapper,
            @NotNull AlertCompactCacheRx alertCompactCache,
            @NotNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECt_SIZE, DEFAULT_MAX_SUBJECt_SIZE, dtoCacheUtil);
        this.alertServiceWrapper = alertServiceWrapper;
        this.alertCompactCache = alertCompactCache;
    }
    //</editor-fold>

    @Override @NotNull public Observable<AlertCompactDTOList> fetch(@NotNull UserBaseKey key)
    {
        return alertServiceWrapper.getAlertsRx(key);
    }

    @Override public void onNext(@NotNull UserBaseKey key, @NotNull AlertCompactDTOList value)
    {
        alertCompactCache.onNext(key, value);
        super.onNext(key, value);
    }

    public Observable<Map<SecurityId, AlertId>> getSecurityMappedAlerts(@NotNull UserBaseKey userBaseKey)
    {
        return get(userBaseKey)
                .map(pair -> {
                    final Map<SecurityId, AlertId> securitiesWithAlerts = new HashMap<>();
                    CollectionUtils.apply(pair.second, alertCompactDTO -> {
                        if (alertCompactDTO.security != null)
                        {
                            securitiesWithAlerts.put(alertCompactDTO.security.getSecurityId(), alertCompactDTO.getAlertId(pair.first));
                        }
                        else
                        {
                            Timber.d("populate: AlertId %s had a null alertCompact of securityCompact", alertCompactDTO);
                        }
                    });
                    return securitiesWithAlerts;
                });
    }

}
