package com.tradehero.th.utils.level;

import android.content.IntentFilter;
import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
        },
        complete = false,
        library = true
)
public class XpModule
{
    public static final String XP_INTENT_ACTION_NAME = "com.tradehero.th.level.xp.ALERT";
    public static final String KEY_XP_BROADCAST = XpModule.class.getName()+".xpBroadcast";
    public static final String KEY_XP_NODE = "xp";

    @Provides @ForXP IntentFilter providesIntentFilterAchievement()
    {
        return new IntentFilter(XP_INTENT_ACTION_NAME);
    }
}
