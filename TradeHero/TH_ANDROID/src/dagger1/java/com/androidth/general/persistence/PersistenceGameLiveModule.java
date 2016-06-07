package com.androidth.general.persistence;

import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.persistence.kyc.KYCFormOptionsCache;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

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
    @Provides @Singleton DTOCacheUtilRx provideDTOCacheUtilRx(DTOCacheUtilImpl dtoCacheUtil)
    {
        return dtoCacheUtil;
    }
}