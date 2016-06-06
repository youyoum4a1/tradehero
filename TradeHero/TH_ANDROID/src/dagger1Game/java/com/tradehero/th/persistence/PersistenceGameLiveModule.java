package com.androidth.general.persistence;

import com.androidth.general.common.persistence.DTOCacheUtilRx;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
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
