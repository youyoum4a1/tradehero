package com.androidth.general.persistence.security;

import android.support.annotation.NonNull;

import com.androidth.general.api.competition.key.BasicProviderSecurityV2ListType;
import com.androidth.general.api.security.SecurityCompositeDTO;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.network.service.SecurityServiceWrapper;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Lazy;
import rx.Observable;

@Singleton @UserCache
public class SecurityCompositeListCacheRx extends BaseFetchDTOCacheRx<
        BasicProviderSecurityV2ListType,
        SecurityCompositeDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 50;

    @NonNull private final Lazy<SecurityServiceWrapper> securityServiceWrapper;
    @NonNull private final Lazy<SecurityCompactCacheRx> securityCompactCache;

    //<editor-fold desc="Constructors">
    @Inject protected SecurityCompositeListCacheRx(
            @NonNull Lazy<SecurityServiceWrapper> securityServiceWrapper,
            @NonNull Lazy<SecurityCompactCacheRx> securityCompactCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.securityServiceWrapper = securityServiceWrapper;
        this.securityCompactCache = securityCompactCache;
    }
    //</editor-fold>

    @NonNull @Override protected Observable<SecurityCompositeDTO> fetch(@NonNull BasicProviderSecurityV2ListType key)
    {
        return securityServiceWrapper.get().getSecuritiesV2Rx(key);
    }

    @Override public void onNext(@NonNull BasicProviderSecurityV2ListType key, @NonNull SecurityCompositeDTO value)
    {
        securityCompactCache.get().onNext(value);
        super.onNext(key, value);
    }
}
