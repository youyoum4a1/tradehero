package com.ayondo.academy.persistence;

import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.ayondo.academy.persistence.kyc.KYCFormOptionsCache;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
        includes = {
        },
        injects = {
                KYCFormOptionsCache.class, // This is a HACK to ensure that the cache instance is a singleton...
        },
        complete = false,
        library = true
)
public class PersistenceGameLiveModule
{
    @Provides @Singleton DTOCacheUtilRx provideDTOCacheUtilRx(DTOCacheUtilLiveImpl dtoCacheUtil)
    {
        return dtoCacheUtil;
    }
}
