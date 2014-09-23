package com.tradehero.th;

import com.tradehero.th.fragments.achievement.ForAchievementListFragment;
import com.tradehero.th.fragments.achievement.ForAchievementListTestingFragment;
import com.tradehero.th.fragments.achievement.ForQuestListTestingFragment;
import com.tradehero.th.fragments.level.ForXpTestingFragment;

public interface BuildTypeUIModuleExpectation
{
    // Annotations are here only as hints
    @ForAchievementListFragment Class provideAchievementListFragmentClass();
    @ForAchievementListTestingFragment Class provideAchievementListTestingFragmentClass();
    @ForQuestListTestingFragment Class provideQuestListTestingFragmentClass();
    @ForXpTestingFragment Class provideXpTestingFragmentClass();
}
