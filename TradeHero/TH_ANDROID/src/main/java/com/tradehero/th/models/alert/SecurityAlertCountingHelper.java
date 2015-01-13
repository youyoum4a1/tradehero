package com.tradehero.th.models.alert;

import android.support.annotation.NonNull;
import com.tradehero.th.api.system.SystemStatusKey;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.alert.AlertCompactListCacheRx;
import com.tradehero.th.persistence.system.SystemStatusCache;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import javax.inject.Inject;
import rx.Observable;

public class SecurityAlertCountingHelper
{
    @NonNull private final UserProfileCacheRx userProfileCache;
    @NonNull private final AlertCompactListCacheRx alertCompactListCache;
    @NonNull private final SystemStatusCache systemStatusCache;

    //<editor-fold desc="Constructors">
    @Inject public SecurityAlertCountingHelper(
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull AlertCompactListCacheRx alertCompactListCache,
            @NonNull SystemStatusCache systemStatusCache)
    {
        this.userProfileCache = userProfileCache;
        this.alertCompactListCache = alertCompactListCache;
        this.systemStatusCache = systemStatusCache;
    }
    //</editor-fold>

    @NonNull public Observable<AlertSlotDTO> getAlertSlots(@NonNull UserBaseKey userBaseKey)
    {
        return Observable.zip(
                userProfileCache.get(userBaseKey),
                alertCompactListCache.get(userBaseKey),
                systemStatusCache.get(new SystemStatusKey()),
                (u, a, s) -> new AlertSlotDTO(u.second, a.second, s.second));
    }
}
