package com.tradehero.th.persistence.security;

import android.support.annotation.NonNull;
import android.util.Pair;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIntegerId;
import com.tradehero.th.api.security.SecurityIntegerIdList;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import rx.Observable;

public class SecurityMultiFetchAssistant
{
    @NonNull private final SecurityIdCache securityIdCache;
    @NonNull private final SecurityCompactCacheRx securityCompactCache;
    @NonNull private final SecurityServiceWrapper securityServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public SecurityMultiFetchAssistant(
            @NonNull SecurityIdCache securityIdCache,
            @NonNull SecurityCompactCacheRx securityCompactCache,
            @NonNull SecurityServiceWrapper securityServiceWrapper)
    {
        super();
        this.securityIdCache = securityIdCache;
        this.securityCompactCache = securityCompactCache;
        this.securityServiceWrapper = securityServiceWrapper;
    }
    //</editor-fold>

    public Observable<Map<SecurityIntegerId, SecurityCompactDTO>> get(List<SecurityIntegerId> keysToFetch)
    {
        SecurityIntegerIdList remainingKeys = new SecurityIntegerIdList(keysToFetch, null);
        return getCachedSecurities(keysToFetch)
                .doOnNext(securityCompact -> remainingKeys.remove(securityCompact.getSecurityIntegerId()))
                .toList()
                .flatMap(cachedSecurityCompacts -> Observable.merge(
                        Observable.from(cachedSecurityCompacts),
                        securityServiceWrapper.getMultipleSecuritiesRx(remainingKeys)
                                .flatMap(map -> Observable.from(map.values()))))
                .toList()
                .map(securityCompacts -> {
                    Map<SecurityIntegerId, SecurityCompactDTO> map = new HashMap<>();
                    for (SecurityCompactDTO securityCompact : securityCompacts)
                    {
                        map.put(securityCompact.getSecurityIntegerId(), securityCompact);
                    }
                    return map;
                });
    }

    @NonNull public Observable<SecurityCompactDTO> getCachedSecurities(@NonNull List<SecurityIntegerId> keysToFetch)
    {
        // Otherwise it cannot infer
        //noinspection Convert2MethodRef
        return Observable.from(keysToFetch)
                .flatMap(securityIntegerId -> {
                    SecurityId cached = securityIdCache.getCachedValue(securityIntegerId);
                    if (cached != null)
                    {
                        return Observable.just(Pair.create(securityIntegerId, cached));
                    }
                    return securityIdCache.get(securityIntegerId).take(1);
                })
                //.flatMap(securityIntegerId -> securityIdCache.getFirstOrEmpty(securityIntegerId))
                .map(pair -> pair.second)
                .flatMap(securityId -> {
                    SecurityCompactDTO cached = securityCompactCache.getCachedValue(securityId);
                    if (cached != null)
                    {
                        return Observable.just(Pair.create(securityId, cached));
                    }
                    return securityCompactCache.get(securityId).take(1);
                })
                .map(pair -> pair.second);
    }
}
