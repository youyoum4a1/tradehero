package com.tradehero.th.utils.dagger;

import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import com.tradehero.th.fragments.achievement.AchievementCellView;
import com.tradehero.th.fragments.achievement.AchievementListFragment;
import com.tradehero.th.fragments.achievement.AchievementListTestingFragment;
import com.tradehero.th.fragments.achievement.QuestDialogFragment;
import com.tradehero.th.fragments.achievement.QuestListTestingFragment;
import com.tradehero.th.persistence.achievement.UserAchievementCache;
import com.tradehero.th.utils.achievement.ForAchievement;
import com.tradehero.th.fragments.achievement.AchievementDialogFragment;
import com.tradehero.th.widget.UserLevelProgressBar;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
        injects = {
                UserLevelProgressBar.class,
                AchievementDialogFragment.class,
                QuestDialogFragment.class,
                AchievementListFragment.class,
                AchievementCellView.class,
                AchievementListTestingFragment.class,
                QuestListTestingFragment.class,

        },
        complete = false,
        library = true
)
public class AchievementModule
{
    @Provides @Singleton LocalBroadcastManager providesLocalBroadcastReceiver(Context context)
    {
        return LocalBroadcastManager.getInstance(context);
    }

    @Provides @ForAchievement IntentFilter providesIntentFilterAchievement()
    {
        return new IntentFilter(UserAchievementCache.INTENT_ACTION_NAME);
    }
}
