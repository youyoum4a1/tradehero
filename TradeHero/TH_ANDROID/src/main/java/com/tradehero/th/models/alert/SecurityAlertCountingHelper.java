package com.tradehero.th.models.alert;

import android.support.annotation.NonNull;
import android.util.Pair;
import com.tradehero.th.api.alert.AlertCompactDTOList;
import com.tradehero.th.api.system.SystemStatusDTO;
import com.tradehero.th.api.system.SystemStatusKey;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THBillingInteractorRx;
import com.tradehero.th.persistence.alert.AlertCompactListCacheRx;
import com.tradehero.th.persistence.system.SystemStatusCache;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.ReplaceWith;
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
                                    .map(new ReplaceWith<>(alertSlot));
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
