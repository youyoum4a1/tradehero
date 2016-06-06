package com.androidth.general.persistence.alert;

import android.support.annotation.NonNull;
import android.util.Pair;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.common.utils.CollectionUtils;
import com.androidth.general.api.alert.AlertCompactDTO;
import com.androidth.general.api.alert.AlertCompactDTOList;
import com.androidth.general.api.alert.AlertId;
import com.androidth.general.api.security.SecurityId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.network.service.AlertServiceWrapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
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

    public void remove(@NonNull AlertId alertId)
    {
        AlertCompactDTOList list = getCachedValue(alertId.getUserBaseKey());
        if (list != null)
        {
            for (AlertCompactDTO dto : new ArrayList<>(list))
            {
                if (dto.id == alertId.alertId)
                {
                    list.remove(dto);
                }
            }
            putValue(alertId.getUserBaseKey(), list);
        }
    }

    public void addCreated(@NonNull UserBaseKey userBaseKey, @NonNull AlertCompactDTO alertCompactDTO)
    {
        AlertCompactDTOList list = getCachedValue(userBaseKey);
        if (list == null)
        {
            list = new AlertCompactDTOList();
        }
        else
        {
            remove(alertCompactDTO.getAlertId(userBaseKey));
        }
        list.add(alertCompactDTO);
        putValue(userBaseKey, list);
    }

    @NonNull public Observable<Map<SecurityId, AlertCompactDTO>> getSecurityMappedAlerts(@NonNull UserBaseKey userBaseKey)
    {
        return get(userBaseKey)
                .map(new Func1<Pair<UserBaseKey, AlertCompactDTOList>, Map<SecurityId, AlertCompactDTO>>()
                {
                    @Override public Map<SecurityId, AlertCompactDTO> call(final Pair<UserBaseKey, AlertCompactDTOList> pair)
                    {
                        return map(pair);
                    }
                });
    }

    @NonNull public Observable<Map<SecurityId, AlertCompactDTO>> getOneSecurityMappedAlerts(@NonNull UserBaseKey userBaseKey)
    {
        return getOne(userBaseKey)
                .map(new Func1<Pair<UserBaseKey, AlertCompactDTOList>, Map<SecurityId, AlertCompactDTO>>()
                {
                    @Override public Map<SecurityId, AlertCompactDTO> call(final Pair<UserBaseKey, AlertCompactDTOList> pair)
                    {
                        return map(pair);
                    }
                });
    }

    @NonNull private static Map<SecurityId, AlertCompactDTO> map(@NonNull final Pair<UserBaseKey, AlertCompactDTOList> pair)
    {
        final Map<SecurityId, AlertCompactDTO> securitiesWithAlerts = new HashMap<>();
        CollectionUtils.apply(pair.second, new Action1<AlertCompactDTO>()
        {
            @Override public void call(AlertCompactDTO alertCompactDTO)
            {
                if (alertCompactDTO.security != null)
                {
                    securitiesWithAlerts.put(alertCompactDTO.security.getSecurityId(), alertCompactDTO);
                }
                else
                {
                    Timber.d("populate: AlertId %s had a null alertCompact of securityCompact", alertCompactDTO);
                }
            }
        });
        return securitiesWithAlerts;
    }
}
