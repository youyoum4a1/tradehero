package com.tradehero.th.persistence.achievement;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
        includes = {},
        injects = {
                UserAchievementCacheDummy.class,
        },

        complete = false,
        library = true
)
public class PersistenceAchievementDebugModule
{
    @Provides @Singleton UserAchievementCache providerLevelDefListCache(UserAchievementCacheDummy userAchievementCacheDummy)
    {
        return userAchievementCacheDummy;
    }
}
