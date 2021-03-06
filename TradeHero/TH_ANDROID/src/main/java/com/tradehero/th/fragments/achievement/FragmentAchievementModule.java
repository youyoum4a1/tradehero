package com.tradehero.th.fragments.achievement;

import com.tradehero.th.fragments.level.LevelUpDialogFragment;
import com.tradehero.th.widget.UserLevelProgressBar;
import dagger.Module;

@Module(
        injects = {
                UserLevelProgressBar.class,
                AchievementDialogFragment.class,
                QuestDialogFragment.class,
                AchievementListFragment.class,
                AchievementCellView.class,
                LevelUpDialogFragment.class,
        },
        complete = false,
        library = true
)
public class FragmentAchievementModule
{
}
