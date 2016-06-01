package com.ayondo.academy.persistence;

import com.tradehero.common.persistence.DTOCacheUtilRx;
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
