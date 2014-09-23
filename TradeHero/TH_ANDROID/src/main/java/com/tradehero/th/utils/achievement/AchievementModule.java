package com.tradehero.th.utils.achievement;

import android.content.IntentFilter;
import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
        },
        complete = false,
        library = true
)
public class AchievementModule {

    public static final String ACHIEVEMENT_INTENT_ACTION_NAME = "com.tradehero.th.achievement.ALERT";
    public static final String KEY_USER_ACHIEVEMENT_ID = AchievementModule.class.getName() + ".achievementId";
    public static final String KEY_ACHIEVEMENT_NODE = "achievements";

    @Provides @ForAchievement
    IntentFilter providesIntentFilterAchievement()
    {
        return new IntentFilter(ACHIEVEMENT_INTENT_ACTION_NAME);
    }
}
