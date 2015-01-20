package com.tradehero.th.persistence.alert;

import android.support.annotation.NonNull;
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
import rx.Observable;
import timber.log.Timber;

@Singleton @UserCache
public class AlertCompactListCacheRx extends BaseFetchDTOCacheRx<UserBaseKey, AlertCompactDTOList>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 50;

    @NonNull private final AlertServiceWrapper alertServiceWrapper;
    @NonNull private final AlertCompactCacheRx alertCompactCache;

    //<editor-fold desc="Constructors">
    @Inject public AlertCompactListCacheRx(
            @NonNull AlertServiceWrapper alertServiceWrapper,
            @NonNull AlertCompactCacheRx alertCompactCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.alertServiceWrapper = alertServiceWrapper;
        this.alertCompactCache = alertCompactCache;
    }
    //</editor-fold>

    @Override @NonNull public Observable<AlertCompactDTOList> fetch(@NonNull UserBaseKey key)
    {
        return alertServiceWrapper.getAlertsRx(key);
    }

    @Override public void onNext(@NonNull UserBaseKey key, @NonNull AlertCompactDTOList value)
    {
        alertCompactCache.onNext(key, value);
        super.onNext(key, value);
    }

    public Observable<Map<SecurityId, AlertId>> getSecurityMappedAlerts(@NonNull UserBaseKey userBaseKey)
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
