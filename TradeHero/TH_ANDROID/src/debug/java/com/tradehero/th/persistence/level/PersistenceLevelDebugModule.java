package com.tradehero.th.persistence.level;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
        includes = {},
        injects = {
                LevelDefListCacheDummy.class,
        },

        complete = false,
        library = true
)
public class PersistenceLevelDebugModule
{
    @Provides @Singleton LevelDefListCache providerLevelDefListCache(LevelDefListCacheDummy levelDefListCacheDummy)
    {
        return levelDefListCacheDummy;
    }
}
