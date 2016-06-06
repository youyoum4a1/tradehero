package com.androidth.general.persistence.security;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.SystemCache;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.api.security.SecurityId;
import com.androidth.general.api.security.SecurityIntegerId;
import com.androidth.general.network.service.SecurityServiceWrapper;
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
                .map(new Func1<SecurityCompactDTO, SecurityId>()
                {
                    @Override public SecurityId call(SecurityCompactDTO securityCompactDTO)
                    {
                        SecurityId securityId  = securityCompactDTO.getSecurityId();
                        securityCompactCache.get().onNext(securityId, securityCompactDTO);
                        return securityId;
                    }
                });
    }
}
