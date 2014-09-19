package com.tradehero.th.utils.achievement;

import android.content.IntentFilter;
import com.tradehero.th.persistence.achievement.UserAchievementCache;
import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
        },
        complete = false,
        library = true
)
public class AchievementModule {

    @Provides @ForAchievement
    IntentFilter providesIntentFilterAchievement()
    {
        return new IntentFilter(UserAchievementCache.INTENT_ACTION_NAME);
    }
}
