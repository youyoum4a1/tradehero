package com.tradehero.th.utils.dagger;

import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.tradehero.th.persistence.achievement.UserAchievementCache;
import com.tradehero.th.utils.achievement.ForAchievement;

import javax.inject.Singleton;

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
