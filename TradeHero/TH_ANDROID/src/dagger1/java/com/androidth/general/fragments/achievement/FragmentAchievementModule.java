package com.androidth.general.fragments.achievement;

import com.androidth.general.fragments.level.LevelUpDialogFragment;
import com.androidth.general.widget.UserLevelProgressBar;
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
