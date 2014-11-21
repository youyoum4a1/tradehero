package com.tradehero.th.persistence.security;

import android.support.annotation.NonNull;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityIntegerId;
import com.tradehero.th.api.security.SecurityIntegerIdList;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rx.Observable;

public class SecurityMultiFetchAssistant
{
    @NonNull private final SecurityIdCache securityIdCache;
    @NonNull private final SecurityCompactCacheRx securityCompactCache;
    @NonNull private final SecurityServiceWrapper securityServiceWrapper;

    //<editor-fold desc="Constructors">
    public SecurityMultiFetchAssistant(
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

    public Observable<SecurityCompactDTO> getCachedSecurities(List<SecurityIntegerId> keysToFetch)
    {
        return Observable.from(keysToFetch)
                .flatMap(securityIntegerId -> securityIdCache.getFirstOrEmpty(securityIntegerId))
                .map(pair -> pair.second)
                .flatMap(securityId -> securityCompactCache.getFirstOrEmpty(securityId))
                .map(pair -> pair.second);
    }
}
