package com.tradehero.th.models.alert;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.alert.AlertCompactListCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.MakePairFunc2;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

public class SecurityAlertCountingHelper
{
    @NotNull private final UserProfileCacheRx userProfileCache;
    @NotNull private final AlertCompactListCacheRx alertCompactListCache;

    //<editor-fold desc="Constructors">
    @Inject public SecurityAlertCountingHelper(
            @NotNull UserProfileCacheRx userProfileCache,
            @NotNull AlertCompactListCacheRx alertCompactListCache)
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
            .map(pair -> {
                AlertSlotDTO alertSlots = new AlertSlotDTO();
                alertSlots.usedAlertSlots = pair.second.second == null ? 0 : pair.second.second.size();
                alertSlots.totalAlertSlots = pair.first.second == null ? 0 : pair.first.second.getUserAlertPlansAlertCount();
                alertSlots.freeAlertSlots = alertSlots.totalAlertSlots - alertSlots.usedAlertSlots;
                return alertSlots;
            });
    }
}
