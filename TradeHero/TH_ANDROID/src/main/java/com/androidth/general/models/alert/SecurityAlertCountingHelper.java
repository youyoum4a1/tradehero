package com.androidth.general.models.alert;

import android.support.annotation.NonNull;
import android.util.Pair;
import com.androidth.general.api.alert.AlertCompactDTOList;
import com.androidth.general.api.system.SystemStatusDTO;
import com.androidth.general.api.system.SystemStatusKey;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.billing.ProductIdentifierDomain;
import com.androidth.general.billing.THBillingInteractorRx;
import com.androidth.general.persistence.alert.AlertCompactListCacheRx;
import com.androidth.general.persistence.system.SystemStatusCache;
import com.androidth.general.persistence.user.UserProfileCacheRx;
import com.androidth.general.rx.ReplaceWithFunc1;
import javax.inject.Inject;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func3;

public class SecurityAlertCountingHelper
{
    @NonNull private final UserProfileCacheRx userProfileCache;
    @NonNull private final AlertCompactListCacheRx alertCompactListCache;
    @NonNull private final SystemStatusCache systemStatusCache;
    @NonNull private final THBillingInteractorRx userInteractorRx;

    //<editor-fold desc="Constructors">
    @Inject public SecurityAlertCountingHelper(
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull AlertCompactListCacheRx alertCompactListCache,
            @NonNull SystemStatusCache systemStatusCache,
            @NonNull THBillingInteractorRx userInteractorRx)
    {
        this.userProfileCache = userProfileCache;
        this.alertCompactListCache = alertCompactListCache;
        this.systemStatusCache = systemStatusCache;
        this.userInteractorRx = userInteractorRx;
    }
    //</editor-fold>

    @NonNull public Observable<AlertSlotDTO> getAlertSlotsOrPurchase(@NonNull UserBaseKey userBaseKey)
    {
        return getAlertSlots(userBaseKey)
                .take(1)
                .flatMap(new Func1<AlertSlotDTO, Observable<AlertSlotDTO>>()
                {
                    @Override public Observable<AlertSlotDTO> call(AlertSlotDTO alertSlot)
                    {
                        if (alertSlot.freeAlertSlots <= 0)
                        {
                            //noinspection unchecked
                            return userInteractorRx.purchaseAndClear(ProductIdentifierDomain.DOMAIN_STOCK_ALERTS)
                                    .map(new ReplaceWithFunc1<>(alertSlot));
                        }
                        return Observable.just(alertSlot);
                    }
                });
    }

    @NonNull public Observable<AlertSlotDTO> getAlertSlots(@NonNull UserBaseKey userBaseKey)
    {
        return Observable.zip(
                userProfileCache.get(userBaseKey),
                alertCompactListCache.get(userBaseKey),
                systemStatusCache.get(new SystemStatusKey()),
                new Func3<Pair<UserBaseKey, UserProfileDTO>, Pair<UserBaseKey, AlertCompactDTOList>, Pair<SystemStatusKey, SystemStatusDTO>, AlertSlotDTO>()
                {
                    @Override public AlertSlotDTO call(Pair<UserBaseKey, UserProfileDTO> u,
                            Pair<UserBaseKey, AlertCompactDTOList> a,
                            Pair<SystemStatusKey, SystemStatusDTO> s)
                    {
                        return new AlertSlotDTO(u.second, a.second, s.second);
                    }
                });
    }
}
