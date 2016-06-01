package com.ayondo.academy.fragments.achievement;

import com.ayondo.academy.fragments.level.LevelUpDialogFragment;
import com.ayondo.academy.widget.UserLevelProgressBar;
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
