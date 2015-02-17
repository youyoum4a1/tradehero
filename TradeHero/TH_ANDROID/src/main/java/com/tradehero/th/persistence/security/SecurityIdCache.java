package com.tradehero.th.persistence.security;

import android.support.annotation.NonNull;
import android.util.Pair;
import com.tradehero.common.persistence.ActionOnNextCache;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.SystemCache;
import com.tradehero.common.rx.PairGetFirst;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIntegerId;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import rx.functions.Func1;

@Singleton @SystemCache
public class SecurityIdCache extends BaseFetchDTOCacheRx<SecurityIntegerId, SecurityId>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 2000;

    @NonNull private final SecurityServiceWrapper securityServiceWrapper;
    @NonNull private final Lazy<SecurityCompactCacheRx> securityCompactCache;

    //<editor-fold desc="Constructors">
    @Inject public SecurityIdCache(
            @NonNull SecurityServiceWrapper securityServiceWrapper,
            @NonNull Lazy<SecurityCompactCacheRx> securityCompactCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.securityServiceWrapper = securityServiceWrapper;
        this.securityCompactCache = securityCompactCache;
    }
    //</editor-fold>

    @NonNull @Override protected Observable<SecurityId> fetch(@NonNull SecurityIntegerId key)
    {
        return securityServiceWrapper.getSecurityRx(key)
                .map(new Func1<SecurityCompactDTO, Pair<SecurityId, SecurityCompactDTO>>()
                {
                    @Override public Pair<SecurityId, SecurityCompactDTO> call(SecurityCompactDTO securityCompactDTO)
                    {
                        return Pair.create(securityCompactDTO.getSecurityId(), securityCompactDTO);
                    }
                })
                .doOnNext(new ActionOnNextCache<>(securityCompactCache.get()))
                .map(new PairGetFirst<SecurityId, SecurityCompactDTO>());
    }
}
