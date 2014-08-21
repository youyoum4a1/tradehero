package com.tradehero.th.persistence;

import com.tradehero.th.persistence.achievement.PersistenceAchievementDebugModule;
import com.tradehero.th.persistence.level.PersistenceLevelDebugModule;
import dagger.Module;

@Module(
        includes = {
                PersistenceAchievementDebugModule.class,
                PersistenceLevelDebugModule.class
        },

        complete = false,
        library = true
)
public class PersistenceDebugModule
{
}
