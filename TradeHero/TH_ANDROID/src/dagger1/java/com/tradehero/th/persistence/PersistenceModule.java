package com.tradehero.th.persistence;

import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.th.persistence.prefs.PreferenceModule;
import dagger.Module;
import dagger.Provides;

@Module(
        includes = {
                PreferenceModule.class,
                PersistenceGameLiveModule.class,
        },
        complete = false,
        library = true
)
public class PersistenceModule
{
    @Provides DTOCacheUtilRx provideDTOCacheUtilRx(DTOCacheUtilImpl dtoCacheUtil)
    {
        return dtoCacheUtil;
    }
}
