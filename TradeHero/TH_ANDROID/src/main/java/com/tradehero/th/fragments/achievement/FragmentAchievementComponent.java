package com.tradehero.th.fragments.achievement;

import com.tradehero.th.fragments.level.LevelUpDialogFragment;
import com.tradehero.th.widget.UserLevelProgressBar;
import dagger.Component;

@Component
public interface FragmentAchievementComponent
{
    void injectUserLevelProgressBar(UserLevelProgressBar userLevelProgressBar);
    void injectAchievementDialogFragment(AchievementDialogFragment achievementDialogFragment);
    void injectQuestDialogFragment();
    void injectAchievementListFragment(AchievementListFragment achievementListFragment);
    void injectAchievementCellView(AchievementCellView achievementCellView);
    void injectLevelUpDialogFragment(LevelUpDialogFragment levelUpDialogFragment);
}
