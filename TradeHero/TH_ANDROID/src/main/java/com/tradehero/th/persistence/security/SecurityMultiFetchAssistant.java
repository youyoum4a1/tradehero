package com.tradehero.th.persistence.security;

import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityIntegerId;
import com.tradehero.th.api.security.SecurityIntegerIdList;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

public class SecurityMultiFetchAssistant
{
    @NotNull private final SecurityIdCache securityIdCache;
    @NotNull private final SecurityCompactCacheRx securityCompactCache;
    @NotNull private final SecurityServiceWrapper securityServiceWrapper;

    //<editor-fold desc="Constructors">
    public SecurityMultiFetchAssistant(
            @NotNull SecurityIdCache securityIdCache,
            @NotNull SecurityCompactCacheRx securityCompactCache,
            @NotNull SecurityServiceWrapper securityServiceWrapper)
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
