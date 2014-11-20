package com.tradehero.th.models.alert;

import android.support.annotation.NonNull;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.alert.AlertCompactListCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.MakePairFunc2;
import javax.inject.Inject;
import rx.Observable;

public class SecurityAlertCountingHelper
{
    @NonNull private final UserProfileCacheRx userProfileCache;
    @NonNull private final AlertCompactListCacheRx alertCompactListCache;

    //<editor-fold desc="Constructors">
    @Inject public SecurityAlertCountingHelper(
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull AlertCompactListCacheRx alertCompactListCache)
    {
        this.userProfileCache = userProfileCache;
        this.alertCompactListCache = alertCompactListCache;
    }
    //</editor-fold>

    public Observable<AlertSlotDTO> getAlertSlots(UserBaseKey userBaseKey)
    {
        return Observable.zip(
                userProfileCache.get(userBaseKey),
                alertCompactListCache.get(userBaseKey),
                new MakePairFunc2<>())
            .map(pair -> new AlertSlotDTO(pair.first.second, pair.second.second));
    }
}
